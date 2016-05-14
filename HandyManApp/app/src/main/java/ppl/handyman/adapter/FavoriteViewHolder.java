package ppl.handyman.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import ppl.handyman.R;

/**
 * Created by ASUS on 5/7/2016.
 */
public class FavoriteViewHolder extends RecyclerView.ViewHolder {
    public TextView name, category;
    public ImageView photo;
    public Button rating_button;
    public RatingBar ratingBar;

    public FavoriteViewHolder(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        category = (TextView) view.findViewById(R.id.category);
        rating_button = (Button) view.findViewById(R.id.rating_button);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        photo = (ImageView) view.findViewById(R.id.photo);

    }
}
