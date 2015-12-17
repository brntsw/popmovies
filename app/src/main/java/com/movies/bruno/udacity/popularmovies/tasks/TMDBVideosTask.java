package com.movies.bruno.udacity.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by techresult on 17/12/2015.
 */
public class TMDBVideosTask extends AsyncTask<Integer, Void, ArrayList<String>> {

    private final String TMDB_API_KEY = "3d62bcc6c5dec00b1e0eb1532ad18bb1";

    @Override
    protected ArrayList<String> doInBackground(Integer... params) {
        int id = params[0];

        try {
            URL url = new URL("http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + TMDB_API_KEY);

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

                return parseResult(reader(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if(inputStream != null){
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<String> parseResult(String result){
        ArrayList<String> listKeys = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = (JSONArray) jsonObject.get("results");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonMovie = jsonArray.getJSONObject(i);

                listKeys.add(jsonMovie.getString("key"));
            }
        }
        catch (JSONException e){
            Log.e("Error JSON", "Error parsing JSON. String was: " + result);
        }

        return listKeys;
    }

    public String reader(InputStream stream) throws IOException{
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        return bufferedReader.readLine();
    }
}
