package ppla5.handymanworkerapp.fragment;


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
import java.util.Map;

import ppla5.handymanworkerapp.AppController;
import ppla5.handymanworkerapp.R;
import ppla5.handymanworkerapp.adapter.HistoryCardAdapter;
import ppla5.handymanworkerapp.handler.SessionHandler;
import ppla5.handymanworkerapp.custom_object.History;

/**
 * Created by Ari on 5/22/2016.
 */
public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<History> history;
    private SessionHandler session;
    private RecyclerView recyclerView;
    private HistoryCardAdapter historyCardAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getContext());
        history = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        historyCardAdapter = new HistoryCardAdapter(history, getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    recyclerView.setAdapter(historyCardAdapter);
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
                                        getHistory();
                                    }
                                }
        );
        return view;
    }

    public void getHistory(){
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/worker.php/gethistory", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONArray jsonArr = new JSONArray(s);
                    Log.d("JSONResult:", jsonArr.toString());
                    for(int ii =0; ii < jsonArr.length();ii++){
                        JSONObject json = jsonArr.getJSONObject(ii);
                        String name = json.getString("user_username");
                        String category = json.getString("category");
                        String address = json.getString("address");
                        String date = json.getString("date");
                        int total_worker = Integer.parseInt(json.getString("total_worker"));
                        History h = new History(name,category,address,date,total_worker);
                        history.add(h);
                    }
                    historyCardAdapter.notifyDataSetChanged();
                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                }catch (JSONException e){
                    Toast.makeText(getContext(), "JSON Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),"Something Wrong In Volley", Toast.LENGTH_LONG).show();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                Log.d("username",session.getUsername());
                String username = session.getUsername();
                map.put("worker_username", username);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onRefresh() {
        history.clear();
        historyCardAdapter.clear();
        getHistory();
    }
}


