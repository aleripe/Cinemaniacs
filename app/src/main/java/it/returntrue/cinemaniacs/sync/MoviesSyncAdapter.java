/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import it.returntrue.cinemaniacs.BuildConfig;
import it.returntrue.cinemaniacs.R;
import it.returntrue.cinemaniacs.api.TheMovieDbApi;
import it.returntrue.cinemaniacs.api.TheMovieDbApi.Genre;
import it.returntrue.cinemaniacs.api.TheMovieDbApi.Movie;
import it.returntrue.cinemaniacs.api.TheMovieDbApi.MovieReview;
import it.returntrue.cinemaniacs.api.TheMovieDbApi.MovieVideo;
import it.returntrue.cinemaniacs.data.MovieData;
import it.returntrue.cinemaniacs.data.MoviesContract.GenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieGenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieReviewEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieVideoEntry;
import it.returntrue.cinemaniacs.provider.MoviesProvider;
import it.returntrue.cinemaniacs.utilities.Preferences;

/**
 * Provides an implementation of the synchronization adapter
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String SYNC_COMPLETED = "it.returntrue.cinemaniacs.SYNC_COMPLETED";

    private static final String TAG = MoviesSyncAdapter.class.getSimpleName();
    private static final long SECONDS_PER_MINUTE = 60;
    private static final long MINUTES_PER_HOUR = 60;
    private static final long HOURS_PER_DAY = 24;
    private static final long SYNC_INTERVAL = SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY;

    private final ContentResolver mContentResolver;
    private final TheMovieDbApi mTheMovieDbApi;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mTheMovieDbApi = new TheMovieDbApi(BuildConfig.THE_MOVIE_DB_API_KEY);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        HashMap<Integer, Boolean> moviesFavoriteStates = getMoviesFavoriteStates();

        addGenres(mTheMovieDbApi.getGenres());
        addMovies(mTheMovieDbApi.getPopularMovies(), moviesFavoriteStates);
        addMovies(mTheMovieDbApi.getTopRatedMovies(), moviesFavoriteStates);

        // Broadcast that sync operation has been completed
        Intent intent = new Intent(SYNC_COMPLETED);
        getContext().sendBroadcast(intent);

        // Sets sync status as completed (first and subsequent)
        Preferences.setIsFirstSyncCompleted(getContext(), true);
    }

    /** Initializes sync adapter account */
    public static void initialize(Context context) {
        getSyncAccount(context);
    }

    /** Executes an immediate synchronization */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_provider_authority), bundle);
    }

    /** Gets the authentication account (creating it, if necessary) */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_provider_account_type));

        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.e(TAG, "Could not add SyncAdapter account.");
                return null;
            }

            configurePeriodicSync(context, newAccount);
        }

        return newAccount;
    }

    /** Configures a periodic synchronization */
    private static void configurePeriodicSync(Context context, Account account) {
        ContentResolver.addPeriodicSync(
                account,
                context.getString(R.string.content_provider_authority),
                Bundle.EMPTY,
                SYNC_INTERVAL);

        ContentResolver.setSyncAutomatically(
                account,
                context.getString(R.string.content_provider_authority),
                true
        );
    }

    /** Adds the downloaded genres to the SQLite database using the content provider */
    private void addGenres(ArrayList<Genre> genres) {
        Uri uri = MoviesProvider.buildGenresUri();

        ContentValues[] valuesArray = new ContentValues[genres.size()];

        for (int genreIndex = 0; genreIndex < genres.size(); genreIndex++) {
            Genre genre = genres.get(genreIndex);

            ContentValues values = new ContentValues();
            values.put(GenreEntry._ID, genre.Id);
            values.put(GenreEntry.COLUMN_NAME_NAME, genre.Name);
            valuesArray[genreIndex] = values;
        }

        mContentResolver.bulkInsert(uri, valuesArray);
    }

    /** Adds the downloaded movies to the SQLite database using the content provider */
    private void addMovies(ArrayList<Movie> movies, HashMap<Integer, Boolean> moviesFavoriteStates) {
        ArrayList<ContentValues> movieValuesArray = new ArrayList<>();
        ArrayList<ContentValues> genreValuesArray = new ArrayList<>();
        ArrayList<ContentValues> movieVideosValuesArray = new ArrayList<>();
        ArrayList<ContentValues> movieReviewsValuesArray = new ArrayList<>();

        for (Movie movie : movies)
        {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry._ID, movie.Id);
            movieValues.put(MovieEntry.COLUMN_NAME_TITLE, movie.Title);
            movieValues.put(MovieEntry.COLUMN_NAME_OVERVIEW, movie.Overview);
            movieValues.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.ReleaseDate);
            movieValues.put(MovieEntry.COLUMN_NAME_POPULARITY, movie.Popularity);
            movieValues.put(MovieEntry.COLUMN_NAME_RATING, movie.Rating);
            movieValues.put(MovieEntry.COLUMN_NAME_BACKDROP_PATH, movie.BackdropPath);
            movieValues.put(MovieEntry.COLUMN_NAME_COVER_PATH, movie.CoverPath);
            movieValues.put(MovieEntry.COLUMN_NAME_IS_FAVORITE,
                    moviesFavoriteStates.containsKey(movie.Id) ?
                            moviesFavoriteStates.get(movie.Id) :
                            false);
            movieValuesArray.add(movieValues);

            for (int genreId : movie.GenreIds)
            {
                ContentValues genreValues = new ContentValues();
                genreValues.put(MovieGenreEntry.COLUMN_NAME_MOVIE_ID, movie.Id);
                genreValues.put(MovieGenreEntry.COLUMN_NAME_GENRE_ID, genreId);
                genreValuesArray.add(genreValues);
            }

            for (MovieVideo movieVideo : movie.Videos) {
                ContentValues movieVideoValues = new ContentValues();
                movieVideoValues.put(MovieVideoEntry.COLUMN_NAME_MOVIE_ID, movieVideo.MovieId);
                movieVideoValues.put(MovieVideoEntry.COLUMN_NAME_KEY, movieVideo.Key);
                movieVideoValues.put(MovieVideoEntry.COLUMN_NAME_NAME, movieVideo.Name);
                movieVideoValues.put(MovieVideoEntry.COLUMN_NAME_UNIQUE_ID, movieVideo.UniqueId);
                movieVideosValuesArray.add(movieVideoValues);
            }

            for (MovieReview movieReview : movie.Reviews) {
                ContentValues movieReviewValues = new ContentValues();
                movieReviewValues.put(MovieReviewEntry.COLUMN_NAME_MOVIE_ID, movieReview.MovieId);
                movieReviewValues.put(MovieReviewEntry.COLUMN_NAME_AUTHOR, movieReview.Author);
                movieReviewValues.put(MovieReviewEntry.COLUMN_NAME_CONTENT, movieReview.Content);
                movieReviewValues.put(MovieReviewEntry.COLUMN_NAME_URL, movieReview.Url);
                movieReviewValues.put(MovieReviewEntry.COLUMN_NAME_UNIQUE_ID, movieReview.UniqueId);
                movieReviewsValuesArray.add(movieReviewValues);
            }
        }

        mContentResolver.bulkInsert(MoviesProvider.buildMoviesUri(),
                movieValuesArray.toArray(new ContentValues[movieValuesArray.size()]));
        mContentResolver.bulkInsert(MoviesProvider.buildMoviesGenresUri(),
                genreValuesArray.toArray(new ContentValues[genreValuesArray.size()]));
        mContentResolver.bulkInsert(MoviesProvider.buildMoviesVideosUri(),
                movieVideosValuesArray.toArray(new ContentValues[movieVideosValuesArray.size()]));
        mContentResolver.bulkInsert(MoviesProvider.buildMoviesReviewsUri(),
                movieReviewsValuesArray.toArray(new ContentValues[movieReviewsValuesArray.size()]));
    }

    private HashMap<Integer, Boolean> getMoviesFavoriteStates() {
        HashMap<Integer, Boolean> moviesFavoriteStates = new HashMap<>();

        Cursor cursor = mContentResolver.query(
                MoviesProvider.buildMoviesUri(),
                new String[] { MovieEntry._ID, MovieEntry.COLUMN_NAME_IS_FAVORITE },
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            moviesFavoriteStates.put(MovieData.getId(cursor), MovieData.getIsFavorite(cursor));
        }

        return moviesFavoriteStates;
    }
}