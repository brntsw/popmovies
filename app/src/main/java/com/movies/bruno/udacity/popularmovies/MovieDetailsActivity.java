package com.movies.bruno.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.bruno.udacity.popularmovies.adapter.VideoAdapter;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBVideosTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MovieDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    private ImageView backdropView;
    private ImageView posterView;
    private TextView titleView;
    private TextView releasedView;
    private TextView starText;
    private TextView overviewView;
    private RecyclerView recyclerVideos;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initializeComponents();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.details_title);
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.abc_ic_ab_back_mtrl_am_alpha, null));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Get the Bundle sent from the MainActivity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            int id = extras.getInt("movieId");
            String backdropPath = extras.getString("backdropPath");
            String posterPath = extras.getString("posterPath");
            String title = extras.getString("title");
            String released = extras.getString("released");
            double voteAverage = extras.getDouble("voteAverage");
            String overview = extras.getString("overview");

            try {
                ArrayList<String> listKeys = new TMDBVideosTask().execute(id).get();
                VideoAdapter adapter = new VideoAdapter(MovieDetailsActivity.this, listKeys, R.layout.item_movie_trailer);
                //recyclerVideos.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerVideos.setAdapter(adapter);
                recyclerVideos.setLayoutManager(llm);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Picasso.with(MovieDetailsActivity.this).load("http://image.tmdb.org/t/p/w185/" + backdropPath).into(backdropView);
            Picasso.with(MovieDetailsActivity.this).load("http://image.tmdb.org/t/p/w185/" + posterPath).into(posterView);
            titleView.setText(title);
            releasedView.setText(released);
            starText.setText(String.valueOf(voteAverage));
            overviewView.setText(overview);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                //Call the SettingsActivity
                Intent intent = new Intent(MovieDetailsActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeComponents(){
        backdropView = (ImageView) findViewById(R.id.backdropView);
        posterView = (ImageView) findViewById(R.id.posterView);
        titleView = (TextView) findViewById(R.id.titleView);
        releasedView = (TextView) findViewById(R.id.releasedView);
        starText = (TextView) findViewById(R.id.starText);
        overviewView = (TextView) findViewById(R.id.overviewView);
        recyclerVideos = (RecyclerView) findViewById(R.id.recyclerVideos);
    }
}
