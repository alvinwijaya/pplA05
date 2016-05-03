package ppla5.handymanworkerapp;

/**
 * Created by Ari on 5/2/2016.
 */
public class Order {
    private String title,description;
    public Order() {
    }

    public Order(String title, String description) {
        this.title = title;
        this.description = description;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
