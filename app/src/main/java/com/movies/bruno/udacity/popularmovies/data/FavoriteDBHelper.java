package com.movies.bruno.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by techresult on 04/01/2016.
 */
public class FavoriteDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FavoriteDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoriteContract.FavoriteEntry.TABLE_FAVORITES + "(" + FavoriteContract.FavoriteEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteContract.FavoriteEntry.COLUMN_ID_MOVIE + " INTEGER NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_TRAILERS + " TEXT NOT NULL);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_FAVORITES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                FavoriteContract.FavoriteEntry.TABLE_FAVORITES + "'");


        // re-create database
        onCreate(db);
    }
}
