package ppla5.handymanworkerapp.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ppla5.handymanworkerapp.R;

/**
 * Created by Ari on 5/22/2016.
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder{
    public TextView userName, category, date,address, total_worker;

    public HistoryViewHolder(View view) {
        super(view);
        userName = (TextView) view.findViewById(R.id.user_value);
        category = (TextView) view.findViewById(R.id.category_value);
        date = (TextView) view.findViewById(R.id.date_value);
        address = (TextView) view.findViewById(R.id.address_value);
        total_worker = (TextView) view.findViewById(R.id.total_worker_value);
    }
}
