package ppla5.handymanworkerapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.Map;

import ppla5.handymanworkerapp.AppController;
import ppla5.handymanworkerapp.R;
import ppla5.handymanworkerapp.handler.SessionHandler;

/**
 * Created by Ari on 4/29/2016.
 */
public class DetailPesanan extends AppCompatActivity {
    private String name,description,address,category,date,phone;
    private int total_worker,id;
    private TextView textName,textAddress,textDescription,textCategory,textDate,textTotalWorker,textCost;
    private Button accept;
    private SessionHandler session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);
        final Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");
        address = intent.getStringExtra("address");
        category = intent.getStringExtra("category");
        date = intent.getStringExtra("date");
        total_worker = intent.getIntExtra("total_worker", 1);
        phone = intent.getStringExtra("phone");
        session = new SessionHandler(getApplicationContext());

        textName = (TextView) findViewById(R.id.name);
        textAddress = (TextView) findViewById(R.id.address);
        textDescription = (TextView) findViewById(R.id.description);
        textCategory = (TextView) findViewById(R.id.category);
        textDate = (TextView) findViewById(R.id.date);
        textTotalWorker = (TextView) findViewById(R.id.total_worker);
        textCost = (TextView) findViewById(R.id.cost_estimation);
        accept = (Button) findViewById(R.id.accept);

        textName.setText(name);
        textAddress.setText(address);
        textDescription.setText(description);
        textCategory.setText(category);
        textDate.setText(date);
        textTotalWorker.setText(String.valueOf(total_worker));
        textCost.setText("Rp "+String.format("%,.2f",(total_worker * (double)200000))+"");
        Log.d("nomor telp", phone);
        Log.d("id",id+"");
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("masuk","onclick");
                AlertDialog.Builder alertAccept = new AlertDialog.Builder(DetailPesanan.this);
                alertAccept.setMessage("Are you sure to accept this order?").setTitle("Accept Order?");
                alertAccept.setCancelable(false);
                alertAccept.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("masuk","yes");
                        acceptOrder();
                        call();
                    }
                });
                alertAccept.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("masuk","no");
                        dialog.dismiss();
                    }
                });
                alertAccept.create().show();
            }
        });
    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    public void acceptOrder() {
        StringRequest request = new StringRequest(Request.Method.POST,"http://reyzan.cloudapp.net/HandyMan/worker.php/acceptorder",new Response.Listener<String>() {
            //StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.104/Handyman/worker.php/acceptorder", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("status","update");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Something Wrong In Volley", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("status", "1");
                Log.d("id",id+"");
                map.put("user_order_id",id+"");
                Log.d("worker",session.getUsername());
                map.put("worker_username",session.getUsername());
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
