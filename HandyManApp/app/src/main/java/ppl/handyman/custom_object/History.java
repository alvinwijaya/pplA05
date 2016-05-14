package ppl.handyman.custom_object;

/**
 * Created by ASUS on 5/13/2016.
 */
public class History implements Comparable{

    private String name;
    private String category;
    private String address;
    private String date;

    public History(String name, String category, String address, String date){
        this.name = name;
        this.category = category;
        this.address = address;
        this.date = date;
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

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Object another) {
        History hist = (History) another;
        return this.date.compareTo(hist.date);
    }


}
