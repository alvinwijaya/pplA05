package ppla5.handymanworkerapp;

/**
 * Created by Ari on 5/2/2016.
 */
public class Order {
    private String name,description;
    public Order() {
    }

    public Order(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
