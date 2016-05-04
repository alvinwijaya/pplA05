package ppla5.handymanworkerapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ari on 5/2/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private List<Order> ordersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
        }
    }
    public OrderAdapter(List<Order> ordersList) {
        this.ordersList = ordersList;
    }
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list, parent, false);
        return new OrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.MyViewHolder holder, int position) {
        Order order = ordersList.get(position);
        holder.name.setText(order.getName());
        holder.description.setText(order.getDescription());
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}
