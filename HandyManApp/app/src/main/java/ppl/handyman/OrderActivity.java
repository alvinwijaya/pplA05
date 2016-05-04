package ppl.handyman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private ArrayList<JSONObject> filtered;
    private Button orderBtn;
    private EditText detailWork;
    private EditText totalWorker;
    private TextView estimateCost;
    private boolean firstTime = false;
    private final double DISTANCE_TO_WORKER = 5000;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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
            turnGPSOn();
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
        setUpMapIfNeeded();
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
                                putMarkerWithoutCurrentLocation();
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lgn)).title("Your Location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lgn), 17.0f));
                            }
                        }, 500);

                    } else {
                        //this location supposed to be your current location (still hardcoded)
                        putMarkerWithoutCurrentLocation();
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
                boolean orderAccepted = putOrder(filtered);
                if(orderAccepted){
                    Intent intent = new Intent(getApplicationContext(),WaitOrderActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

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
        setUpMap();
        for(JSONObject json: this.filtered){
            try {
                mMap.addMarker(new MarkerOptions().position(new LatLng(json.getDouble("latitude"), json.getDouble("longitude"))).title(json.getString("name") + " Location"));
                Log.d("Marker", "Marker was added");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void putMarkerWithoutCurrentLocation(){
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

    public boolean putOrder(final ArrayList<JSONObject> workers){
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
                Map<String,String> userDetails = sqlhandler.getUserDetails();
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
                map.put("username", userDetails.get("username"));
                map.put("date", dateFormat.format(date));
                map.put("category",category);
                map.put("rating","0");
                map.put("review","");
                map.put("details",detailWork.getText().toString());
                map.put("status",false+"");
                map.put("totalWorker",totalWorker.getText().toString());
                if(address.getText().equals("")){
                    map.put("address",userDetails.get("address"));
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
                    Log.d("JSONResult:",jsonArr.toString());
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
                    if(firstTime){
                        firstTime = !firstTime;
                        putMarker();
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
                List<String> pickedCategories = new ArrayList<>(Arrays.asList(picked));
                Map<String,String> map = new HashMap<>();
                map.put("category1",pickedCategories.get(0));
                Log.d("category1",pickedCategories.get(0));
                if(pickedCategories.size() > 1){
                    map.put("category2",pickedCategories.get(1));
                    Log.d("category2",pickedCategories.get(1));
                }

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
        turnGPSOff();
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
        turnGPSOn();
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
            mMap = ((MapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
            ((MapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new MapFragment.OnTouchListener(){
                @Override
                public void onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                setUpMap();
                String[] picked = session.getPickedCategory();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                        if(currentLoc != null) {
                            currentLoc.setLatitude(latLng.latitude);
                            currentLoc.setLongitude(latLng.longitude);
                            latitude = currentLoc.getLatitude();
                            longitude = currentLoc.getLongitude();
                        }
                        String[] picked = session.getPickedCategory();
                        getWorker(picked);
                        //delay put marker 1s to make sure request respond is processed properly
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                putMarkerWithoutCurrentLocation();
                                mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                            }
                        }, 1000);


                    }
                });
            }else{
                putMarker();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Connected","Now connected");
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if(currentLoc != null){
            latitude = currentLoc.getLatitude();
            longitude = currentLoc.getLongitude();
            Log.d("Location: ", latitude + " " + longitude);
            String [] picked = session.getPickedCategory();
            getWorker(picked);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpMapIfNeeded();
                }
            }, 1000);

        }
    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
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
