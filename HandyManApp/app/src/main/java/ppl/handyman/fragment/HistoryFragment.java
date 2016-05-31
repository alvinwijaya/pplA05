package ppl.handyman.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ppl.handyman.AppController;
import ppl.handyman.R;
import ppl.handyman.adapter.FavoriteCardAdapter;
import ppl.handyman.adapter.HistoryCardAdapter;
import ppl.handyman.custom_object.History;
import ppl.handyman.custom_object.Worker;
import ppl.handyman.handler.SessionHandler;

/**
 * Created by Ari on 4/12/2016.
 */
public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Handler handler = new Handler();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        Collections.sort(history);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        history.clear();
                                        historyCardAdapter.clear();
                                        getHistory();
                                    }
                                }
        );
        historyCardAdapter = new HistoryCardAdapter(history, getContext());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(historyCardAdapter);
            }
        }
                , 1000);
        return view;
    }

    public void getHistory(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/history", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONArray jsonArr = new JSONArray(s);
                    Log.d("JSONResult:", jsonArr.toString());
                    for(int ii =0; ii < jsonArr.length();ii++){
                        JSONObject json = jsonArr.getJSONObject(ii);
                        String name = json.getString("worker");
                        String category = json.getString("category");
                        String address = json.getString("address");
                        String date = json.getString("date");
                        History h = new History(name,category,address,date);
                        history.add(h);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }catch (JSONException e){
                    Log.d("error",e.getMessage());
                    Toast.makeText(getContext(), "JSON Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                Log.d("username",session.getUsername());
                String username = session.getUsername();
                map.put("username", username);
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
