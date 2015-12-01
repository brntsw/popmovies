package com.movies.bruno.udacity.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.movies.bruno.udacity.popularmovies.adapter.MovieAdapter;
import com.movies.bruno.udacity.popularmovies.classes.Movie;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private GridView gridMovies;
    Toolbar toolbar;
    Spinner spinnerSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        initialize();

        spinnerSort = (Spinner) toolbar.findViewById(R.id.spinner_menu);
    }

    protected void onResume(){
        super.onResume();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String query = "";

                    if(position == 1){ //Popularity
                        //When selecting Sort by Popularity, the query is &sort_by=popularity.desc
                        query += "&sort_by=popularity.desc";
                    }
                    else if(position == 2){ //Highest-rated
                        //When selecting Sort by Highest-rated, the query is &sort_by=vote_average.desc
                        query += "&sort_by=vote_average.desc";
                    }

                    try {
                        ArrayList<Movie> movies = new TMDBTask().execute(query).get();

                        if(movies != null){
                            MovieAdapter adapter = new MovieAdapter(MainActivity.this, movies);

                            gridMovies.setAdapter(adapter);

                            for(int i = 0; i < movies.size(); i++){
                                Log.d("RESULT", "Popularity: " + movies.get(i).getPopularity());
                                Log.d("RESULT", "Vote: " + movies.get(i).getVoteAverage());
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, R.string.no_movies, Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            try{
                ArrayList<Movie> movies = new TMDBTask().execute("all").get();
                if(movies != null){
                    for(int i = 0; i < movies.size(); i++){
                        Log.d("RESULT", movies.get(i).getOriginalTitle());
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, R.string.no_movies, Toast.LENGTH_SHORT).show();
                }
            }
            catch(InterruptedException | ExecutionException e){
                Log.e("Error", e.getMessage());
            }

        }
        else{
            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                return true;
            case R.id.action_about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialize(){
        gridMovies = (GridView) findViewById(R.id.gridMovies);
    }
}
