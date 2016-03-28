package ppl.handyman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {
    private EditText inputUsername;
    private EditText inputName;
    private EditText inputAddress;
    private EditText inputPassword;
    private Button register;
    private TextView login;
    private ProgressDialog pDialog;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputUsername = (EditText) findViewById(R.id.username);
        inputName = (EditText) findViewById(R.id.name);
        inputAddress = (EditText) findViewById(R.id.address);
        inputPassword = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        login = (TextView) findViewById(R.id.login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session Handler
        session = new SessionHandler(getApplicationContext());


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputUsername.getText().toString().trim();
                String name = inputName.getText().toString();
                String address = inputAddress.getText().toString();
                String password = inputPassword.getText().toString();

                if(!email.isEmpty() && !name.isEmpty() && !address.isEmpty() && !password.isEmpty()){
                    registerUser(email,name,address,password);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter your credential",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void registerUser(final String username,final String name,final String address,final String password){

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest request = new StringRequest(Request.Method.POST,"http://192.168.43.229/HandyMan/index.php/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(RegisterActivity.class.getSimpleName(), "Register Response: " + response.toString());
                hideDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Something Wrong",Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }
    private void showDialog() {
    if (!pDialog.isShowing())
            pDialog.show();
}

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

