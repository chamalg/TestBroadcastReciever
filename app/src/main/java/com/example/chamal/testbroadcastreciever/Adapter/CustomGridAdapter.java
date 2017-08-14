package com.example.chamal.testbroadcastreciever.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.chamal.testbroadcastreciever.R;

import java.util.ArrayList;

/**
 * Created by Chamal on 12/15/2016.
 */

public class CustomGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> list;
    LayoutInflater inflater;

    public CustomGridAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        View gridView = convertView;
        if (gridView == null) {
            holder = new Holder();
            gridView = inflater.inflate(R.layout.grid_layout_item, null);
            holder.tvName = (TextView) gridView.findViewById(R.id.tvGridName);
            gridView.setTag(holder);
        }else{
            holder = (Holder)gridView.getTag();
        }
            holder.tvName.setText(list.get(position).toString());

        return gridView;
    }

    class Holder {
        TextView tvName;
    }
}
