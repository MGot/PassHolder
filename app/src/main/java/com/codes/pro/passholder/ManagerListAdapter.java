package com.codes.pro.passholder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codes.pro.passholder.R;

import java.util.ArrayList;
import java.util.List;

public class ManagerListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname = new ArrayList<String>();

    public ManagerListAdapter(Activity context, ArrayList itemname) {
        super(context, R.layout.mylist);
// TODO Auto-generated constructor stub
        this.context=context;
        itemname.add(this);
    }
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);
        //TextView txtTitle = (TextView) rowView.findViewById(R.id.item);

        //txtTitle.setText(itemname[position]);

        return rowView;
    };
}