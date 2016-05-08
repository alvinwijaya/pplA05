package ppla5.handymanworkerapp;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Ari on 5/2/2016.
 */
public class Order {
    private String date;
    private boolean order_status;
    private int total_worker;
    private float rating;
    private double latitude,longitude;
    private String name,description,review,category,address;
    public Order() {
    }

    public Order(String name,String date,boolean order_status,int total_worker,String category,float rating,String review,String description,String address,double latitude,double longitude) {
        this.name = name;
        this.date = date;
        this.order_status = order_status;
        this.total_worker = total_worker;
        this.category = category;
        this.rating = rating;
        this.review = review;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOrder_status() {
        return order_status;
    }

    public void setOrder_status(boolean order_status) {
        this.order_status = order_status;
    }

    public int getTotal_worker() {
        return total_worker;
    }

    public void setTotal_worker(int total_worker) {
        this.total_worker = total_worker;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
