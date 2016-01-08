package com.movies.bruno.udacity.popularmovies.fragments;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.bruno.udacity.popularmovies.R;
import com.movies.bruno.udacity.popularmovies.classes.Review;
import com.movies.bruno.udacity.popularmovies.data.FavoriteContract;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBReviewsTask;
import com.movies.bruno.udacity.popularmovies.tasks.TMDBVideosTask;
import com.movies.bruno.udacity.popularmovies.util.Constants;
import com.movies.bruno.udacity.popularmovies.util.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    private ImageView backdropView;
    private ImageView posterView;
    private TextView titleView;
    private TextView releasedView;
    private TextView starText;
    private TextView overviewView;
    private ImageView imgFavorite;
    private String trailers;


    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    private void buildMovie(LayoutInflater inflater, View view, Bundle bundle){
        NetworkUtil networkUtil = new NetworkUtil(getActivity());
        boolean hasNetworkAvailable = networkUtil.getNetworkConnection();

        final int id = bundle.getInt("movieId");
        final String backdropPath = bundle.getString("backdropPath");
        final String posterPath = bundle.getString("posterPath");
        final String title = bundle.getString("title");
        final String released = bundle.getString("released");
        final double voteAverage = bundle.getDouble("voteAverage");
        final String overview = bundle.getString("overview");

        if(hasNetworkAvailable) {
            trailers = "";

            //Get the video keys from the webservice and, when a trailer is clicked, the user is redirected to the Youtube video
            try {
                final ArrayList<String> listKeys = new TMDBVideosTask().execute(id).get();

                LinearLayout linearTrailers = (LinearLayout) view.findViewById(R.id.linearTrailers);
                for (int i = 0; i < listKeys.size(); i++) {
                    View viewItemTrailer = inflater.inflate(R.layout.item_movie_trailer, linearTrailers, false);
                    TextView text = (TextView) viewItemTrailer.findViewById(R.id.textTrailer);
                    text.setText("Trailer " + (i + 1));
                    linearTrailers.addView(viewItemTrailer);
                }

                for (int i = 0; i < linearTrailers.getChildCount(); i++) {
                    final int num = i;
                    linearTrailers.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_VIDEO_URL + listKeys.get(num)));
                            youTubeIntent.putExtra("force_fullscreen", true);
                            startActivity(youTubeIntent);
                        }
                    });

                    trailers += listKeys.get(num) + ",";
                }

                trailers = trailers.substring(0, trailers.length() - 1);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


            //Get the reviews from the webservice and displays it (author and content)
            try {
                final ArrayList<Review> listReviews = new TMDBReviewsTask().execute(id).get();

                LinearLayout linearReviews = (LinearLayout) view.findViewById(R.id.linearReviews);
                for (int i = 0; i < listReviews.size(); i++) {
                    View viewItemReview = inflater.inflate(R.layout.item_review, linearReviews, false);
                    TextView tvAuthor = (TextView) viewItemReview.findViewById(R.id.tvAuthorName);
                    tvAuthor.setText(listReviews.get(i).getAuthor());
                    TextView tvContent = (TextView) viewItemReview.findViewById(R.id.tvContentText);
                    tvContent.setText(listReviews.get(i).getContent());
                    linearReviews.addView(viewItemReview);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + backdropPath).into(backdropView);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + posterPath).into(posterView);
            titleView.setText(title);
            releasedView.setText(released);
            starText.setText(String.valueOf(voteAverage));
            overviewView.setText(overview);

            imgFavorite.setTag(R.drawable.favorite);

            Cursor cursor = getActivity().getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                                                    new String[]{},
                                                    FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE + " = ?",
                                                    new String[]{String.valueOf(id)},
                                                    FavoriteContract.FavoriteEntry.COLUMN_TITLE);

            if(cursor.getCount() > 0){
                imgFavorite.setTag(R.drawable.favorite_red);
                imgFavorite.setImageResource(R.drawable.favorite_red);
            }

            cursor.close();

            final String finalTrailers = trailers;
            imgFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(String.valueOf(imgFavorite.getTag())) == R.drawable.favorite) {
                        imgFavorite.setImageResource(R.drawable.favorite_red);
                        imgFavorite.setTag(R.drawable.favorite_red);
                        Toast.makeText(getActivity(), "Movie added to the favorites!", Toast.LENGTH_SHORT).show();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE, id);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, title);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_PATH, backdropPath);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH, posterPath);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, released);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, overview);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TRAILERS, finalTrailers);

                        getActivity().getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
                    } else {
                        imgFavorite.setImageResource(R.drawable.favorite);
                        imgFavorite.setTag(R.drawable.favorite);
                        Toast.makeText(getActivity(), "Movie removed from the favorites!", Toast.LENGTH_SHORT).show();

                        getActivity().getContentResolver().delete(FavoriteContract.FavoriteEntry.CONTENT_URI,
                                FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE + " = ?", new String[]{String.valueOf(id)});
                    }
                }
            });
        }
        else{

            Cursor cursor = getActivity().getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                    new String[]{
                            FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE,
                            FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                            FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_PATH,
                            FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                            FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE,
                            FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW,
                            FavoriteContract.FavoriteEntry.COLUMN_TRAILERS
                    },
                    FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE + " = ?",
                    new String[]{String.valueOf(id)},
                    FavoriteContract.FavoriteEntry.COLUMN_TITLE);

            titleView.setText(title);
            releasedView.setText(released);
            starText.setText(String.valueOf(voteAverage));
            overviewView.setText(overview);

            Log.d("TAM", cursor.getCount() + "");

            if(cursor.getCount() > 0){
                String trailers = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TRAILERS));
                final String[] auxTrailers = trailers.split(",");

                LinearLayout linearTrailers = (LinearLayout) view.findViewById(R.id.linearTrailers);

                for(int i = 0; i < auxTrailers.length; i++){
                    View viewItemTrailer = inflater.inflate(R.layout.item_movie_trailer, linearTrailers, false);
                    TextView text = (TextView) viewItemTrailer.findViewById(R.id.textTrailer);
                    text.setText("Trailer " + (i + 1));
                    linearTrailers.addView(viewItemTrailer);
                }

                for (int i = 0; i < linearTrailers.getChildCount(); i++) {
                    final int num = i;
                    linearTrailers.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_VIDEO_URL + auxTrailers[num]));
                            youTubeIntent.putExtra("force_fullscreen", true);
                            startActivity(youTubeIntent);
                        }
                    });
                }
            }

            cursor.close();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        backdropView = (ImageView) view.findViewById(R.id.backdropView);
        posterView = (ImageView) view.findViewById(R.id.posterView);
        titleView = (TextView) view.findViewById(R.id.titleView);
        releasedView = (TextView) view.findViewById(R.id.releasedView);
        starText = (TextView) view.findViewById(R.id.starText);
        overviewView = (TextView) view.findViewById(R.id.overviewView);
        imgFavorite = (ImageView) view.findViewById(R.id.imgFavorite);

        //Get the Bundle sent from the MainActivity
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            buildMovie(inflater, view, extras);
        }
        else{
            Bundle bundle = getArguments();

            if(bundle != null){
                buildMovie(inflater, view, bundle);
            }
        }

        return view;
    }


}
