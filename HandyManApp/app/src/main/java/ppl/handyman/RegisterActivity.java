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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private EditText inputUsername;
    private EditText inputName;
    private EditText inputAddress;
    private EditText inputPassword;
    private EditText inputPhone;
    private Button register;
    private TextView login;
    private ProgressDialog pDialog;
    //private SessionHandler session;
    private SQLiteHandler sqlhandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputUsername = (EditText) findViewById(R.id.username);
        inputName = (EditText) findViewById(R.id.name);
        inputAddress = (EditText) findViewById(R.id.address);
        inputPhone = (EditText) findViewById(R.id.phone);
        inputPassword = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        login = (TextView) findViewById(R.id.login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session Handler
        //session = new SessionHandler(getApplicationContext());
        sqlhandler = new SQLiteHandler(getApplicationContext());
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim();
                String name = inputName.getText().toString();
                String address = inputAddress.getText().toString();
                String phone = inputPhone.getText().toString();
                String password = inputPassword.getText().toString();

                if(!username.isEmpty() && !name.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !password.isEmpty()){
                    if(emailValidator(username) && password.length() > 7){
                        registerUser(username,name,password,phone,address);
                        Intent intent = new Intent(RegisterActivity.this,Login.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Email must be valid email address and password minimal 8 character long",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter your credential",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean emailValidator(String email){
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this,Login.class);
        startActivity(intent);
        finish();
    }

    public void registerUser(final String username,final String name, final String password,final String phone,final String address){

        pDialog.setMessage("Registering ...");
        showDialog();

        final StringRequest request = new StringRequest(Request.Method.POST,"http://reyzan.cloudapp.net/HandyMan/index.php/register", new Response.Listener<String>() {
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
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<String, String>();
                map.put("username",username);
                map.put("password",password);
                map.put("name",name);
                map.put("phone",phone);
                map.put("address",address);
                return map;
            }
        };
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

