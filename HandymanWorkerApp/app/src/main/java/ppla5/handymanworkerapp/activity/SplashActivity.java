package ppla5.handymanworkerapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ppla5.handymanworkerapp.R;
import ppla5.handymanworkerapp.custom_object.Maps;
import ppla5.handymanworkerapp.handler.SessionHandler;

/**
 * Created by Ari on 5/22/2016.
 */
public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    private SessionHandler session;
    public static double latitude, longitude;
    private GoogleApiClient mClient;
    public static Location currentLoc;
    private LocationManager locationManager;
    private Maps maps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_splash);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder alertLocation = new AlertDialog.Builder(this);
            alertLocation.setMessage("Let Google help apps to determine location. This means sending anonymous location data to Google.").setTitle("Use Location?");
            alertLocation.setCancelable(false);
            alertLocation.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent gpsOptionsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        if (checkPlayServices()) {
            if (mClient == null) {
                mClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(AppIndex.API).build();
            }
        }
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if(currentLoc != null){
            latitude = currentLoc.getLatitude();
            longitude = currentLoc.getLongitude();
            Log.d("map dashboard", latitude+" "+ longitude);
            maps = new Maps(currentLoc,latitude,longitude);

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

    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    public void onStop() {
        super.onStop();
        turnGPSOff();
        mClient.disconnect();
    }
    @Override
    public void onResume() {
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
}
