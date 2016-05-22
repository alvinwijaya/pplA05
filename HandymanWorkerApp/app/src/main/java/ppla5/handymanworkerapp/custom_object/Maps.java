package ppla5.handymanworkerapp.custom_object;

import android.location.Location;

/**
 * Created by Ari on 5/22/2016.
 */
public class Maps {
    private Location currentLoc;
    private double latitude,longitude;

    public Maps(Location currentLoc, double latitude, double longitude) {
        this.currentLoc = currentLoc;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location getCurrentLoc() {
        return currentLoc;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
