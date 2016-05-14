package ppl.handyman.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import java.util.TreeSet;

import ppl.handyman.AppController;
import ppl.handyman.R;
import ppl.handyman.handler.SessionHandler;
import ppl.handyman.custom_object.Worker;
import ppl.handyman.adapter.FavoriteCardAdapter;

/**
 * Created by Ari on 4/12/2016.
 */
public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavoriteCardAdapter favoriteCardAdapter;
    private ArrayList<Worker> workerList;
    private TreeSet<Worker> hasVoted;
    private SessionHandler session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getContext());
        workerList = new ArrayList<>();
        hasVoted = new TreeSet<>();

        getWorkerThatHaveWorkedForMe();
        getWorkerThatHaveVoted();

        favoriteCardAdapter = new FavoriteCardAdapter(workerList,hasVoted,getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorit, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(favoriteCardAdapter);
                                }
                            }
               , 1000);
        return view;
    }
    public void getWorkerThatHaveVoted(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/getmeworker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONArray jsonArr = new JSONArray(s);
                    Log.d("JSONResult:", jsonArr.toString());
                    for(int ii =0; ii < jsonArr.length();ii++){
                        JSONObject json = jsonArr.getJSONObject(ii);
                        String name = json.getString("name");
                        String username = json.getString("username");
                        String photoLink = json.getString("photo");
                        String tag = json.getString("tag");
                        double rating = json.getDouble("rating");
                        Worker worker = new Worker(username,name,photoLink,tag,rating);
                        hasVoted.add(worker);
                    }
                }catch (JSONException e){
                    Log.d("error",e.getMessage());
                    Toast.makeText(getContext(), "JSON Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
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

    public void getWorkerThatHaveWorkedForMe(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/getallworker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONArray jsonArr = new JSONArray(s);
                    Log.d("JSONResult:", jsonArr.toString());
                    for(int ii =0; ii < jsonArr.length();ii++){
                        JSONObject json = jsonArr.getJSONObject(ii);
                        String username = json.getString("username");
                        String name = json.getString("name");
                        String photoLink = json.getString("photo");
                        String tag = json.getString("tag");
                        double rating = json.getDouble("rating");
                        Worker worker = new Worker(username,name,tag,photoLink,rating);
                        workerList.add(worker);
                    }
                }catch (JSONException e){
                    Log.d("error",e.getMessage());
                    Toast.makeText(getContext(), "JSON Error getWorkerThatHaveWorkedForMe" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                String username = session.getUsername();
                Log.d("username",session.getUsername());
                map.put("username", username);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
