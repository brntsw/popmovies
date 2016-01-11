package com.movies.bruno.udacity.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by techresult on 04/01/2016.
 */
public class FavoriteContract {

    public static final String CONTENT_AUTHORITY = "com.movies.bruno.udacity.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteEntry implements BaseColumns{
        //table names
        public static final String TABLE_FAVORITES = "favorites";
        public static final String TABLE_REVIEWS = "reviews";
        //general columns
        public static final String _ID = "_id";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        //columns favorites
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TRAILERS = "trailers";
        //columns reviews
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        //create content uri
        public static final Uri CONTENT_URI_FAVORITES = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FAVORITES).build();
        public static final Uri CONTENT_URI_REVIEWS = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_REVIEWS).build();

        //create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE_FAVORITES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;
        public static final String CONTENT_DIR_TYPE_REVIEWS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEWS;

        //create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE_FAVORITES = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;
        public static final String CONTENT_ITEM_TYPE_REVIEWS = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_REVIEWS;

        //For building Uri on insertion
        public static Uri buildFavoritedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI_FAVORITES, id);
        }

        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI_REVIEWS, id);
        }
    }

}
