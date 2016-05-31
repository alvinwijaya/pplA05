package ppl.handyman.adapter;

/**
 * Created by ASUS on 5/7/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import ppl.handyman.R;
import ppl.handyman.custom_object.History;
import ppl.handyman.handler.SessionHandler;

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
        holder.workerName.setText(his.getName());
        holder.category.setText(his.getCategory());
        holder.address.setText(his.getAddress());
        holder.date.setText(his.getDate());
    }

    public void clear(){
        this.history.clear();
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}
