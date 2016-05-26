package ppla5.handymanworkerapp.view_holder;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ppla5.handymanworkerapp.R;
import ppla5.handymanworkerapp.custom_object.Order;

/**
 * Created by Ari on 5/7/2016.
 */
public class OrderViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    public TextView name,description;
    public CardView cardView;
    protected GoogleMap mGoogleMap;
    protected Order mOrder;

    public MapView mapView;
    private Context mContext;

    public OrderViewHolder(Context context,View itemView) {
        super(itemView);
        mContext = context;
        cardView = (CardView) itemView.findViewById(R.id.card_view);

        mapView = (MapView) itemView.findViewById(R.id.map);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"clicked",Toast.LENGTH_LONG).show();
            }
        });
        name = (TextView) itemView.findViewById(R.id.name);
        description = (TextView) itemView.findViewById(R.id.description);

        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }
    public void setMapLocation(Order mapLocation) {
        mOrder = mapLocation;

        // If the map is ready, update its content.
        if (mGoogleMap != null) {
            updateMapContents();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        MapsInitializer.initialize(mContext);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // If we have map data, update the map content.
        if (mOrder!= null) {
            updateMapContents();
        }
    }

    private void updateMapContents() {
        // Since the mapView is re-used, need to remove pre-existing mapView features.
        mGoogleMap.clear();
        // Update the mapView feature data and camera position.
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mOrder.getLatitude(),mOrder.getLongitude())).title("Order Location"));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mOrder.getLatitude(),mOrder.getLongitude()), 17f);
        mGoogleMap.moveCamera(cameraUpdate);
    }
    public Context getmContext(){
        return mContext;
    }
}
