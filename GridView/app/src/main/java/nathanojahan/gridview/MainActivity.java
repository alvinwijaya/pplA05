package nathanojahan.gridview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            //boolean checked = false;
            boolean[] flag= new boolean[9];

            public void onItemClick(AdapterView<?> parent, View imgView, int position, long id) {

                if (position == 0){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.ac_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.ac);
                        flag[position] = false;
                    }
                }
                else if (position == 1){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.air_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.air);
                        flag[position] = false;
                    }
                }
                else if (position == 2){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.bengkel_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.bengkel);
                        flag[position] = false;
                    }
                }
                else if (position == 3){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.besi_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.besi);
                        flag[position] = false;
                    }
                }
                else if (position == 4){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.cat_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.cat);
                        flag[position] = false;
                    }
                }
                else if (position == 5){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.kayu_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.kayu);
                        flag[position] = false;
                    }
                }
                else if (position == 6){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.listrik_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.listrik);
                        flag[position] = false;
                    }
                }
                else if (position == 7){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.taman_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.taman);
                        flag[position] = false;
                    }
                }
                else if (position == 8){
                    if(!flag[position]){
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.tembok_checked);
                        flag[position] = true;
                    }
                    else{
                        ImageView imageView = (ImageView) imgView;
                        imageView.setImageResource(R.drawable.tembok);
                        flag[position] = false;
                    }
                }


            }
        });


    }


}