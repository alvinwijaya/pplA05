package ppl.handyman.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ppl.handyman.AppController;
import ppl.handyman.R;
import ppl.handyman.custom_object.Worker;
import ppl.handyman.handler.SessionHandler;

/**
 * Created by Ari on 4/12/2016.
 */
public class ProfileFragment extends Fragment {
    private EditText phoneNumber;
    private EditText address;
    private SessionHandler session;
    private Button updateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        phoneNumber = (EditText) view.findViewById(R.id.phone_value);
        address = (EditText) view.findViewById(R.id.profile_address_value);
        updateBtn = (Button) view.findViewById(R.id.updateBtn);

        return view;
    }

    public void updateProfile(String newAddress, String newPhone){
        StringRequest request = new StringRequest(Request.Method.POST, "http://reyzan.cloudapp.net/HandyMan/user.php/getmeworker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getContext(),"your Profile was updated",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),"Something Wrong In Volley",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                Log.d("username",session.getUsername());
                String username = session.getUsername();
                String phone = phoneNumber.getText().toString();
                String adress = address.getText().toString();
                map.put("username", username);
                map.put("phone",phone);
                map.put("address",adress);
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
