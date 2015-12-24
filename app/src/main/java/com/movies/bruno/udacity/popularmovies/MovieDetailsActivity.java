package com.movies.bruno.udacity.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.bruno.udacity.popularmovies.classes.Movie;
import com.movies.bruno.udacity.popularmovies.classes.Review;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBReviewsTask;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBVideosTask;
import com.movies.bruno.udacity.popularmovies.util.Constants;
import com.movies.bruno.udacity.popularmovies.util.DatabaseOpenHelper;
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
    private ImageView imgFavorite;

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
            final int id = extras.getInt("movieId");
            final String backdropPath = extras.getString("backdropPath");
            final String posterPath = extras.getString("posterPath");
            final String title = extras.getString("title");
            final String released = extras.getString("released");
            final double voteAverage = extras.getDouble("voteAverage");
            final String overview = extras.getString("overview");

            //Get the video keys from the webservice and, when a trailer is clicked, the user is redirected to the Youtube video
            try {
                final ArrayList<String> listKeys = new TMDBVideosTask().execute(id).get();

                LinearLayout linearTrailers = (LinearLayout) findViewById(R.id.linearTrailers);
                LayoutInflater inflater = LayoutInflater.from(this);
                for(int i = 0; i < listKeys.size(); i++){
                    View view = inflater.inflate(R.layout.item_movie_trailer, linearTrailers, false);
                    TextView text = (TextView) view.findViewById(R.id.textTrailer);
                    text.setText("Trailer " + (i + 1));
                    linearTrailers.addView(view);
                }

                for(int i = 0; i < linearTrailers.getChildCount(); i++){
                    final int num = i;
                    linearTrailers.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_VIDEO_URL + listKeys.get(num)));
                            youTubeIntent.putExtra("force_fullscreen", true);
                            startActivity(youTubeIntent);
                        }
                    });
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


            //Get the reviews from the webservice and displays it (author and content)
            try {
                final ArrayList<Review> listReviews = new TMDBReviewsTask().execute(id).get();

                LinearLayout linearReviews = (LinearLayout) findViewById(R.id.linearReviews);
                LayoutInflater inflater = LayoutInflater.from(this);
                for(int i = 0; i < listReviews.size(); i++){
                    View view = inflater.inflate(R.layout.item_review, linearReviews, false);
                    TextView tvAuthor = (TextView) view.findViewById(R.id.tvAuthorName);
                    tvAuthor.setText(listReviews.get(i).getAuthor());
                    TextView tvContent = (TextView) view.findViewById(R.id.tvContentText);
                    tvContent.setText(listReviews.get(i).getContent());
                    linearReviews.addView(view);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Picasso.with(MovieDetailsActivity.this).load("http://image.tmdb.org/t/p/w185/" + backdropPath).into(backdropView);
            Picasso.with(MovieDetailsActivity.this).load("http://image.tmdb.org/t/p/w185/" + posterPath).into(posterView);
            titleView.setText(title);
            releasedView.setText(released);
            starText.setText(String.valueOf(voteAverage));
            overviewView.setText(overview);

            DatabaseOpenHelper db = new DatabaseOpenHelper(MovieDetailsActivity.this);
            Movie movie = db.getMovie(id);

            if(movie != null){
                imgFavorite.setImageResource(R.drawable.favorite_red);
                imgFavorite.setTag(R.drawable.favorite_red);
            }
            else{
                imgFavorite.setImageResource(R.drawable.favorite);
                imgFavorite.setTag(R.drawable.favorite);
            }

            imgFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(String.valueOf(imgFavorite.getTag())) == R.drawable.favorite){
                        imgFavorite.setImageResource(R.drawable.favorite_red);
                        imgFavorite.setTag(R.drawable.favorite_red);
                        Toast.makeText(MovieDetailsActivity.this, "Movie added to the favorites!", Toast.LENGTH_SHORT).show();
                        //Store it on SQLite database

                        Movie movie = new Movie();
                        movie.setId(id);
                        movie.setBackdropPath(backdropPath);
                        movie.setPosterPath(posterPath);
                        movie.setTitle(title);
                        movie.setReleaseDate(released);
                        movie.setVoteAverage(voteAverage);
                        movie.setOverview(overview);

                        DatabaseOpenHelper db = new DatabaseOpenHelper(MovieDetailsActivity.this);
                        db.insertFavorite(movie);
                    }
                    else{
                        imgFavorite.setImageResource(R.drawable.favorite);
                        imgFavorite.setTag(R.drawable.favorite);
                        Toast.makeText(MovieDetailsActivity.this, "Movie removed from the favorites!", Toast.LENGTH_SHORT).show();
                        //Delete it from the SQLite database
                        DatabaseOpenHelper db = new DatabaseOpenHelper(MovieDetailsActivity.this);
                        db.deleteFavorite(id);
                    }
                }
            });
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
        imgFavorite = (ImageView) findViewById(R.id.imgFavorite);
    }
}
