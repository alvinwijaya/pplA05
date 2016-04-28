package ppl.handyman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.TreeSet;
import java.util.zip.Inflater;

/**
 * Created by Ari on 4/12/2016.
 */
public class SearchFragment extends Fragment {
    SQLiteHandler sql;
    private ImageView ac;
    private ImageView air;
    private ImageView bengkel;
    private ImageView besi;
    private ImageView cat;
    private ImageView kayu;
    private ImageView listrik;
    private ImageView taman;
    private ImageView tembok;
    private Button search;
    private TreeSet<String> pickedCategory;
    private SessionHandler session;

    public SearchFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getContext());
        sql = new SQLiteHandler(getContext());
        session = new SessionHandler(getContext());
        pickedCategory = new TreeSet<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ac = (ImageView) view.findViewById(R.id.ac);
        air = (ImageView) view.findViewById(R.id.air);
        bengkel = (ImageView) view.findViewById(R.id.bengkel);
        besi = (ImageView) view.findViewById(R.id.besi);
        cat = (ImageView) view.findViewById(R.id.cat);
        kayu = (ImageView) view.findViewById(R.id.kayu);
        listrik = (ImageView) view.findViewById(R.id.listrik);
        taman = (ImageView) view.findViewById(R.id.taman);
        tembok = (ImageView) view.findViewById(R.id.tembok);
        search = (Button) view.findViewById(R.id.button);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save data to a bundle so data wont be destroyed
                if(pickedCategory.size()==0){
                    Toast.makeText(getActivity(),"You must choose at least 1 category",Toast.LENGTH_LONG).show();
                }else{
                session.setPickedCategory(pickedCategory);
                Intent intent = new Intent(getContext(),OrderActivity.class);
                startActivity(intent);
                }
            }
        });
        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickedCategory.size() < 2){
                    pickedCategory.add("ac");
                }else{
                    Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                }
            }
        });
        air.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickedCategory.size() < 2){
                    pickedCategory.add("air");
                }
                else{
                    Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                }
            }
        });
        bengkel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickedCategory.size() < 2){
                    pickedCategory.add("bengkel");
                }
                else{
                    Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                }

            }
        });
        besi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickedCategory.size() < 2){
                    pickedCategory.add("besi");

                }
                else{
                    Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                }

            }
        });
        cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickedCategory.size() < 2){
                    pickedCategory.add("cat");

                }
                else{
                    Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                }

            }
        });
        kayu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedCategory.size() < 2) {
                    pickedCategory.add("kayu");

                } else {
                    Toast.makeText(getContext(), "You can only pick at most 2 different categories", Toast.LENGTH_LONG).show();
                }

            }
        });
        listrik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pickedCategory.size() < 2){
                    pickedCategory.add("listrik");

                }
                else{
                    Toast.makeText(getContext(), "You can only pick at most 2 different categories", Toast.LENGTH_LONG).show();
                }
            }
        });
        taman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedCategory.size() < 2) {
                    pickedCategory.add("taman");
                } else {
                    Toast.makeText(getContext(), "You can only pick at most 2 different categories", Toast.LENGTH_LONG).show();
                }

            }
        });
        tembok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedCategory.size() < 2) {
                    pickedCategory.add("tembok");

                } else {
                    Toast.makeText(getContext(), "You can only pick at most 2 different categories", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }
}
