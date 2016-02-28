/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import it.returntrue.cinemaniacs.data.MoviesContract.MovieEntry;
import it.returntrue.cinemaniacs.provider.MoviesProvider;
import it.returntrue.cinemaniacs.utilities.Utilities;

/**
 * Contains all the methods to manage Movie data
 */
public final class MovieData {
	public static int getId(Cursor cursor) {
		return Utilities.getInt(cursor, MovieEntry._ID);
	}
	
	public static String getTitle(Cursor cursor) {
		return Utilities.getString(cursor, MovieEntry.COLUMN_NAME_TITLE);
	}

	public static String getOverview(Cursor cursor) {
		return Utilities.getString(cursor, MovieEntry.COLUMN_NAME_OVERVIEW);
	}
	
	public static String getReleaseDate(Cursor cursor) {
		return Utilities.getString(cursor, MovieEntry.COLUMN_NAME_RELEASE_DATE);
	}
	
	public static String getCoverPath(Cursor cursor) {
		return Utilities.getString(cursor, MovieEntry.COLUMN_NAME_COVER_PATH);
	}

	public static String getBackdropPath(Cursor cursor) {
		return Utilities.getString(cursor, MovieEntry.COLUMN_NAME_BACKDROP_PATH);
	}

	public static float getRating(Cursor cursor) {
		return Utilities.getFloat(cursor, MovieEntry.COLUMN_NAME_RATING);
	}

	public static boolean getIsFavorite(Cursor cursor) {
		return Utilities.getBoolean(cursor, MovieEntry.COLUMN_NAME_IS_FAVORITE);
	}
	
	public static CursorLoader loadPopularMovies(Context context) {
		return loadMovies(context, null, null, MovieEntry.COLUMN_NAME_POPULARITY + " DESC");
	}
	
	public static CursorLoader loadTopRatedMovies(Context context) {
		return loadMovies(context, null, null, MovieEntry.COLUMN_NAME_RATING + " DESC");
	}
	
	public static CursorLoader loadFavoriteMovies(Context context) {
		return loadMovies(context, MovieEntry.COLUMN_NAME_IS_FAVORITE + " = ?",
				new String[]{"1"}, MovieEntry.COLUMN_NAME_RATING + " DESC");
	}

	public static final Cursor getMovie(Context context, long id) {
		return context.getContentResolver().query(
				MoviesProvider.buildMovieUri(id),
				null,
				null,
				null,
				null);
	}

    public static final int setMovieAsFavorite(Context context, long id, boolean isFavorite) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_NAME_IS_FAVORITE, isFavorite);

        return context.getContentResolver().update(
				MoviesProvider.buildMoviesUri(),
				values,
				MovieEntry._ID + " = ?",
				new String[]{String.valueOf(id)});
    }
	
	private static final CursorLoader loadMovies(Context context, String selection, 
		String[] selectionArgs, String sortOrder) {
		
        return new CursorLoader(context,
                MoviesProvider.buildMoviesUri(),
                null,
                selection,
                selectionArgs,
                sortOrder);
	}
}