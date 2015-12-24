package com.movies.bruno.udacity.popularmovies.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.MatrixCursor;

import com.movies.bruno.udacity.popularmovies.classes.Movie;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by BRUNO on 24/12/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PopMovies.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "favorites";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME +
                                "(id INTEGER PRIMARY KEY," +
                                "backdrop_path TEXT," +
                                "poster_path TEXT," +
                                "title TEXT," +
                                "released TEXT," +
                                "vote_average REAL," +
                                "overview TEXT)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertFavorite(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", movie.getId());
        contentValues.put("backdrop_path", movie.getBackdropPath());
        contentValues.put("poster_path", movie.getPosterPath());
        contentValues.put("title", movie.getTitle());
        contentValues.put("released", movie.getReleaseDate());
        contentValues.put("vote_average", movie.getVoteAverage());
        contentValues.put("overview", movie.getOverview());

        db.insert(TABLE_NAME, null, contentValues);

        return true;
    }

    public Movie getMovie(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME
                            + " WHERE id = " + id;

        Cursor cursor =  db.rawQuery(sql, null);

        if(cursor.moveToNext()){
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndex("id")));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex("backdrop_path")));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex("poster_path")));
            movie.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex("released")));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex("vote_average")));
            movie.setOverview(cursor.getString(cursor.getColumnIndex("overview")));

            cursor.close();

            return movie;
        }
        else{
            return null;
        }

    }

    public ArrayList<Movie> getAllFavorites(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Movie> listFavorites = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor =  db.rawQuery(sql, null);

        while (cursor.moveToNext()){
            Movie movie = new Movie();

            movie.setId(cursor.getInt(cursor.getColumnIndex("id")));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex("backdrop_path")));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex("poster_path")));
            movie.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex("released")));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex("vote_average")));
            movie.setOverview(cursor.getString(cursor.getColumnIndex("overview")));

            listFavorites.add(movie);
        }

        cursor.close();

        return listFavorites;
    }

    public boolean deleteFavorite(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_NAME, "id = " + id, null) > 0;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
