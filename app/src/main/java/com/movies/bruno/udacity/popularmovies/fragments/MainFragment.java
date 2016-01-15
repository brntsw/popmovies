package com.movies.bruno.udacity.popularmovies.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.movies.bruno.udacity.popularmovies.MovieDetailsActivity;
import com.movies.bruno.udacity.popularmovies.R;
import com.movies.bruno.udacity.popularmovies.adapter.MovieAdapter;
import com.movies.bruno.udacity.popularmovies.classes.Movie;
import com.movies.bruno.udacity.popularmovies.classes.Review;
import com.movies.bruno.udacity.popularmovies.data.FavoriteContract;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBReviewsTask;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBTask;
import com.movies.bruno.udacity.popularmovies.util.NetworkUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private GridView gridMovies;
    MovieAdapter adapter;

    boolean isTablet;

    public MainFragment() {
        // Required empty public constructor
    }

    private void listMovies(){
        NetworkUtil networkUtil = new NetworkUtil(getActivity());
        boolean hasNetworkAvailable = networkUtil.getNetworkConnection();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_name), Context.MODE_PRIVATE);
        int pref = sharedPref.getInt(getString(R.string.saved_sort_movie), -1);

        Log.d("Pref", pref + "");

        String query = "";
        if(pref == -1 || pref == R.id.radioMostPopular){
            query = "&sort_by=popularity.desc";
        }
        else if(pref == R.id.radioHighestRated){
            query = "&sort_by=vote_average.desc";
        }

        if(pref != R.id.radioFavorite) {
            if(hasNetworkAvailable) {
                try {
                    final ArrayList<Movie> movies = new TMDBTask().execute(query).get();

                    if (movies != null) {
                        adapter = new MovieAdapter(getActivity(), movies);

                        gridMovies.setAdapter(adapter);

                        if (!isTablet) {

                            gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                                    Movie movie = (Movie) imageView.getTag();
                                    //Iniciar outra activity MovieDetailsActivity, enviando o objeto movie
                                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                                    intent.putExtra("movieId", movie.getId());
                                    intent.putExtra("backdropPath", movie.getBackdropPath());
                                    intent.putExtra("posterPath", movie.getPosterPath());
                                    intent.putExtra("title", movie.getTitle());
                                    intent.putExtra("released", movie.getReleaseDate());
                                    intent.putExtra("voteAverage", movie.getVoteAverage());
                                    intent.putExtra("overview", movie.getOverview());

                                    try {
                                        ArrayList<Review> reviews = new TMDBReviewsTask().execute(movie.getId()).get();
                                        intent.putExtra("reviews", reviews);
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                    startActivity(intent);
                                }
                            });
                        } else {

                            gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        FragmentManager fm = getFragmentManager();

                                        ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                                        Movie movie = (Movie) imageView.getTag();

                                        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("movieId", movie.getId());
                                        bundle.putString("backdropPath", movie.getBackdropPath());
                                        bundle.putString("posterPath", movie.getPosterPath());
                                        bundle.putString("title", movie.getTitle());
                                        bundle.putString("released", movie.getReleaseDate());
                                        bundle.putDouble("voteAverage", movie.getVoteAverage());
                                        bundle.putString("overview", movie.getOverview());

                                        try {
                                            ArrayList<Review> reviews = new TMDBReviewsTask().execute(movie.getId()).get();
                                            bundle.putParcelableArrayList("reviews", reviews);
                                        } catch (InterruptedException | ExecutionException e) {
                                            e.printStackTrace();
                                        }

                                        movieDetailsFragment.setArguments(bundle);

                                        FragmentTransaction ft2 = fm.beginTransaction();
                                        ft2.replace(R.id.frag_details, movieDetailsFragment);
                                        ft2.commit();
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "Msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        for (int i = 0; i < movies.size(); i++) {
                            Log.d("RESULT", "Popularity: " + movies.get(i).getPopularity());
                            Log.d("RESULT", "Vote: " + movies.get(i).getVoteAverage());
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.no_movies, Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Cursor cursor = getActivity().getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI_FAVORITES,
                    new String[]{
                            FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE,
                            FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                            FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_PATH,
                            FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                            FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                            FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE,
                            FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW,
                            FavoriteContract.FavoriteEntry.COLUMN_TRAILERS
                    },
                    null,
                    null,
                    FavoriteContract.FavoriteEntry.COLUMN_TITLE
            );

            ArrayList<Movie> movies = new ArrayList<>();

            if(cursor.getCount() > 0){
                while(cursor.moveToNext()){
                    Movie movie = new Movie();
                    movie.setId(cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)));
                    movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_PATH)));
                    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH)));
                    movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE)));
                    movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE)));
                    movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW)));

                    movies.add(movie);
                }
            }

            cursor.close();

            adapter = new MovieAdapter(getActivity(), movies);

            gridMovies.setAdapter(adapter);

            if(!isTablet) {
                gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                        Movie movie = (Movie) imageView.getTag();
                        //Iniciar outra activity MovieDetailsActivity, enviando o objeto movie
                        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
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
            else{
                gridMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            FragmentManager fm = getFragmentManager();

                            ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                            Movie movie = (Movie) imageView.getTag();

                            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

                            Bundle bundle = new Bundle();
                            bundle.putInt("movieId", movie.getId());
                            bundle.putString("backdropPath", movie.getBackdropPath());
                            bundle.putString("posterPath", movie.getPosterPath());
                            bundle.putString("title", movie.getTitle());
                            bundle.putString("released", movie.getReleaseDate());
                            bundle.putDouble("voteAverage", movie.getVoteAverage());
                            bundle.putString("overview", movie.getOverview());

                            movieDetailsFragment.setArguments(bundle);

                            FragmentTransaction ft2 = fm.beginTransaction();
                            ft2.replace(R.id.frag_details, movieDetailsFragment);
                            ft2.commit();
                        }
                        catch(Exception e){
                            Toast.makeText(getActivity(), "Msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            for (int i = 0; i < movies.size(); i++) {
                Log.d("RESULT", "Popularity: " + movies.get(i).getPopularity());
                Log.d("RESULT", "Vote: " + movies.get(i).getVoteAverage());
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateView", "OnCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        gridMovies = (GridView) view.findViewById(R.id.gridMovies);

        listMovies();

        return view;
    }

    public void onResume(){
        super.onResume();

        if(adapter != null){
            listMovies();
        }
    }


}
