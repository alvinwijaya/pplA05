package ppl.handyman.adapter;

/**
 * Created by ASUS on 5/7/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ppl.handyman.AppController;
import ppl.handyman.R;
import ppl.handyman.custom_object.Worker;
import ppl.handyman.handler.SessionHandler;

public class FavoriteCardAdapter extends RecyclerView.Adapter<FavoriteViewHolder>{
    public ArrayList<Worker> favoriteWorker;
    public TreeSet<Worker> hasNotRatedWorker;
    public Context context;
    private SessionHandler session;

    public FavoriteCardAdapter(ArrayList<Worker> fav, TreeSet<Worker> hasRated, Context context) {
        this.favoriteWorker = fav;
        this.hasNotRatedWorker = hasRated;
        this.context = context;
        session = new SessionHandler(context);
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.worker_list, parent, false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, int position) {
        //String name = favoriteWorker.get(position).getName();
        String url = "http://reyzan.cloudapp.net/HandyMan/img/2.jpg";

        if(favoriteWorker != null && !favoriteWorker.isEmpty()){
            final Worker worker = favoriteWorker.get(position);
            holder.category.setText(worker.getCategory());
            holder.name.setText(worker.getName());
            Picasso.with(context).load(url).into(holder.photo);
            if(hasNotRatedWorker.contains(worker)){
                Button btn = holder.rating_button;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        voteWorker(worker.getUsername(),holder);
                        holder.rating_button.setVisibility(View.GONE);
                    }
                });
            }else {
                holder.rating_button.setVisibility(View.GONE);
            }
        }
    }

    private void voteWorker(final String workerUsername, final FavoriteViewHolder holder){
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/voteworker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Rating status","Rating updated");
                Toast.makeText(context,"Thank you!",Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Rating status","Rating update failed");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                RatingBar rat = holder.ratingBar;
                float rating = rat.getRating();
                map.put("user_username",session.getUsername());
                map.put("worker_username", workerUsername);
                map.put("vote",rating+"");
                Log.d("user_username", session.getUsername());
                Log.d("worker_username", workerUsername);
                Log.d("vote",rating+"");
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public int getItemCount() {
        return favoriteWorker.size();
    }
}
