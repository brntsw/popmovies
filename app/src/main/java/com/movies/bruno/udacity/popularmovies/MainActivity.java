package com.movies.bruno.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.movies.bruno.udacity.popularmovies.adapter.MovieAdapter;
import com.movies.bruno.udacity.popularmovies.classes.Movie;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBTask;
import com.movies.bruno.udacity.popularmovies.util.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private GridView gridMovies;
    Toolbar toolbar;
    MovieAdapter adapter;
    //Spinner spinnerSort;

    private void listMovies(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.pref_name), Context.MODE_PRIVATE);
            int pref = sharedPref.getInt(getString(R.string.saved_sort_movie), -1);

            Log.d("Pref", pref+"");

            String query = "";
            if(pref == -1 || pref == R.id.radioMostPopular){
                query = "&sort_by=popularity.desc";
            }
            else if(pref == R.id.linearHighestRated){
                query = "&sort_by=vote_average.desc";
            }

            if(pref != R.id.radioFavorite) {
                try {
                    final ArrayList<Movie> movies = new TMDBTask().execute(query).get();

                    if (movies != null) {
                        adapter = new MovieAdapter(MainActivity.this, movies);

                        gridMovies.setAdapter(adapter);

                        gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                                Movie movie = (Movie) imageView.getTag();
                                //Iniciar outra activity MovieDetailsActivity, enviando o objeto movie
                                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                                intent.putExtra("movieId", movie.getId());
                                intent.putExtra("backdropPath", movie.getBackdropPath());
                                intent.putExtra("posterPath", movie.getPosterPath());
                                intent.putExtra("title", movie.getTitle());
                                intent.putExtra("released", movie.getReleaseDate());
                                intent.putExtra("voteAverage", movie.getVoteAverage());
                                intent.putExtra("overview", movie.getOverview());
                                startActivity(intent);
                            }
                        });

                        for (int i = 0; i < movies.size(); i++) {
                            Log.d("RESULT", "Popularity: " + movies.get(i).getPopularity());
                            Log.d("RESULT", "Vote: " + movies.get(i).getVoteAverage());
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.no_movies, Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            else{
                DatabaseOpenHelper db = new DatabaseOpenHelper(MainActivity.this);
                ArrayList<Movie> movies = db.getAllFavorites();

                if (movies != null) {
                    adapter = new MovieAdapter(MainActivity.this, movies);

                    gridMovies.setAdapter(adapter);

                    gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                            Movie movie = (Movie) imageView.getTag();
                            //Iniciar outra activity MovieDetailsActivity, enviando o objeto movie
                            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                            intent.putExtra("movieId", movie.getId());
                            intent.putExtra("backdropPath", movie.getBackdropPath());
                            intent.putExtra("posterPath", movie.getPosterPath());
                            intent.putExtra("title", movie.getTitle());
                            intent.putExtra("released", movie.getReleaseDate());
                            intent.putExtra("voteAverage", movie.getVoteAverage());
                            intent.putExtra("overview", movie.getOverview());
                            startActivity(intent);
                        }
                    });
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        initialize();

        listMovies();

        //spinnerSort = (Spinner) toolbar.findViewById(R.id.spinner_menu);
    }

    protected void onResume(){
        super.onResume();

        if(adapter != null){
            listMovies();
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
                //Call the SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
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
