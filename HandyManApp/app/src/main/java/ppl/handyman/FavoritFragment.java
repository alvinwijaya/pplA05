package ppl.handyman;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Ari on 4/12/2016.
 */
public class FavoritFragment extends Fragment {
    public FavoritFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    public ArrayList<JSONArray> getWorkerThatHaveWorkedForMe(){

        return null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorit, container, false);
    }
}
