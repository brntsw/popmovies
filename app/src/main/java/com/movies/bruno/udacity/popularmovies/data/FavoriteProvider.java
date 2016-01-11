package com.movies.bruno.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by techresult on 05/01/2016.
 */
public class FavoriteProvider extends ContentProvider {

    private static final String LOG_TAG = FavoriteProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteDBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int FAVORITE = 1;
    private static final int FAVORITE_WITH_ID = 2;
    private static final int REVIEW = 3;
    private static final int REVIEW_WITH_ID = 4;

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, FavoriteContract.FavoriteEntry.TABLE_FAVORITES, FAVORITE);
        matcher.addURI(authority, FavoriteContract.FavoriteEntry.TABLE_FAVORITES + "/#", FAVORITE_WITH_ID);
        matcher.addURI(authority, FavoriteContract.FavoriteEntry.TABLE_REVIEWS, REVIEW);
        matcher.addURI(authority, FavoriteContract.FavoriteEntry.TABLE_REVIEWS + "/#", REVIEW_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoriteDBHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case FAVORITE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                return cursor;
            case FAVORITE_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_FAVORITES,
                        projection,
                        FavoriteContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );

                return cursor;

            case REVIEW:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_REVIEWS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                return cursor;

            case REVIEW_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_REVIEWS,
                        projection,
                        FavoriteContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );

                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITE:
                return FavoriteContract.FavoriteEntry.CONTENT_DIR_TYPE_FAVORITES;
            case FAVORITE_WITH_ID:
                return FavoriteContract.FavoriteEntry.CONTENT_ITEM_TYPE_FAVORITES;
            case REVIEW:
                return FavoriteContract.FavoriteEntry.CONTENT_DIR_TYPE_REVIEWS;
            case REVIEW_WITH_ID:
                return FavoriteContract.FavoriteEntry.CONTENT_ITEM_TYPE_REVIEWS;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case FAVORITE: {
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_FAVORITES, null, values);
                if (id > 0) {
                    returnUri = FavoriteContract.FavoriteEntry.buildFavoritedUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case REVIEW:{
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_REVIEWS, null, values);
                if (id > 0) {
                    returnUri = FavoriteContract.FavoriteEntry.buildReviewUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unkown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITE: {
                //multiple transactions
                db.beginTransaction();

                //keep track of successful inserts
                int numInserted = 0;
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long id = -1;
                        try {
                            id = db.insertOrThrow(FavoriteContract.FavoriteEntry.TABLE_FAVORITES, null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.v(LOG_TAG, "The value is already in database");
                        }

                        if (id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }

                if (numInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return numInserted;
            }
            case REVIEW: {
                //multiple transactions
                db.beginTransaction();

                //keep track of successful inserts
                int numInserted = 0;
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long id = -1;
                        try {
                            id = db.insertOrThrow(FavoriteContract.FavoriteEntry.TABLE_REVIEWS, null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.v(LOG_TAG, "The value is already in database");
                        }

                        if (id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }

                if (numInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return numInserted;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;

        switch (match){
            case FAVORITE:
                numDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_FAVORITES, selection, selectionArgs);
                //reset ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoriteContract.FavoriteEntry.TABLE_FAVORITES + "'");
                break;
            case FAVORITE_WITH_ID:
                numDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_FAVORITES,
                        FavoriteContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                //reset ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoriteContract.FavoriteEntry.TABLE_FAVORITES + "'");

                break;
            case REVIEW:
                numDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_REVIEWS, selection, selectionArgs);
                //reset ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoriteContract.FavoriteEntry.TABLE_REVIEWS + "'");
                break;
            case REVIEW_WITH_ID:
                numDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_REVIEWS,
                        FavoriteContract.FavoriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                //reset ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoriteContract.FavoriteEntry.TABLE_REVIEWS + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
