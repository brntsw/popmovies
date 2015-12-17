package com.movies.bruno.udacity.popularmovies.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.bruno.udacity.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by techresult on 17/12/2015.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<String> listKeys = new ArrayList<>();
    private int itemLayout;

    public VideoAdapter(Activity a, ArrayList<String> listKeys, int itemLayout){
        this.activity = a;
        this.listKeys = listKeys;
        this.itemLayout = itemLayout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.ViewHolder holder, int position) {
        holder.text.setText("Trailer " + position);
    }

    @Override
    public int getItemCount() {
        return listKeys.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imgPlay);
            text = (TextView) itemView.findViewById(R.id.textTrailer);
        }
    }
}
