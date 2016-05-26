package ppla5.handymanworkerapp.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;

import java.util.HashSet;
import java.util.List;

import ppla5.handymanworkerapp.activity.DetailPesanan;
import ppla5.handymanworkerapp.custom_object.Order;
import ppla5.handymanworkerapp.view_holder.OrderViewHolder;
import ppla5.handymanworkerapp.R;

/**
 * Created by Ari on 5/2/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private List<Order> ordersList;
    protected HashSet<MapView> mMapViews = new HashSet<>();

    public OrderAdapter(List<Order> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list, parent, false);

        OrderViewHolder viewHolder = new OrderViewHolder(parent.getContext(), itemView);

        mMapViews.add(viewHolder.mapView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, int position) {
        final Order order = ordersList.get(position);
        holder.setMapLocation(order);
        holder.name.setText(order.getName());
        holder.description.setText(order.getDescription());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), order.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(holder.getmContext(), DetailPesanan.class);
                intent.putExtra("id", order.getID());
                intent.putExtra("name", order.getName());
                intent.putExtra("description", order.getDescription());
                intent.putExtra("address", order.getAddress());
                intent.putExtra("category", order.getCategory());
                intent.putExtra("total_worker", order.getTotal_worker());
                intent.putExtra("date", order.getDate());
                intent.putExtra("phone", order.getPhone());
                holder.getmContext().startActivity(intent);
                //mContext.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public Object getItem(int position) {
        return ordersList.get(position);
    }

    public HashSet<MapView> getMapViews() {
        return mMapViews;
    }

    public void clear(){
        ordersList.clear();
    }
}
