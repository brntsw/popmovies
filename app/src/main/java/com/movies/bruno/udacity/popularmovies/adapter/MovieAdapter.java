package com.movies.bruno.udacity.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.movies.bruno.udacity.popularmovies.R;
import com.movies.bruno.udacity.popularmovies.classes.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by techresult on 30/11/2015.
 */
public class MovieAdapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater = null;
    private ArrayList<Movie> listMovies = new ArrayList<>();

    public MovieAdapter(Activity a, ArrayList<Movie> listMovies){
        this.activity = a;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listMovies = listMovies;
    }

    @Override
    public int getCount() {
        return listMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return listMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView imageView;

        Movie movie = listMovies.get(position);

        if(convertView == null){
            v = inflater.inflate(R.layout.image_movie, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
        }

        imageView = (ImageView) v.getTag(R.id.picture);

        //IMPLEMENT THE ASYNCTASK TO DOWNLOAD THE IMAGE FROM SERVER
        //Details: http://stackoverflow.com/questions/5776851/load-image-from-url

        Picasso.with(activity).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath()).into(imageView);

        return v;
    }
}
