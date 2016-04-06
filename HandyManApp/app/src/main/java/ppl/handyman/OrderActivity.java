package ppl.handyman;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class OrderActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setUpMapIfNeeded();
        address = (EditText)findViewById(R.id.searchLocation);
        /*
        Text watcher is a listener for text when text value changed
         */
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String address_value = address.getText().toString().trim();
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> addresses = geocoder.getFromLocationName(address_value,1);
                    if(addresses.size() > 0){
                        double lat = addresses.get(0).getLatitude();
                        double lgn = addresses.get(0).getLongitude();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lgn)).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lgn), 17.0f));
                    }else{
                        //this location supposed to be your current location (still hardcoded)
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(-6.364542, 106.828671)).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.364542, 106.828671), 17.0f));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        address.addTextChangedListener(watcher);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                    }
                });
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //this location supposed to be your current location (still hardcoded)
        mMap.addMarker(new MarkerOptions().position(new LatLng(-6.364542, 106.828671)).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.364542, 106.828671), 17.0f));
    }
}