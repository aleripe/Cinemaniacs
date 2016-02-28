/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.data;

import android.database.Cursor;

import it.returntrue.cinemaniacs.data.MoviesContract.MovieVideoEntry;
import it.returntrue.cinemaniacs.utilities.Utilities;

/**
 * Contains all the methods to manage MovieVideo data
 */
public final class MovieVideoData {
	public static String getName(Cursor cursor) {
		return Utilities.getString(cursor, MovieVideoEntry.COLUMN_NAME_NAME);
	}

	public static String getKey(Cursor cursor) {
		return Utilities.getString(cursor, MovieVideoEntry.COLUMN_NAME_KEY);
	}
}