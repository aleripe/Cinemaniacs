/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import it.returntrue.cinemaniacs.data.MoviesContract.GenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieGenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieVideoEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieReviewEntry;
import it.returntrue.cinemaniacs.data.MoviesDbHelper;

/**
 * Provides an implementation of the movies content provider
 */
public class MoviesProvider extends ContentProvider {
    public static final String CONTENT_AUTHORITY = "it.returntrue.cinemaniacs.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_GENRE = "genre";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_GENRE = "movie_genre";
    public static final String PATH_MOVIE_VIDEO = "movie_video";
    public static final String PATH_MOVIE_REVIEW = "movie_review";
    public static final String GENRE_DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "vnd." + CONTENT_AUTHORITY + "." + PATH_GENRE;
    public static final String MOVIE_DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE;
    public static final String MOVIE_ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE;
    public static final String MOVIE_GENRE_DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE_GENRE;
    public static final String MOVIE_VIDEO_DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE_VIDEO;
    public static final String MOVIE_REVIEW_DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "vnd." + CONTENT_AUTHORITY + "." + PATH_MOVIE_REVIEW;

    private static final int GENRE = 100;
    private static final int MOVIE = 200;
    private static final int MOVIE_ID = 201;
    private static final int MOVIE_GENRE = 300;
    private static final int MOVIE_VIDEO = 400;
    private static final int MOVIE_REVIEW = 500;
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private MoviesDbHelper mMoviesDbHelper;

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case GENRE:
                return GENRE_DIR_CONTENT_TYPE;
            case MOVIE:
                return MOVIE_DIR_CONTENT_TYPE;
            case MOVIE_ID:
                return MOVIE_ITEM_CONTENT_TYPE;
            case MOVIE_GENRE:
                return MOVIE_GENRE_DIR_CONTENT_TYPE;
            case MOVIE_VIDEO:
                return MOVIE_VIDEO_DIR_CONTENT_TYPE;
            case MOVIE_REVIEW:
                return MOVIE_REVIEW_DIR_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (mUriMatcher.match(uri)) {
            case GENRE:
                cursor = queryGenres();
                break;
            case MOVIE:
                cursor = queryMovies(selection, selectionArgs, sortOrder);
                break;
            case MOVIE_ID:
                cursor = queryMovie(Integer.valueOf(uri.getLastPathSegment()));
                break;
            case MOVIE_VIDEO:
                cursor = queryMovieVideos(selection, selectionArgs);
                break;
            case MOVIE_REVIEW:
                cursor = queryMovieReviews(selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private Cursor queryGenres() {
        return mMoviesDbHelper.getReadableDatabase().query(GenreEntry.TABLE_NAME,
                null, null, null, null, null, null);
    }

    private Cursor queryMovies(String selection, String[] selectionArgs, String sortOrder) {
        return mMoviesDbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                null, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor queryMovie(int id) {
        Cursor[] cursors = new Cursor[2];

        cursors[0] = mMoviesDbHelper.getReadableDatabase().query(
                MovieEntry.TABLE_NAME,
                null,
                MovieEntry._ID + " = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null);

        cursors[1] = mMoviesDbHelper.getReadableDatabase().query(
                GenreEntry.TABLE_NAME + " INNER JOIN " + MovieGenreEntry.TABLE_NAME + " ON " + GenreEntry.TABLE_NAME + "." + GenreEntry._ID + " = " + MovieGenreEntry.TABLE_NAME + "." + MovieGenreEntry.COLUMN_NAME_GENRE_ID,
                new String[] { GenreEntry.TABLE_NAME + "." + GenreEntry.COLUMN_NAME_NAME },
                MovieGenreEntry.COLUMN_NAME_MOVIE_ID + " = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null);

        return new MergeCursor(cursors);
    }

    private Cursor queryMovieVideos(String selection, String[] selectionArgs) {
        return mMoviesDbHelper.getReadableDatabase().query(
                MovieVideoEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor queryMovieReviews(String selection, String[] selectionArgs) {
        return mMoviesDbHelper.getReadableDatabase().query(
                MovieReviewEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = buildMovieUri(id);
                }
                else {
                    throw new SQLException("Failed to insert row into Uri: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valuesArray) {
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int totalRowCount = 0;

        String tableName;

        switch (mUriMatcher.match(uri)) {
            case GENRE:
                tableName = GenreEntry.TABLE_NAME;
                break;
            case MOVIE:
                tableName = MovieEntry.TABLE_NAME;
                break;
            case MOVIE_GENRE:
                tableName = MovieGenreEntry.TABLE_NAME;
                break;
            case MOVIE_VIDEO:
                tableName = MovieVideoEntry.TABLE_NAME;
                break;
            case MOVIE_REVIEW:
                tableName = MovieReviewEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        db.beginTransaction();

        try {
            for (ContentValues values : valuesArray) {
                long id = db.insert(tableName, null, values);

                if (id != -1) {
                    totalRowCount += 1;
                }
            }

            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        if (totalRowCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return totalRowCount;
    }

    public static Uri buildGenresUri() {
        return CONTENT_URI.buildUpon()
                .appendPath(PATH_GENRE)
                .build();
    }

    public static Uri buildMoviesUri() {
        return CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
    }

    public static Uri buildMovieUri(long id) {
        return CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(id))
                .build();
    }

    public static Uri buildMoviesGenresUri() {
        return CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_GENRE)
                .build();
    }

    public static Uri buildMoviesVideosUri() {
        return CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_VIDEO)
                .build();
    }

    public static Uri buildMoviesReviewsUri() {
        return CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_REVIEW)
                .build();
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_GENRE, GENRE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE, MOVIE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE + "/#", MOVIE_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_GENRE, MOVIE_GENRE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_VIDEO, MOVIE_VIDEO);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_REVIEW, MOVIE_REVIEW);
        return uriMatcher;
    }
}