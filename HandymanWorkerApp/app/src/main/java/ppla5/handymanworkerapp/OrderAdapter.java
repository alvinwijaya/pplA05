package ppla5.handymanworkerapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.HashSet;
import java.util.List;

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
        Log.d("masuk2",mMapViews.size()+"");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = ordersList.get(position);
        Log.d("masuk3",ordersList.size()+"");
        holder.setMapLocation(order);
        holder.name.setText(order.getName());
        holder.description.setText(order.getDescription());

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public HashSet<MapView> getMapViews() {
        return mMapViews;
    }
}
