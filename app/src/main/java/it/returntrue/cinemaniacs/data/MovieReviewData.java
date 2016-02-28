/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.data;

import android.database.Cursor;

import it.returntrue.cinemaniacs.data.MoviesContract.MovieReviewEntry;
import it.returntrue.cinemaniacs.utilities.Utilities;

/**
 * Contains all the methods to manage MovieReview data
 */
public final class MovieReviewData {
	public static String getContent(Cursor cursor) {
		return Utilities.getString(cursor, MovieReviewEntry.COLUMN_NAME_CONTENT);
	}

	public static String getAuthor(Cursor cursor) {
		return Utilities.getString(cursor, MovieReviewEntry.COLUMN_NAME_AUTHOR);
	}
}