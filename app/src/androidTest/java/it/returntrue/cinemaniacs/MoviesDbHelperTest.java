/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import it.returntrue.cinemaniacs.data.MoviesContract.GenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieGenreEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieReviewEntry;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieVideoEntry;
import it.returntrue.cinemaniacs.data.MoviesDbHelper;

public class MoviesDbHelperTest extends AndroidTestCase {
    private static final String TAG = MoviesDbHelperTest.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteDatabase();
    }

    public void testCreateDatabase() throws Throwable {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        //region Database
        // Check if database is available
        assertEquals("Database is not available.", true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        // Check if tables are available
        assertTrue("No tables are available.", cursor.moveToFirst());

        HashSet<String> tableNameHashSet = getTableNameHashSet();
        do {
            tableNameHashSet.remove(cursor.getString(cursor.getColumnIndex("name")));
        } while(cursor.moveToNext());

        // Check if all tables are available
        assertTrue("Some tables are not available.", tableNameHashSet.isEmpty());
        //endregion

        //region Genre table
        cursor = db.rawQuery("PRAGMA table_info(" + GenreEntry.TABLE_NAME + ")", null);

        // Check if Genre table information is available
        assertTrue("Could not query the database for Genre table information.", cursor.moveToFirst());

        HashSet<String> genreColumnHashSet = getGenreColumnHashSet();
        do {
            genreColumnHashSet.remove(cursor.getString(cursor.getColumnIndex("name")));
        } while(cursor.moveToNext());

        // Check if all Genre columns are available
        assertTrue("Database doesn't contain all of the required Genre columns.", genreColumnHashSet.isEmpty());
        //endregion

        //region Movie table
        cursor = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")", null);

        // Check if Movie table information is available
        assertTrue("Could not query the database for Movie table information.", cursor.moveToFirst());

        HashSet<String> movieColumnHashSet = getMovieColumnHashSet();
        do {
            movieColumnHashSet.remove(cursor.getString(cursor.getColumnIndex("name")));
        } while(cursor.moveToNext());

        // Check if all Movie columns are available
        assertTrue("Database doesn't contain all of the required Movie columns.", movieColumnHashSet.isEmpty());
        //endregion

        //region MovieGenre table
        cursor = db.rawQuery("PRAGMA table_info(" + MovieGenreEntry.TABLE_NAME + ")", null);

        // Check if MovieGenre table information is available
        assertTrue("Could not query the database for MovieGenre table information.", cursor.moveToFirst());

        HashSet<String> movieGenreColumnHashSet = getMovieGenreColumnHashSet();
        do {
            movieGenreColumnHashSet.remove(cursor.getString(cursor.getColumnIndex("name")));
        } while(cursor.moveToNext());

        // Check if all MovieGenre columns are available
        assertTrue("Database doesn't contain all of the required MovieGenre columns.", movieGenreColumnHashSet.isEmpty());
        //endregion

        //region MovieReview table
        cursor = db.rawQuery("PRAGMA table_info(" + MovieReviewEntry.TABLE_NAME + ")", null);

        // Check if MovieReview table information is available
        assertTrue("Could not query the database for MovieReview table information.", cursor.moveToFirst());

        HashSet<String> movieReviewColumnHashSet = getMovieReviewColumnHashSet();
        do {
            movieReviewColumnHashSet.remove(cursor.getString(cursor.getColumnIndex("name")));
        } while(cursor.moveToNext());

        // Check if all MovieReview columns are available
        assertTrue("Database doesn't contain all of the required MovieReview columns.", movieReviewColumnHashSet.isEmpty());
        //endregion

        //region MovieVideo table
        cursor = db.rawQuery("PRAGMA table_info(" + MovieVideoEntry.TABLE_NAME + ")", null);

        // Check if MovieVideo table information is available
        assertTrue("Could not query the database for MovieVideo table information.", cursor.moveToFirst());

        HashSet<String> movieVideoColumnHashSet = getMovieVideoColumnHashSet();
        do {
            movieVideoColumnHashSet.remove(cursor.getString(cursor.getColumnIndex("name")));
        } while(cursor.moveToNext());

        // Check if all MovieVideo columns are available
        assertTrue("Database doesn't contain all of the required MovieVideo columns.", movieVideoColumnHashSet.isEmpty());
        //endregion
    }

    public void testInsertGenreTable() {
        insertGenre();
    }

    public void testInsertMovieTable() {
        insertMovie();
    }

    public void testInsertMovieGenreTable() {
        insertMovieGenre();
    }

    public void testInsertMovieReviewTable() {
        insertMovieReview();
    }

    public void testInsertMovieVideoTable() {
        insertMovieVideo();
    }

    public long insertGenre() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GenreEntry._ID, 28);
        contentValues.put(GenreEntry.COLUMN_NAME_NAME, "Action");

        long id = db.insert(GenreEntry.TABLE_NAME, null, contentValues);

        // Check if Genre can be inserted
        assertTrue("Could not insert Genre.", id != -1);

        String whereClause = GenreEntry._ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        Cursor cursor = db.query(GenreEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        // Check if Genre values are correct
        validateCursor("Genre", cursor, contentValues);

        cursor.close();
        db.close();

        return id;
    }

    public long insertMovie() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry._ID, 135397);
        contentValues.put(MovieEntry.COLUMN_NAME_TITLE, "Jurassic World");
        contentValues.put(MovieEntry.COLUMN_NAME_OVERVIEW, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        contentValues.put(MovieEntry.COLUMN_NAME_RELEASE_DATE, 2457185.5);
        contentValues.put(MovieEntry.COLUMN_NAME_POPULARITY, 88.551849);
        contentValues.put(MovieEntry.COLUMN_NAME_RATING, 7.1);
        contentValues.put(MovieEntry.COLUMN_NAME_BACKDROP_PATH, "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
        contentValues.put(MovieEntry.COLUMN_NAME_COVER_PATH, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
        contentValues.put(MovieEntry.COLUMN_NAME_IS_FAVORITE, false);

        long id = db.insert(MovieEntry.TABLE_NAME, null, contentValues);

        // Check if Movie can be inserted
        assertTrue("Could not insert Movie.", id != -1);

        String whereClause = MovieEntry._ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        Cursor cursor = db.query(MovieEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        // Check if Movie values are correct
        validateCursor("Movie", cursor, contentValues);

        cursor.close();
        db.close();

        return id;
    }

    public long insertMovieGenre() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        long genreId = insertGenre();
        long movieId = insertMovie();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieGenreEntry.COLUMN_NAME_MOVIE_ID, movieId);
        contentValues.put(MovieGenreEntry.COLUMN_NAME_GENRE_ID, genreId);

        long id = db.insert(MovieGenreEntry.TABLE_NAME, null, contentValues);

        // Check if MovieGenre can be inserted
        assertTrue("Could not insert MovieGenre.", id != -1);

        String whereClause = MovieGenreEntry._ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        Cursor cursor = db.query(MovieGenreEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        // Check if MovieGenre values are correct
        validateCursor("MovieGenre", cursor, contentValues);

        cursor.close();
        db.close();

        return id;
    }

    public long insertMovieReview() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        long movieId = insertMovie();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieReviewEntry.COLUMN_NAME_MOVIE_ID, movieId);
        contentValues.put(MovieReviewEntry.COLUMN_NAME_AUTHOR, "Travis Bell");
        contentValues.put(MovieReviewEntry.COLUMN_NAME_CONTENT, "I felt like this was a tremendous end to Nolan's Batman trilogy. The Dark Knight Rises may very well have been the weakest of all 3 films but when you're talking about a scale of this magnitude, it still makes this one of the best movies I've seen in the past few years.\r\n\r\nI expected a little more _Batman_ than we got (especially with a runtime of 2:45) but while the story around the fall of Bruce Wayne and Gotham City was good I didn't find it amazing. This might be in fact, one of my only criticisms—it was a long movie but still, maybe too short for the story I felt was really being told. I feel confident in saying this big of a story could have been split into two movies.\r\n\r\nThe acting, editing, pacing, soundtrack and overall theme were the same 'as-close-to-perfect' as ever with any of Christopher Nolan's other films. Man does this guy know how to make a movie!\r\n\r\nYou don't have to be a Batman fan to enjoy these movies and I hope any of you who feel this way re-consider. These 3 movies are without a doubt in my mind, the finest display of comic mythology ever told on the big screen. They are damn near perfect.");
        contentValues.put(MovieReviewEntry.COLUMN_NAME_URL, "http://j.mp/QSjAK2");
        contentValues.put(MovieReviewEntry.COLUMN_NAME_UNIQUE_ID, "5010553819c2952d1b000451");

        long id = db.insert(MovieReviewEntry.TABLE_NAME, null, contentValues);

        // Check if MovieReview can be inserted
        assertTrue("Could not insert MovieReview.", id != -1);

        String whereClause = MovieReviewEntry._ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        Cursor cursor = db.query(MovieReviewEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        // Check if MovieReview values are correct
        validateCursor("MovieReview", cursor, contentValues);

        cursor.close();
        db.close();

        return id;
    }

    public long insertMovieVideo() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();

        long movieId = insertMovie();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieVideoEntry.COLUMN_NAME_MOVIE_ID, movieId);
        contentValues.put(MovieVideoEntry.COLUMN_NAME_KEY, "SUXWAEX2jlg");
        contentValues.put(MovieVideoEntry.COLUMN_NAME_NAME, "Trailer 1");
        contentValues.put(MovieVideoEntry.COLUMN_NAME_UNIQUE_ID, "533ec654c3a36854480003eb");

        long id = db.insert(MovieVideoEntry.TABLE_NAME, null, contentValues);

        // Check if MovieVideo can be inserted
        assertTrue("Could not insert MovieVideo.", id != -1);

        String whereClause = MovieVideoEntry._ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        Cursor cursor = db.query(MovieVideoEntry.TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        // Check if MovieVideo values are correct
        validateCursor("MovieVideo", cursor, contentValues);

        cursor.close();
        db.close();

        return id;
    }

    private void deleteDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    private void validateCursor(String error, Cursor cursor, ContentValues contentValues) {
        // Check if cursor returned contains data
        assertTrue("Empty cursor returned for table " + error + ".", cursor.moveToFirst());

        for (String key : contentValues.keySet()) {
            // Check if cursor value is correct
            assertTrue("Column " + key + " for table " + error + " value is not the same as ContentValues.",
                    cursor.getString(cursor.getColumnIndex(key)) != contentValues.getAsString(key));
        }
    }

    private HashSet<String> getTableNameHashSet() {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(GenreEntry.TABLE_NAME);
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieGenreEntry.TABLE_NAME);
        tableNameHashSet.add(MovieReviewEntry.TABLE_NAME);
        tableNameHashSet.add(MovieVideoEntry.TABLE_NAME);
        return tableNameHashSet;
    }

    private HashSet<String> getGenreColumnHashSet() {
        final HashSet<String> genreColumnHashSet = new HashSet<String>();
        genreColumnHashSet.add(GenreEntry._ID);
        genreColumnHashSet.add(GenreEntry.COLUMN_NAME_NAME);
        return genreColumnHashSet;
    }

    private HashSet<String> getMovieColumnHashSet() {
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_RELEASE_DATE);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_POPULARITY);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_RATING);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_BACKDROP_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_COVER_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_NAME_IS_FAVORITE);
        return movieColumnHashSet;
    }

    private HashSet<String> getMovieGenreColumnHashSet() {
        final HashSet<String> movieGenreColumnHashSet = new HashSet<String>();
        movieGenreColumnHashSet.add(MovieGenreEntry._ID);
        movieGenreColumnHashSet.add(MovieGenreEntry.COLUMN_NAME_MOVIE_ID);
        movieGenreColumnHashSet.add(MovieGenreEntry.COLUMN_NAME_GENRE_ID);
        return movieGenreColumnHashSet;
    }

    private HashSet<String> getMovieReviewColumnHashSet() {
        final HashSet<String> movieReviewColumnHashSet = new HashSet<String>();
        movieReviewColumnHashSet.add(MovieReviewEntry._ID);
        movieReviewColumnHashSet.add(MovieReviewEntry.COLUMN_NAME_MOVIE_ID);
        movieReviewColumnHashSet.add(MovieReviewEntry.COLUMN_NAME_AUTHOR);
        movieReviewColumnHashSet.add(MovieReviewEntry.COLUMN_NAME_CONTENT);
        movieReviewColumnHashSet.add(MovieReviewEntry.COLUMN_NAME_URL);
        movieReviewColumnHashSet.add(MovieReviewEntry.COLUMN_NAME_UNIQUE_ID);
        return movieReviewColumnHashSet;
    }

    private HashSet<String> getMovieVideoColumnHashSet() {
        final HashSet<String> movieVideoColumnHashSet = new HashSet<String>();
        movieVideoColumnHashSet.add(MovieVideoEntry._ID);
        movieVideoColumnHashSet.add(MovieVideoEntry.COLUMN_NAME_MOVIE_ID);
        movieVideoColumnHashSet.add(MovieVideoEntry.COLUMN_NAME_KEY);
        movieVideoColumnHashSet.add(MovieVideoEntry.COLUMN_NAME_NAME);
        movieVideoColumnHashSet.add(MovieVideoEntry.COLUMN_NAME_UNIQUE_ID);
        return movieVideoColumnHashSet;
    }
}