package ppla5.handymanworkerapp.adapter;

/**
 * Created by Ari on 5/22/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import ppla5.handymanworkerapp.R;
import ppla5.handymanworkerapp.custom_object.History;
import ppla5.handymanworkerapp.handler.SessionHandler;
import ppla5.handymanworkerapp.view_holder.HistoryViewHolder;

public class HistoryCardAdapter extends RecyclerView.Adapter<HistoryViewHolder>{
    public ArrayList<History> history;
    public Context context;
    private SessionHandler session;

    public HistoryCardAdapter(ArrayList<History> history, Context context) {
        this.history = history;
        this.context = context;
        session = new SessionHandler(context);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, int position) {
        History his = history.get(position);
        holder.userName.setText(his.getName());
        holder.category.setText(his.getCategory());
        holder.address.setText(his.getAddress());
        holder.date.setText(his.getDate());
        holder.total_worker.setText(his.getTotal_worker()+"");
    }



    @Override
    public int getItemCount() {
        return history.size();
    }

    public void clear() {
        history.clear();
    }
}
