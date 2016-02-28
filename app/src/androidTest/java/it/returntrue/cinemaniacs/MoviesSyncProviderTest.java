/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import it.returntrue.cinemaniacs.provider.MoviesProvider;
import it.returntrue.cinemaniacs.sync.MoviesSyncAdapter;

public class MoviesSyncProviderTest extends AndroidTestCase {
    public void testSyncAdapter() {
        MoviesSyncAdapter.syncImmediately(getContext());

        Uri genresUri = MoviesProvider.buildGenresUri();

        Cursor cursor = getContext().getContentResolver().query(
                genresUri,
                null,
                null,
                null,
                null
        );

        // Check that Genre data is available
        assertTrue("Genre data has not been synchronized.", cursor.moveToFirst());

        Uri moviesUri = MoviesProvider.buildMoviesUri();

        cursor = getContext().getContentResolver().query(
                moviesUri,
                null,
                null,
                null,
                null
        );

        // Check that Movie data is available
        assertTrue("Movie data has not been synchronized.", cursor.moveToFirst());

        cursor.close();
    }
}
