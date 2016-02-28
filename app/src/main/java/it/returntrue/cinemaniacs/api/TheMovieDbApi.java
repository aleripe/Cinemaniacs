/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.api;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Wraps calls to The Movie Database API
 */
public class TheMovieDbApi {
    private static final String TAG = TheMovieDbApi.class.getSimpleName();

    private final String API_KEY_PARAMETER = "api_key";
    private final String GENRES_TOKEN = "genres";
    private final String RESULTS_TOKEN = "results";
    private final String ID_TOKEN = "id";
    private final String NAME_TOKEN = "name";
    private final String TITLE_TOKEN = "title";
    private final String OVERVIEW_TOKEN = "overview";
    private final String RELEASE_DATE_TOKEN = "release_date";
    private final String POPULARITY_TOKEN = "popularity";
    private final String VOTE_AVERAGE_TOKEN = "vote_average";
    private final String BACKDROP_PATH_TOKEN = "backdrop_path";
    private final String POSTER_PATH_TOKEN = "poster_path";
    private final String GENRE_IDS_TOKEN = "genre_ids";
    private final String KEY_TOKEN = "key";
    private final String AUTHOR_TOKEN = "author";
    private final String CONTENT_TOKEN = "content";
    private final String URL_TOKEN = "url";
    private final String BASE_URL = "http://api.themoviedb.org/3";
    private final String GENRES_URL = "/genre/movie/list";
    private final String POPULAR_MOVIES_URL = "/movie/popular";
    private final String TOP_RATED_MOVIES_URL = "/movie/top_rated";
    private final String MOVIE_VIDEOS_URL = "/movie/{id}/videos";
    private final String MOVIE_REVIEWS_URL = "/movie/{id}/reviews";
    private final String mApiKey;

    public TheMovieDbApi(String apiKey) {
        mApiKey = apiKey;
    }

    /** Returns the list of available genres */
    public ArrayList<Genre> getGenres() {
        Uri uri = Uri.parse(BASE_URL + GENRES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, mApiKey)
                .build();

        ArrayList<Genre> genres = new ArrayList<>();
        String json = getJson(uri);

        if (json != null) {
            try {
                JSONObject rootObject = new JSONObject(json);
                JSONArray genresArray = rootObject.getJSONArray(GENRES_TOKEN);

                for (int genreIndex = 0; genreIndex < genresArray.length(); genreIndex++) {
                    JSONObject genreObject = genresArray.getJSONObject(genreIndex);

                    Genre genre = new Genre();
                    genre.Id = genreObject.getInt(ID_TOKEN);
                    genre.Name = genreObject.getString(NAME_TOKEN);
                    genres.add(genre);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "The Movie Database API JSON is malformed for URL: " + uri.toString());
            }
        }

        return genres;
    }

    /** Returns the list of popular movies */
    public ArrayList<Movie> getPopularMovies() {
        Uri uri = Uri.parse(BASE_URL + POPULAR_MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, mApiKey)
                .build();

        return getMovies(uri);
    }

    /** Returns the list of top rated movies */
    public ArrayList<Movie> getTopRatedMovies() {
        Uri uri = Uri.parse(BASE_URL + TOP_RATED_MOVIES_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, mApiKey)
                .build();

        return getMovies(uri);
    }

    /** Returns the list of movies from the Uri */
    private ArrayList<Movie> getMovies(Uri uri) {
        ArrayList<Movie> movies = new ArrayList<>();
        String json = getJson(uri);

        if (json != null) {
            try {
                JSONObject rootObject = new JSONObject(json);
                JSONArray resultsArray = rootObject.getJSONArray(RESULTS_TOKEN);

                for (int movieIndex = 0; movieIndex < resultsArray.length(); movieIndex++) {
                    JSONObject movieObject = resultsArray.getJSONObject(movieIndex);
                    JSONArray genresArray = movieObject.getJSONArray(GENRE_IDS_TOKEN);

                    Movie movie = new Movie();
                    movie.Id = movieObject.getInt(ID_TOKEN);
                    movie.Title = movieObject.getString(TITLE_TOKEN);
                    movie.Overview = movieObject.getString(OVERVIEW_TOKEN);
                    movie.ReleaseDate = movieObject.getString(RELEASE_DATE_TOKEN);
                    movie.Popularity = movieObject.getDouble(POPULARITY_TOKEN);
                    movie.Rating = movieObject.getDouble(VOTE_AVERAGE_TOKEN);
                    movie.BackdropPath = movieObject.getString(BACKDROP_PATH_TOKEN);
                    movie.CoverPath = movieObject.getString(POSTER_PATH_TOKEN);
                    movie.GenreIds = new ArrayList<>();
                    movie.Videos = getMovieVideos(movie.Id);
                    movie.Reviews = getMovieReviews(movie.Id);

                    for (int genreIndex = 0; genreIndex < genresArray.length(); genreIndex++) {
                        movie.GenreIds.add(genresArray.getInt(genreIndex));
                    }

                    movies.add(movie);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "The Movie Database API JSON is malformed for URL: " + uri.toString());
            }
        }

        return movies;
    }

    private ArrayList<MovieVideo> getMovieVideos(int movieId) {
        Uri uri = Uri.parse(BASE_URL + MOVIE_VIDEOS_URL.replace("{id}", String.valueOf(movieId)))
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, mApiKey)
                .build();

        ArrayList<MovieVideo> movieVideos = new ArrayList<>();
        String json = getJson(uri);

        if (json != null) {
            try {
                JSONObject rootObject = new JSONObject(json);
                JSONArray resultsArray = rootObject.getJSONArray(RESULTS_TOKEN);

                for (int movieVideoIndex = 0; movieVideoIndex < resultsArray.length(); movieVideoIndex++) {
                    JSONObject movieVideoObject = resultsArray.getJSONObject(movieVideoIndex);

                    MovieVideo movieVideo = new MovieVideo();
                    movieVideo.MovieId = movieId;
                    movieVideo.Key = movieVideoObject.getString(KEY_TOKEN);
                    movieVideo.Name = movieVideoObject.getString(NAME_TOKEN);
                    movieVideo.UniqueId = movieVideoObject.getString(ID_TOKEN);

                    movieVideos.add(movieVideo);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "The Movie Database API JSON is malformed for URL: " + uri.toString());
            }
        }

        return movieVideos;
    }

    private ArrayList<MovieReview> getMovieReviews(int movieId) {
        Uri uri = Uri.parse(BASE_URL + MOVIE_REVIEWS_URL.replace("{id}", String.valueOf(movieId)))
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAMETER, mApiKey)
                .build();

        ArrayList<MovieReview> movieReviews = new ArrayList<>();
        String json = getJson(uri);

        if (json != null) {
            try {
                JSONObject rootObject = new JSONObject(json);
                JSONArray resultsArray = rootObject.getJSONArray(RESULTS_TOKEN);

                for (int movieReviewIndex = 0; movieReviewIndex < resultsArray.length(); movieReviewIndex++) {
                    JSONObject movieReviewObject = resultsArray.getJSONObject(movieReviewIndex);

                    MovieReview movieReview = new MovieReview();
                    movieReview.MovieId = movieId;
                    movieReview.Author = movieReviewObject.getString(AUTHOR_TOKEN);
                    movieReview.Content = movieReviewObject.getString(CONTENT_TOKEN);
                    movieReview.Url = movieReviewObject.getString(URL_TOKEN);
                    movieReview.UniqueId = movieReviewObject.getString(ID_TOKEN);

                    movieReviews.add(movieReview);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "The Movie Database API JSON is malformed for URL: " + uri.toString());
            }
        }

        return movieReviews;
    }

    /** Connects to the Uri to fetch JSON data */
    private String getJson(Uri uri) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(uri.toString());

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream stream = connection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (stream == null) {
                Log.i(TAG, "No stream available for The Movie Database URL: " + uri.toString());
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            if (builder.length() == 0) {
                Log.i(TAG, "No data available for The Movie Database URL: " + uri.toString());
                return null;
            }

            return builder.toString();
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "The Movie Database API URL is malformed: " + uri.toString());
            return null;
        }
        catch (IOException e) {
            Log.e(TAG, "Could not connect to The Movie Database API");
            return null;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "Could not close The Movie Database API stream");
                }
            }
        }
    }

    /**
     * Represents a genre from The Movie Database API
     */
    public class Genre {
        public int Id;
        public String Name;
    }

    /**
     * Represents a Movie from The Movie Database API
     */
    public class Movie {
        public int Id;
        public String Title;
        public String Overview;
        public String ReleaseDate;
        public double Popularity;
        public double Rating;
        public String BackdropPath;
        public String CoverPath;
        public ArrayList<Integer> GenreIds;
        public ArrayList<MovieVideo> Videos;
        public ArrayList<MovieReview> Reviews;
    }

    /**
     * Represents a Movie Video from The Movie Database API
     */
    public class MovieVideo {
        public int Id;
        public int MovieId;
        public String Key;
        public String Name;
        public String UniqueId;
    }

    /**
     * Represents a Movie Review from The Movie Database API
     */
    public class MovieReview {
        public int Id;
        public int MovieId;
        public String Author;
        public String Content;
        public String Url;
        public String UniqueId;
    }
}