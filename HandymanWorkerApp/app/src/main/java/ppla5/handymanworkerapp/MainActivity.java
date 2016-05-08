package ppla5.handymanworkerapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    private List<Order> orderList = new ArrayList<>();
    private Order order;
    private ArrayList<JSONObject> filtered;
    private String date;
    private boolean order_status;
    private int total_worker;
    private float rating;
    private double latitude, longitude;
    private String name, description, review, category, address;
    private SQLiteHandler sql;
    private SessionHandler session;
    private GoogleApiClient mClient;
    private Location currentLoc;
    private final double DISTANCE_TO_WORKER = 5000;
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sql = new SQLiteHandler(getApplicationContext());
        session = new SessionHandler(getApplicationContext());
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        order = new Order();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        filtered = new ArrayList<>();
        mAdapter = new OrderAdapter(orderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


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
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    dialog.dismiss();
                    startActivity(i);
                    finish();

                }
            });
            alertLocation.create().show();
            turnGPSOn();
        }

        if (checkPlayServices()) {
            if (mClient == null) {
                mClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Order order = orderList.get(position);
                Toast.makeText(getApplicationContext(), order.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    public void prepareOrderData(ArrayList<JSONObject> orders) {
        for (int i = 0; i < orders.size(); i++) {
            try {
                Log.d("masuk","masuk");
                //setUpMap();
                name = orders.get(i).getString("user_username");
                date = orders.get(i).getString("date");
                String temp = orders.get(i).getString("order_status");
                if(temp.equals("0")){
                    order_status = false;
                }else {
                    order_status = true;
                }
                total_worker = orders.get(i).getInt("total_worker");
                category = orders.get(i).getString("category");
                rating = (float) orders.get(i).getDouble("rating");
                review = orders.get(i).getString("review");
                description = orders.get(i).getString("details");
                address = orders.get(i).getString("address");
                latitude = orders.get(i).getDouble("latitude");
                longitude = orders.get(i).getDouble("longitude");

                order = new Order(name, date, order_status, total_worker, category, rating, review, description, address, latitude, longitude);
                orderList.add(order);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void getOrder() {
        StringRequest request = new StringRequest(Request.Method.POST,"http://reyzan.cloudapp.net/HandyMan/worker.php/getorder",new Response.Listener<String>() {
        //StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.104/Handyman/worker.php/getorder", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(MainActivity.class.getSimpleName(), "Main Response: " + response.toString());
                try {
                    JSONArray jsonArr = new JSONArray(response);
                    Log.d("JSONResult:", jsonArr.toString());
                    Log.d("JSONarrLength:", jsonArr.length() + "");
                    if(currentLoc != null){
                        for (int ii = 0; ii < jsonArr.length(); ii++) {
                            Location locationLatLng = new Location("Worker");
                            JSONObject json = jsonArr.getJSONObject(ii);
                            locationLatLng.setLatitude(json.getDouble("latitude"));
                            locationLatLng.setLongitude(json.getDouble("longitude"));

                            double distance = currentLoc.distanceTo(locationLatLng);
                            if(distance < DISTANCE_TO_WORKER){
                                filtered.add(json);
                                Log.d("isiFiltered", filtered.toString());
                            }
                        }
                    prepareOrderData(filtered);
                    } else {
                        Log.d("error","Current Loc = null");
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "JSON Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Something Wrong In Volley", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                Map<String,String> userDetails = sql.getUserDetails();
                Log.d("maps",latitude +" "+ longitude);
                Log.d("user",userDetails.get("username"));
                map.put("status", "0");
                map.put("latitude",latitude+"");
                map.put("longitude",longitude+"");
                map.put("username",userDetails.get("username"));
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if(currentLoc != null){
            latitude = currentLoc.getLatitude();
            longitude = currentLoc.getLongitude();
            if (orderList.isEmpty()) {
                getOrder();
            }
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
    private void setUpMap() {
        //this location supposed to be your current location (still hardcoded)
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));
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
    protected void onResume() {
        super.onResume();
        turnGPSOn();
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            session.setLogin(false);
            sql.deleteUsers();
            Intent out = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
