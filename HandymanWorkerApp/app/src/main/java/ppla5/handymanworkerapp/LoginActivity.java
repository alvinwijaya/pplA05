package ppla5.handymanworkerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private EditText inputUsername;
    private EditText inputPassword;
    private Button login;
    private SessionHandler session;
    private SQLiteHandler sqlhandler;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session Handler
        session = new SessionHandler(getApplicationContext());

        // SQLite database handler
        sqlhandler = new SQLiteHandler(getApplicationContext());
        if(session.isLoggedIn()){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }else {
            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.password);
            login = (Button) findViewById(R.id.login);
            if (login != null) {

                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = inputUsername.getText().toString().trim();
                        String password = inputPassword.getText().toString();
                        boolean emailValid = emailValidator(email);
                        boolean passwordValid = passwordValidator(password);
                        if(emailValid && passwordValid ){
                            authenticate(email,password);
                        }else {
                            Toast.makeText(getApplicationContext(),"Invalid email address or password is less than 8 character",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean emailValidator(String email){
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean passwordValidator(String password){
        return password.length() >= 8;
    }

    public void authenticate(final String username, final String password){
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/worker.php/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(LoginActivity.class.getSimpleName(), "Login Response: " + s.toString());
                hideDialog();

                try{
                    JSONObject json = new JSONObject(s);
                    boolean error = json.getBoolean("error");
                    if(!error){
                        session.setLogin(true);
                        String username = json.getString("username");
                        String password = json.getString("password");
                        String name = json.getString("name");
                        String photo = json.getString("photo");
                        String address = json.getString("address");
                        double latitude = json.getDouble("latitude");
                        double longitude = json.getDouble("longitude");
                        String tag = json.getString("tag");
                        double rating = json.getDouble("rating");
                        sqlhandler.addUser(username,password,name,photo,address,latitude,longitude,tag,rating);
                        Intent in = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(in);
                        finish();
                    }else {
                        String errMsg = json.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),"JSON Error " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"Something Wrong", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            // Posting params to register url
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<String, String>();
                map.put("username",username);
                map.put("password",password);
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
