package ppl.handyman.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import ppl.handyman.R;

/**
 * Created by ASUS on 5/7/2016.
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView workerName, category, date,address;

    public HistoryViewHolder(View view) {
        super(view);
        workerName = (TextView) view.findViewById(R.id.worker_value);
        category = (TextView) view.findViewById(R.id.category_value);
        date = (TextView) view.findViewById(R.id.date_value);
        address = (TextView) view.findViewById(R.id.address_value);

    }
}
