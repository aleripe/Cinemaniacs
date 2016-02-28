/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import it.returntrue.cinemaniacs.data.MoviesContract;
import it.returntrue.cinemaniacs.data.MoviesDbHelper;
import it.returntrue.cinemaniacs.provider.MoviesProvider;

public class MoviesProviderTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAll();
    }

    public void testGetType() {
        Uri moviesUri = MoviesProvider.buildMoviesUri();

        String type = mContext.getContentResolver().getType(moviesUri);

        // Check that returned ContentType is correct
        assertEquals("Return type is not " + MoviesProvider.MOVIE_DIR_CONTENT_TYPE, type, MoviesProvider.MOVIE_DIR_CONTENT_TYPE);
    }

    public void testMoviesQuery() {
        long id = insertMovie();

        Uri moviesUri = MoviesProvider.buildMoviesUri();
        Cursor cursor = mContext.getContentResolver().query(
                moviesUri,
                null,
                null,
                null,
                null
        );

        // Check if Movie has been created
        assertTrue("Provider should return at least one Movie.", cursor.moveToFirst());

        cursor.close();
    }

    private long insertMovie() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry._ID, 135397);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_TITLE, "Jurassic World");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_OVERVIEW, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, 2457185.5);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_POPULARITY, 88.551849);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_RATING, 7.1);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH, "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_COVER_PATH, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_NAME_IS_FAVORITE, false);

        return db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
    }

    private void deleteAll() {
        Uri moviesUri = MoviesProvider.buildMoviesUri();

        mContext.getContentResolver().delete(moviesUri, null, null);

        Cursor cursor = mContext.getContentResolver().query(
                moviesUri,
                null,
                null,
                null,
                null
        );

        // Check that all record were deleted from Movie table
        assertEquals("Some records were not deleted from Movie table.", 0, cursor.getCount());

        cursor.close();
    }
}