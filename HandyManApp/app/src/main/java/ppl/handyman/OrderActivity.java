package ppl.handyman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText address;
    private GoogleApiClient mClient;
    private Location currentLoc;
    private LocationManager locationManager;
    private SessionHandler session;
    private SQLiteHandler sqlhandler;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        session = new SessionHandler(getApplicationContext());
        sqlhandler = new SQLiteHandler(getApplicationContext());
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            AlertDialog.Builder alertLocation = new AlertDialog.Builder(this);
            alertLocation.setMessage("Let Google help apps to determine location. This means sending anonymous location data to Google.").setTitle("Use Location?");
            alertLocation.setCancelable(false);
            alertLocation.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                    finish();
                }
            });
            alertLocation.setNegativeButton("DISAGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(),DashboardActivity.class);
                    dialog.dismiss();
                    startActivity(i);
                    finish();

                }
            });
            alertLocation.create().show();

        }
        if(checkPlayServices()){
            if (mClient == null) {
                mClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

        String[] picked = session.getPickedCategory();
        String category1 = "";
        String category2 = "";
        if (picked.length > 0){
            category1 = picked[0];
            if(picked.length > 1){
                category2 = picked[1];
            }
        }
        getWorker(picked);
        setUpMapIfNeeded();
        address = (EditText) findViewById(R.id.searchLocation);
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
                    List<Address> addresses = geocoder.getFromLocationName(address_value, 1);
                    if (addresses.size() > 0) {
                        double lat = addresses.get(0).getLatitude();
                        double lgn = addresses.get(0).getLongitude();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lgn)).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lgn), 17.0f));
                    } else {
                        //this location supposed to be your current location (still hardcoded)
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        address.addTextChangedListener(watcher);

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void getWorker(final String[] picked){


        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.229/HandyMan/user.php/getworker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONArray jsonArr = new JSONArray(s);
                    boolean authorized = true;
                    if(authorized){
                        session.setLogin(true);
                        String username = jsonArr.getString(0);
                        Log.e("JSONResult:",jsonArr.toString());
                    }
                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),"JSON Error " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                List<String> arr = new ArrayList<>(Arrays.asList(picked));
                JSONArray pickedCategories = new JSONArray(arr);
                Map<String,String> map = new HashMap<>();
                map.put("categories",pickedCategories.toString());
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


    protected void onStart() {
        super.onStart();
        mClient.connect();

    }

    protected void onStop() {
        super.onStop();
        mClient.disconnect();

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
        finish();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if(currentLoc != null){
            latitude = currentLoc.getLatitude();
            longitude = currentLoc.getLongitude();
            mMap.clear();
            setUpMap();
            Toast.makeText(getApplicationContext(),latitude+" "+longitude,Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        AlertDialog.Builder alertLocation = new AlertDialog.Builder(this);
        alertLocation.setMessage("There is no connection available, please enable your cellular data or connect to WiFi").setTitle("Disconnected from Internet");
        alertLocation.setCancelable(false);
        alertLocation.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent OptionsIntent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                startActivity(OptionsIntent);
                finish();
            }
        });
    }
}
