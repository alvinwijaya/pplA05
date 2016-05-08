package ppla5.handymanworkerapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ari on 5/7/2016.
 */
public class OrderViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    public TextView name,description;

    protected GoogleMap mGoogleMap;
    protected Order mOrder;

    public MapView mapView;
    private Context mContext;

    public OrderViewHolder(Context context,View itemView) {
        super(itemView);
        mContext = context;

        mapView = (MapView) itemView.findViewById(R.id.map);
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
        Log.d("sesuatu","haha");
        // Update the mapView feature data and camera position.
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mOrder.getLatitude(),mOrder.getLongitude())).title("Order Location"));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mOrder.getLatitude(),mOrder.getLongitude()), 17f);
        mGoogleMap.moveCamera(cameraUpdate);
    }
}
