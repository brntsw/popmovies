package com.movies.bruno.udacity.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.movies.bruno.udacity.popularmovies.classes.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by techresult on 23/11/2015.
 */
public class TMDBTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private final String TMDB_API_KEY = "3d62bcc6c5dec00b1e0eb1532ad18bb1";

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        try{
            return searchMovie(params[0]);
        }
        catch(IOException e){
            return null;
        }
    }

    public ArrayList<Movie> searchMovie(String query) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.themoviedb.org/3/search/movie");
        stringBuilder.append("?api_key=" + TMDB_API_KEY);
        stringBuilder.append("&query=" + query);
        URL url = new URL(stringBuilder.toString());

        InputStream inputStream = null;

        try{
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept", "application/json"); // Required to get TMDB to play nicely.
            conn.setDoInput(true);
            conn.connect();

            int responseCode = conn.getResponseCode();
            Log.d("Response", "The response code is: " + responseCode + " " + conn.getResponseMessage());

            inputStream = conn.getInputStream();

            return parseResult(stringify(inputStream));
        }
        finally{
            if(inputStream != null){
                inputStream.close();
            }
        }
    }

    private ArrayList<Movie> parseResult(String result){
        ArrayList<Movie> results = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonMovie = jsonArray.getJSONObject(i);

                Movie movie = new Movie();
                movie.setTitle(jsonMovie.getString("title"));
                movie.setBackdropPath(jsonMovie.getString("backdrop_path"));
                movie.setOriginalTitle(jsonMovie.getString("original_title"));
                movie.setPopularity(jsonMovie.getString("popularity"));
                movie.setPosterPath(jsonMovie.getString("poster_path"));
                movie.setReleaseDate(jsonMovie.getString("release_date"));

                results.add(movie);
            }
        }
        catch (JSONException e){
            Log.e("Error JSON", "Error parsing JSON. String was: " + result);
        }

        return results;
    }

    public String stringify(InputStream stream) throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        return bufferedReader.readLine();
    }
}
