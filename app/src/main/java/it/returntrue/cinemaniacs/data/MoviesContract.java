/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.data;

import android.provider.BaseColumns;

/**
 * Contains all the contracts for the SQLite database tables
 */
public final class MoviesContract {
    public static abstract class GenreEntry implements BaseColumns {
        public static final String TABLE_NAME = "genre";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_NAME_COVER_PATH = "cover_path";
        public static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";
    }

    public static abstract class MovieGenreEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_genre";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_GENRE_ID = "genre_id";
    }

    public static abstract class MovieReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_review";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_UNIQUE_ID = "unique_id";
    }

    public static abstract class MovieVideoEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_video";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_UNIQUE_ID = "unique_id";
    }
}