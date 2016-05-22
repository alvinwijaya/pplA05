package ppla5.handymanworkerapp.custom_object;

/**
 * Created by Ari on 5/22/2016.
 */
public class History {
    private String name,category,address,date;
    private int total_worker;

    public History(){

    }
    public History(String name, String category, String address, String date, int total_worker) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.date = date;
        this.total_worker = total_worker;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public int getTotal_worker() {
        return total_worker;
    }
}
