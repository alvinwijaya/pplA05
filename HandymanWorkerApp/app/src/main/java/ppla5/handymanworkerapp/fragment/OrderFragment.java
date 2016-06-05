package ppla5.handymanworkerapp.fragment;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import ppla5.handymanworkerapp.AppController;
import ppla5.handymanworkerapp.activity.DashboardActivity;
import ppla5.handymanworkerapp.activity.SplashActivity;
import ppla5.handymanworkerapp.custom_object.Maps;
import ppla5.handymanworkerapp.custom_object.Order;
import ppla5.handymanworkerapp.adapter.OrderAdapter;
import ppla5.handymanworkerapp.R;
import ppla5.handymanworkerapp.handler.SQLiteHandler;
import ppla5.handymanworkerapp.handler.SessionHandler;

public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    private List<Order> orderList = new ArrayList<>();
    private Order order;
    private ArrayList<JSONObject> filtered;
    private boolean order_status;
    private int total_worker,id;
    private float rating;
    private double latitude, longitude;
    private String name, description, category, address, phone,date;
    private SQLiteHandler sql;
    private SessionHandler session;
    private final double DISTANCE_TO_WORKER = 5000;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SplashActivity splash;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SQLiteHandler(getContext());
        session = new SessionHandler(getContext());
        filtered = new ArrayList<>();
        splash = new SplashActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        mAdapter = new OrderAdapter(orderList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(mAdapter);
                                }
                            }
                , 1000);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getOrder();
                                    }
                                }
        );
        return view;
    }
    public void prepareOrderData(ArrayList<JSONObject> orders) {
        for (int i = 0; i < orders.size(); i++) {
            try {
                name = orders.get(i).getString("user_name");
                phone = orders.get(i).getString("phone");
                id = orders.get(i).getInt("user_order_id");
                date = orders.get(i).getString("date");
                String temp = orders.get(i).getString("order_status");
                if(temp.equals("0")){
                    order_status = false;
                }else {
                    order_status = true;
                }
                total_worker = orders.get(i).getInt("total_worker");
                category = orders.get(i).getString("category");
                rating = (float) orders.get(i).getDouble("rating");
                description = orders.get(i).getString("details");
                address = orders.get(i).getString("address");
                latitude = orders.get(i).getDouble("latitude");
                longitude = orders.get(i).getDouble("longitude");
                Log.d("nomor",phone+"");
                order = new Order(id,name, date, order_status, total_worker, category, rating, phone, description, address, latitude, longitude);
                orderList.add(order);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }

    public void getOrder() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST,"http://reyzan.cloudapp.net/HandyMan/worker.php/getorder",new Response.Listener<String>() {
        //StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.104/Handyman/worker.php/getorder", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(OrderFragment.class.getSimpleName(), "Main Response: " + response.toString());
                try {
                    Log.d("maps", SplashActivity.latitude +" "+ SplashActivity.longitude);
                    JSONArray jsonArr = new JSONArray(response);
                    if(SplashActivity.currentLoc != null) {
                        for (int ii = 0; ii < jsonArr.length(); ii++) {
                            Location locationLatLng = new Location("Worker");
                            JSONObject json = jsonArr.getJSONObject(ii);
                            locationLatLng.setLatitude(json.getDouble("latitude"));
                            locationLatLng.setLongitude(json.getDouble("longitude"));
                            double distance = SplashActivity.currentLoc.distanceTo(locationLatLng);
                            Log.d("map distance",distance+"");
                            if (distance < DISTANCE_TO_WORKER) {
                                filtered.add(json);
                                Log.d("size:", filtered.size() + "");
                            }
                        }
                        prepareOrderData(filtered);
                    } else {
                        Toast.makeText(getContext(), "Current Location = null. Please restart application", Toast.LENGTH_LONG).show();
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "There are no Order around you", Toast.LENGTH_LONG).show();
                    mAdapter.notifyDataSetChanged();
                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "Something Wrong In Volley", Toast.LENGTH_LONG).show();
                mAdapter.notifyDataSetChanged();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                Log.d("user",session.getUsername());
                Log.d("category",session.getCategory());
                map.put("status", "0");
                map.put("latitude",SplashActivity.latitude+"");
                map.put("longitude",SplashActivity.longitude+"");
                map.put("username",session.getUsername());
                map.put("category",session.getCategory());
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);

    }

    @Override
    public void onRefresh() {
        filtered.clear();
        mAdapter.clear();
        getOrder();
    }
}
