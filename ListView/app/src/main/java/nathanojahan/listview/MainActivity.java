package nathanojahan.listview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //generate list
        ArrayList<String> list = new ArrayList<String>();
        list.add("Pesanan 1");
        list.add("Pesanan 2");
        list.add("Pesanan 3");
        list.add("Pesanan 4");

        //instantiate custom adapter
        MyCustomAdapter adapter = new MyCustomAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.list_order);
        lView.setAdapter(adapter);


    }
    public void sendMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, DetailPesanan.class);
        startActivity(intent);
    }
}
