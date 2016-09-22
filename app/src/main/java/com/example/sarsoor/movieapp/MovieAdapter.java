package com.example.sarsoor.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by sarsoor on 16/09/2016.
 */
public class MovieAdapter extends BaseAdapter {

    Context context;
    MovieData [] movieData;
    public MovieAdapter(Context context, MovieData [] movieData) {
        this.context = context;
        this.movieData = movieData;
    }


    @Override
    public int getCount() {
        return movieData.length;
    }

    @Override
    public Object getItem(int position) {
        return movieData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_movie,parent,false);
        }
        ImageView imgView = (ImageView) convertView.findViewById(R.id.list_item_movie_imageView);
        String baseUrl = "http://image.tmdb.org/t/p/w185";
        String poster_url = baseUrl+movieData[position].getPoster_url();
        Picasso.with(context).load(poster_url).into(imgView);
        return convertView;

    }
}
