package ppl.handyman.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ppl.handyman.AppController;
import ppl.handyman.fragment.MapFragment;
import ppl.handyman.R;
import ppl.handyman.handler.SQLiteHandler;
import ppl.handyman.handler.SessionHandler;

public class OrderActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText address;
    private GoogleApiClient mClient;
    private Location currentLoc;
    private LocationManager locationManager;
    private SessionHandler session;
    private SQLiteHandler sqlhandler;
    private ArrayList<JSONObject> filtered;
    private Button orderBtn;
    private EditText detailWork;
    private EditText totalWorker;
    private TextView estimateCost;
    private final double DISTANCE_TO_WORKER = 5000;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

		final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        session = new SessionHandler(getApplicationContext());
        sqlhandler = new SQLiteHandler(getApplicationContext());
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        filtered = new ArrayList<>();
        orderBtn = (Button) findViewById(R.id.order);
        detailWork = (EditText) findViewById(R.id.detailWork);
        totalWorker = (EditText) findViewById(R.id.numberWorker);
        estimateCost = (TextView) findViewById(R.id.estimateCost);

        if(estimateCost.getText().toString().equals("")){
            estimateCost.setText("Rp "+String.format("%,.2f",0.0));
        }

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
                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
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
                        .addApi(AppIndex.API).build();
            }
        }
        address = (EditText) findViewById(R.id.searchLocation);
        address.setScroller(new Scroller(getApplicationContext()));
        address.setMaxLines(1);
        address.setHorizontalScrollBarEnabled(true);
        address.setMovementMethod(new ScrollingMovementMethod());
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
                    if(address_value.equals("")){
                        Log.d("Location Default","Location is set to default");
                        mMap.addMarker(new MarkerOptions().position(new LatLng(-6.5801552, 106.7651841)).title("Your Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.5801552, 106.7651841), 17.0f));
                        return;
                    }
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> addresses = geocoder.getFromLocationName(address_value, 1);
                    if (addresses.size() > 0) {
                        final double lat = addresses.get(0).getLatitude();
                        final double lgn = addresses.get(0).getLongitude();
                        if(currentLoc != null) {
                            currentLoc.setLatitude(lat);
                            currentLoc.setLongitude(lgn);
                            latitude = currentLoc.getLatitude();
                            longitude = currentLoc.getLongitude();
                        }
                        String[] picked = session.getPickedCategory();
                        getWorker(picked);
                        //delay put marker 1s to make sure request respond is processed properly
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                putMarker();
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lgn)).title("Your Location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lgn), 17.0f));
                            }
                        }, 500);

                    } else {
                        //this location supposed to be your current location (still hardcoded)
                        putMarker();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        TextWatcher estimateCostWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Price","Price label should have changed");
                String value = totalWorker.getText().toString().trim();
                if(value.equals("")){
                    estimateCost.setText("Rp "+String.format("%,.2f",0.0));
                    return;
                }else{
                    estimateCost.setText("Rp "+String.format("%,.2f",(Integer.parseInt(value) * (double)200000))+"");
                }

            }
        };
        address.addTextChangedListener(watcher);
        totalWorker.addTextChangedListener(estimateCostWatcher);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				v.startAnimation(animScale);
                boolean orderAccepted = putOrder();
                if(orderAccepted){
                    final Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);

                    AlertDialog.Builder alertLocation = new AlertDialog.Builder(OrderActivity.this);
                    alertLocation.setMessage("Thank You For Ordering!");
                    alertLocation.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    });
                    alertLocation.create().show();

                }
            }
        });
        setUpMapIfNeeded();
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

    public void putMarker(){
        mMap.clear();
        for(JSONObject json: this.filtered){
            try {
                mMap.addMarker(new MarkerOptions().position(new LatLng(json.getDouble("latitude"), json.getDouble("longitude"))).title(json.getString("name") + " Location"));
                Log.d("Marker", "Marker was added");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean putOrder(){
        Log.d("Order", "Order is being sent");
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/putorder", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                    Log.d("Put Result:",s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
                return;
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String category = "";
                String[] picked = session.getPickedCategory();
                category = category+picked[0]+",";
                if(picked.length > 1){
                    category+= picked[1];
                }else {
                    category = "";
                    category = category+picked[0];
                }
                map.put("username", session.getUsername());
                map.put("date", dateFormat.format(date));
                map.put("category",category);
                map.put("rating","0");
                map.put("details",detailWork.getText().toString());
                map.put("status",0+"");
                map.put("total_worker",totalWorker.getText().toString());
                if(address.getText().equals("")){
                    map.put("address","no address");
                }else{
                    map.put("address",address.getText().toString());
                }
                map.put("latitude",""+latitude);
                map.put("longitude",""+longitude);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
        return true;
    }

    public void getWorker(final String[] picked){

        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/getworker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try{
                    JSONArray jsonArr = new JSONArray(s);
                    filtered.clear();
                    Log.d("JSONResultWorker:",jsonArr.toString());
                    if (currentLoc!=null){
                        for(int ii =0; ii < jsonArr.length();ii++){

                            Location locationLatLng = new Location("A");
                            JSONObject json = jsonArr.getJSONObject(ii);
                            locationLatLng.setLatitude(json.getDouble("latitude"));
                            locationLatLng.setLongitude(json.getDouble("longitude"));

                            double distance = currentLoc.distanceTo(locationLatLng);
                            if(distance < DISTANCE_TO_WORKER){
                                filtered.add(json);
                            }
                        }
                        putMarker();
                    }else {
                        Log.d("","Current loc null");
                    }
                }catch (JSONException e){
                    Log.e("Error", e.toString());
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
                List<String> pickedCategories = new ArrayList<>(Arrays.asList(picked));
                Map<String,String> map = new HashMap<>();
                map.put("category",pickedCategories.get(0));
                Log.d("category",pickedCategories.get(0));
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
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #} once when {@link #mMap} is not null.
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
            mMap = ((MapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
            ((MapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new MapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {

                        if(currentLoc != null) {
                            currentLoc.setLatitude(latLng.latitude);
                            currentLoc.setLongitude(latLng.longitude);
                            latitude = currentLoc.getLatitude();
                            longitude = currentLoc.getLongitude();
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                address.setText(addresses.get(0).getFeatureName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        String[] picked = session.getPickedCategory();
                        getWorker(picked);
                        //delay put marker 1s to make sure request respond is processed properly
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                putMarker();
                                mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                            }
                        }, 500);

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

    @Override
    public void onConnected(Bundle bundle) {
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if(currentLoc != null){
            Log.d("Location",currentLoc.getLatitude()+" "+currentLoc.getLongitude());
            latitude = currentLoc.getLatitude();
            longitude = currentLoc.getLongitude();
            LatLng myLocation = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            String[] picked = session.getPickedCategory();
            getWorker(picked);
            setUpMapIfNeeded();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("SettingMap","Putting Marker "+ filtered.size());
                    putMarker();
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        address.setText(addresses.get(0).getFeatureName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },1000);
        }else {
            currentLoc = mMap.getMyLocation();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}

