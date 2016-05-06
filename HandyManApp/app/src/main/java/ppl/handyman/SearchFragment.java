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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.TreeSet;
import java.util.zip.Inflater;

/**
 * Created by Ari on 4/12/2016.
 */
public class SearchFragment extends Fragment {
    SQLiteHandler sql;
    private Button search;
    private GridView gridview;
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
        /**
         * TO DO:
         * WE NEED TO REFACTOR THIS CODE!!!
         */
        final Animation animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale);

        View view = inflater.inflate(R.layout.fragment_newsearch, container, false);
        search = (Button) view.findViewById(R.id.buttonNextOrder);
        gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(view.getContext()));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean[] flag= new boolean[9];
            @Override
            public void onItemClick(AdapterView<?> parent, View imgView, int position, long id) {

                    if (position == 0) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.ac_checked);
                                flag[position] = true;
                                pickedCategory.add("ac");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }

                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.ac);
                            flag[position] = false;
                            pickedCategory.remove("ac");
                        }
                    } else if (position == 1) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.air_checked);
                                flag[position] = true;
                                pickedCategory.add("air");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.air);
                            flag[position] = false;
                            pickedCategory.remove("air");
                        }
                    } else if (position == 2) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.bengkel_checked);
                                flag[position] = true;
                                pickedCategory.add("bengkel");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.bengkel);
                            flag[position] = false;
                            pickedCategory.remove("bengkel");
                        }
                    } else if (position == 3) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.besi_checked);
                                flag[position] = true;
                                pickedCategory.add("besi");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.besi);
                            flag[position] = false;
                            pickedCategory.remove("besi");
                        }
                    } else if (position == 4) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.cat_checked);
                                flag[position] = true;
                                pickedCategory.add("cat");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.cat);
                            flag[position] = false;
                            pickedCategory.remove("cat");
                        }
                    } else if (position == 5) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.kayu_checked);
                                flag[position] = true;
                                pickedCategory.add("kayu");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.kayu);
                            flag[position] = false;
                            pickedCategory.remove("kayu");
                        }
                    } else if (position == 6) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.listrik_checked);
                                flag[position] = true;
                                pickedCategory.add("listrik");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.listrik);
                            flag[position] = false;
                            pickedCategory.remove("listrik");
                        }
                    } else if (position == 7) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.taman_checked);
                                flag[position] = true;
                                pickedCategory.add("taman");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }

                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.taman);
                            flag[position] = false;
                            pickedCategory.remove("taman");
                        }
                    } else if (position == 8) {
                        if (!flag[position]) {
                            if(pickedCategory.size() < 2) {
                                ImageView imageView = (ImageView) imgView;
                                imageView.setImageResource(R.drawable.tembok_checked);
                                flag[position] = true;
                                pickedCategory.add("tembok");
                            } else {
                                Toast.makeText(getContext(),"You can only pick at most 2 different categories",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ImageView imageView = (ImageView) imgView;
                            imageView.setImageResource(R.drawable.tembok);
                            flag[position] = false;
                            pickedCategory.remove("tembok");
                        }
                    }

                }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save data to a bundle so data wont be destroyed
                v.startAnimation(animScale);
                if(pickedCategory.size()==0){
                    Toast.makeText(getActivity(),"You must choose at least 1 category",Toast.LENGTH_LONG).show();
                }else{
                    session.setPickedCategory(pickedCategory);
                    Intent intent = new Intent(getContext(),OrderActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }
}
