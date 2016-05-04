package ppla5.handymanworkerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    private List<Order> orderList = new ArrayList<>();
    private Order order;
    private ArrayList<JSONObject> filtered;
    private String description;
    private String name;
    private SQLiteHandler sql;
    private SessionHandler session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sql = new SQLiteHandler(getApplicationContext());
        session = new SessionHandler(getApplicationContext());
        order = new Order();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        filtered = new ArrayList<>();
        mAdapter = new OrderAdapter(orderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        if(orderList.isEmpty()){
            Log.d("masuk", "true");
            getOrder();
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Order order = orderList.get(position);
                Toast.makeText(getApplicationContext(), order.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }
    public void prepareOrderData(ArrayList<JSONObject> orders) {
        Log.d("size: ",orders.size()+"");
        for (int i = 0; i <orders.size(); i++ ) {
            try {
                name = orders.get(i).getString("user_username");
                description = orders.get(i).getString("details");
                order = new Order(name,description);
                orderList.add(order);
                Log.d("description:",description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    public void getOrder (){
        //StringRequest request = new StringRequest(Request.Method.POST,"http://reyzan.cloudapp.net/Handyman/worker.php/getorder",new Response.Listener<String>() {
        StringRequest request = new StringRequest(Request.Method.POST,"http://192.168.1.104/Handyman/worker.php/getorder",new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(MainActivity.class.getSimpleName(), "Main Response: " + response.toString());
                try {
                    JSONArray jsonArr = new JSONArray(response);
                    Log.d("JSONResult:", jsonArr.toString());
                    Log.d("JSONarrLength:",jsonArr.length()+"");
                    for(int ii = 0; ii < jsonArr.length();ii++){
                        JSONObject json = jsonArr.getJSONObject(ii);
                        filtered.add(json);
                        Log.d("isiFiltered",filtered.toString());
                    }
                    prepareOrderData(filtered);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"JSON Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("status","0");
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            session.setLogin(false);
            sql.deleteUsers();
            Intent out = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
