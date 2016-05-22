package ppla5.handymanworkerapp.custom_object;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Ari on 5/2/2016.
 */
public class Order {
    private boolean order_status;
    private int total_worker,id;
    private float rating;
    private double latitude,longitude;
    private String name,description,category,address,phone,date;
    public Order() {
    }

    public Order(int id,String name,String date,boolean order_status,int total_worker,String category,float rating,String phone,String description,String address,double latitude,double longitude) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.order_status = order_status;
        this.total_worker = total_worker;
        this.category = category;
        this.rating = rating;
        this.phone = phone;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public int getID() {return id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public int getTotal_worker() {
        return total_worker;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhone() {
        return phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }
}
