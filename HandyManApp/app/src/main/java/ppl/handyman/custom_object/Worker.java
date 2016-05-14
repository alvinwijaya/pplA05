package ppl.handyman.custom_object;

/**
 * Created by ASUS on 5/7/2016.
 */
public class Worker implements Comparable{
    private String name;
    private String category;
    private String photoLink;
    private String username;
    private double rating;

    public Worker(String username,String name, String category, String photoLink, double rating){
        this.username = username;
        this.name = name;
        this.category = category;
        this.photoLink = photoLink;
        this.rating = rating;
    }
    public String getPhotoLink() {

        return photoLink;
    }

    public String getCategory() {

        return category;
    }

    public String getUsername(){
        return this.username;
    }
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Object another) {
        Worker otherWorker = (Worker) another;
        if(otherWorker.rating < this.rating){
            return -1;
        }else if(otherWorker.rating > this.rating){
            return 1;
        }else {
            return 0;
        }
    }
}
