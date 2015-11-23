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
import android.widget.GridView;
import android.widget.Toast;

import com.movies.bruno.udacity.popularmovies.classes.Movie;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private GridView gridMovies;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        initialize();
    }

    protected void onResume(){
        super.onResume();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            try{
                ArrayList<Movie> movies = new TMDBTask().execute("com.daginge.tmdbsearch.QUERY").get();
                if(movies != null){
                    for(int i = 0; i < movies.size(); i++){
                        Log.d("RESULT", movies.get(i).getOriginalTitle());
                    }
                }
                else{
                    Log.d("RESULT", "Empty");
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
