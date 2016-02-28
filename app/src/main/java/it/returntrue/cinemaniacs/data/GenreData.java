/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.data;

import android.database.Cursor;

import it.returntrue.cinemaniacs.data.MoviesContract.GenreEntry;
import it.returntrue.cinemaniacs.utilities.Utilities;

/**
 * Contains all the methods to manage MovieGenre data
 */
public final class GenreData {
	public static String getName(Cursor cursor) {
		return Utilities.getString(cursor, GenreEntry.COLUMN_NAME_NAME);
	}
}