package com.example.sarsoor.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by sarsoor on 16/09/2016.
 */
public class KeyAdapter extends BaseAdapter {
    Context context;
    String[] keys;
    public KeyAdapter(Context context, String[] keys) {
        this.context = context;
        this.keys = keys;
    }

    @Override
    public int getCount() {
        return keys.length;
    }

    @Override
    public Object getItem(int position) {
        return keys[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_trailer,parent, false);
        }
        TextView trailer = (TextView) convertView.findViewById(R.id.trailer_text);
        trailer.setText("Trailer "+(position+1));
        return convertView;
    }
}
