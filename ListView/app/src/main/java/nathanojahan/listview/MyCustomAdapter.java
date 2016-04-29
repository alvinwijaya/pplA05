package nathanojahan.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by asus PC on 28/04/2016.
 */
public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;


    public MyCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.order_list, null);
        }

        //Handle TextView and display string from your list to the TextView (order)in order_list
        TextView listItemText = (TextView)view.findViewById(R.id.order);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        final ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.cancel_order);
        ImageButton addBtn = (ImageButton)view.findViewById(R.id.accept_order);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                Toast.makeText(parent.getContext(), "Pesanan Telah Ditolak", Toast.LENGTH_SHORT).show();
                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                Toast.makeText(parent.getContext(), "Pesanan Telah Diterima", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}