/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import it.returntrue.cinemaniacs.data.MoviesContract.GenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieGenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieReviewEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieVideoEntry;

/**
 * Provides an implementation to manage the application's SQLite database
 */
public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    private static final String SQL_CREATE_GENRE =
            "CREATE TABLE " + GenreEntry.TABLE_NAME + " (" +
            GenreEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
            GenreEntry.COLUMN_NAME_NAME + " TEXT NOT NULL" +
            ")";
    private static final String SQL_CREATE_MOVIE =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
            MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
            MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL," +
            MovieEntry.COLUMN_NAME_RELEASE_DATE + " REAL NOT NULL," +
            MovieEntry.COLUMN_NAME_POPULARITY + " REAL NOT NULL," +
            MovieEntry.COLUMN_NAME_RATING + " REAL NOT NULL," +
            MovieEntry.COLUMN_NAME_BACKDROP_PATH + " TEXT NOT NULL," +
            MovieEntry.COLUMN_NAME_COVER_PATH + " TEXT NOT NULL," +
            MovieEntry.COLUMN_NAME_IS_FAVORITE + " NUMERIC NOT NULL DEFAULT 0" +
            ")";
    private static final String SQL_CREATE_MOVIE_GENRE =
            "CREATE TABLE " + MovieGenreEntry.TABLE_NAME + " (" +
            MovieGenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieGenreEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            MovieGenreEntry.COLUMN_NAME_GENRE_ID + " INTEGER NOT NULL," +
            "UNIQUE (" + MovieGenreEntry.COLUMN_NAME_MOVIE_ID + "," + MovieGenreEntry.COLUMN_NAME_GENRE_ID + ") ON CONFLICT REPLACE" +
            ")";
    private static final String SQL_CREATE_MOVIE_REVIEW =
            "CREATE TABLE " + MovieReviewEntry.TABLE_NAME + " (" +
            MovieReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieReviewEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            MovieReviewEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL," +
            MovieReviewEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL," +
            MovieReviewEntry.COLUMN_NAME_URL + " TEXT NOT NULL," +
            MovieReviewEntry.COLUMN_NAME_UNIQUE_ID + " TEXT NOT NULL," +
            "UNIQUE (" + MovieReviewEntry.COLUMN_NAME_UNIQUE_ID + ") ON CONFLICT REPLACE" +
            ")";
    private static final String SQL_CREATE_MOVIE_VIDEO =
            "CREATE TABLE " + MovieVideoEntry.TABLE_NAME + " (" +
            MovieVideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieVideoEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            MovieVideoEntry.COLUMN_NAME_KEY + " TEXT NOT NULL," +
            MovieVideoEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
            MovieVideoEntry.COLUMN_NAME_UNIQUE_ID + " TEXT NOT NULL," +
            "UNIQUE (" + MovieVideoEntry.COLUMN_NAME_UNIQUE_ID + ") ON CONFLICT REPLACE" +
            ")";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GENRE);
        db.execSQL(SQL_CREATE_MOVIE);
        db.execSQL(SQL_CREATE_MOVIE_GENRE);
        db.execSQL(SQL_CREATE_MOVIE_REVIEW);
        db.execSQL(SQL_CREATE_MOVIE_VIDEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}