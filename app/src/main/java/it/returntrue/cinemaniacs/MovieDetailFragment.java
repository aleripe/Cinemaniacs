/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

import it.returntrue.cinemaniacs.data.GenreData;
import it.returntrue.cinemaniacs.data.MovieData;
import it.returntrue.cinemaniacs.data.MovieReviewData;
import it.returntrue.cinemaniacs.data.MovieVideoData;
import it.returntrue.cinemaniacs.data.MoviesContract.MovieVideoEntry;
import it.returntrue.cinemaniacs.provider.MoviesProvider;
import it.returntrue.cinemaniacs.utilities.Utilities;

/**
 * Represents the movie detail fragment
 */
public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String BUNDLE_URI = "uri";
    public static final String BUNDLE_TWO_PANE = "two_pane";

    private static final int MOVIE_LOADER = 1;
    private static final int MOVIE_VIDEOS_LOADER = 2;
    private static final int MOVIE_REVIEWS_LOADER = 3;

    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private final String COVER_IMAGE_SIZE = "w185";
    private final String BACKDROP_IMAGE_SIZE = "w780";
    private final String VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";

    private Uri mUri;
    private boolean mTwoPane;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mImageBackdrop;
    private ImageView mImageCover;
    private RatingBar mRatingBar;
    private TextView mTitle;
    private TextView mTextReleaseDate;
    private TextView mTextGenres;
    private TextView mTextOverview;
    private FloatingActionButton mFab;
    private LinearLayout mListVideos;
    private LinearLayout mListReviews;
    private Uri mShareUri;

    public static MovieDetailFragment create(Uri uri, boolean twoPane) {
        // Creates a movie detail fragment with a specific Uri as argument, if available
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        if (uri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(BUNDLE_URI, uri);
            arguments.putBoolean(BUNDLE_TWO_PANE, twoPane);
            movieDetailFragment.setArguments(arguments);
        }
        return movieDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getActivity() != null) {
            // Sets the rating stars with accent color
            DrawableCompat.setTint(mRatingBar.getProgressDrawable(),
                    ContextCompat.getColor(getContext(), R.color.colorAccent));

            if (mToolbar != null) {
                // Set toolbar with title and navigation
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(mToolbar);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // Setup the available loader
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
            getLoaderManager().initLoader(MOVIE_VIDEOS_LOADER, null, this);
            getLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, this);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(BUNDLE_URI);
            mTwoPane = arguments.getBoolean(BUNDLE_TWO_PANE);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mCoordinatorLayout = (CoordinatorLayout)rootView.findViewById(R.id.coordinator);
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)rootView.findViewById(R.id.collapsible_toolbar);
        mImageBackdrop = (ImageView)rootView.findViewById(R.id.image_backdrop);
        mImageCover = (ImageView)rootView.findViewById(R.id.image_cover);
        mRatingBar = (RatingBar)rootView.findViewById(R.id.rating_bar);
        mTitle = (TextView)rootView.findViewById(R.id.text_title);
        mTextReleaseDate = (TextView)rootView.findViewById(R.id.text_release_date);
        mTextGenres = (TextView)rootView.findViewById(R.id.text_genres);
        mTextOverview = (TextView)rootView.findViewById(R.id.text_overview);
        mListVideos = (LinearLayout)rootView.findViewById(R.id.list_videos);
        mListReviews = (LinearLayout)rootView.findViewById(R.id.list_reviews);
        mFab = (FloatingActionButton)rootView.findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = Integer.valueOf(mUri.getLastPathSegment());
                final Cursor cursor = MovieData.getMovie(getContext(), id);

                if (cursor.moveToFirst()) {
                    final boolean isFavorite = MovieData.getIsFavorite(cursor);
                    final int rowsAffected = MovieData.setMovieAsFavorite(getContext(), id, !isFavorite);

                    if (rowsAffected > 0) {
                        final View contentView = getView().getRootView().findViewById(android.R.id.content);
                        final String text = getContext().getString(Utilities.toggleStringResource(!isFavorite,
                                R.string.set_as_favorite, R.string.removed_from_favorites));
                        final int colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);

                        mFab.setImageResource(Utilities.toggleImageResource(!isFavorite,
                                R.drawable.ic_favorite_full, R.drawable.ic_favorite_empty));

                        Snackbar.make(contentView, text, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MovieData.setMovieAsFavorite(getContext(), id, isFavorite);

                                        mFab.setImageResource(Utilities.toggleImageResource(isFavorite,
                                                R.drawable.ic_favorite_full, R.drawable.ic_favorite_empty));
                                    }
                                })
                                .setActionTextColor(colorAccent)
                                .show();
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().supportFinishAfterTransition();
                return true;
            case R.id.action_share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareUri.toString());
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            switch (id) {
                case MOVIE_LOADER: {
                    return new CursorLoader(
                            getContext(),
                            mUri,
                            null,
                            null,
                            null,
                            null
                    );
                }

                case MOVIE_VIDEOS_LOADER: {
                    return new CursorLoader(
                            getContext(),
                            MoviesProvider.buildMoviesVideosUri(),
                            null,
                            MovieVideoEntry.COLUMN_NAME_MOVIE_ID + " = ?",
                            new String[] { mUri.getLastPathSegment() },
                            null
                    );
                }

                case MOVIE_REVIEWS_LOADER: {
                    return new CursorLoader(
                            getContext(),
                            MoviesProvider.buildMoviesReviewsUri(),
                            null,
                            MovieVideoEntry.COLUMN_NAME_MOVIE_ID + " = ?",
                            new String[] { mUri.getLastPathSegment() },
                            null
                    );
                }
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case MOVIE_LOADER:
                loadMovie(cursor);
                break;
            case MOVIE_VIDEOS_LOADER:
                loadMovieVideos(cursor);
                break;
            case MOVIE_REVIEWS_LOADER:
                loadMovieReviews(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadMovie(Cursor cursor)  {
        // If Movie cursor is not available returns
        if (cursor.getCount() == 0) {
            return;
        }

        cursor.moveToFirst();

        String backdropUrl = IMAGE_BASE_URL + BACKDROP_IMAGE_SIZE + MovieData.getBackdropPath(cursor);
        String coverUrl = IMAGE_BASE_URL + COVER_IMAGE_SIZE + MovieData.getCoverPath(cursor);

        float rating = MovieData.getRating(cursor);
        String title = MovieData.getTitle(cursor);
        String releaseYear = Utilities.formatYear(MovieData.getReleaseDate(cursor));
        String overview = MovieData.getOverview(cursor);
        boolean isFavorite = MovieData.getIsFavorite(cursor);
        ArrayList<String> genres = new ArrayList<>();

        // Advances to the Genre cursor
        while (cursor.moveToNext()) {
            genres.add(GenreData.getName(cursor));
        }

        // Sets the title of the movie in the action bar
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setTitle(title);

        mCoordinatorLayout.setVisibility(View.VISIBLE);
        mRatingBar.setRating(rating / 2);
        mTitle.setText(title);
        mTextReleaseDate.setText(releaseYear);
        mTextGenres.setText(TextUtils.join(" / ", genres));
        mTextOverview.setText(overview);
        mFab.setImageResource(Utilities.toggleImageResource(isFavorite,
                R.drawable.ic_favorite_full, R.drawable.ic_favorite_empty));

        Glide.with(getContext())
                .load(backdropUrl)
                .asBitmap()
                .into(mImageBackdrop);

        Glide.with(getContext())
                .load(coverUrl)
                .asBitmap()
                .into(new BitmapImageViewTarget(mImageCover) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);

                        scheduleStartPostponedTransition(mImageCover);

                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch dominantSwatch = Utilities.getDominantSwatch(palette);
                                mCollapsingToolbarLayout.setCollapsedTitleTextColor(dominantSwatch.getTitleTextColor());
                                mCollapsingToolbarLayout.setContentScrimColor(dominantSwatch.getRgb());
                                mCollapsingToolbarLayout.setStatusBarScrimColor(
                                        ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                            }
                        });
                    }
                });
    }

    private void loadMovieVideos(Cursor cursor) {
        // If MovieVideo cursor is not available returns
        if (cursor.getCount() == 0) {
            mListVideos.setVisibility(View.GONE);
            return;
        }

        cursor.moveToFirst();

        for (int i = mListVideos.getChildCount() - 1; i >= 1; i--) {
            mListVideos.removeViewAt(i);
        }

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        do {
            final View itemView = inflater.inflate(R.layout.movie_detail_videos_item, mListVideos, false);
            final Button buttonName = (Button)itemView.findViewById(R.id.button_name);

            final String name = MovieVideoData.getName(cursor);
            final String key = MovieVideoData.getKey(cursor);
            final Uri uri = Uri.parse(VIDEO_BASE_URL + key);

            buttonName.setText(name);
            buttonName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });

            mListVideos.addView(itemView);

            if (cursor.isFirst()) {
                mShareUri = uri;
            }
        }
        while (cursor.moveToNext());

        mListVideos.setVisibility(View.VISIBLE);
    }

    private void loadMovieReviews(Cursor cursor) {
        // If MovieReview cursor is not available returns
        if (cursor.getCount() == 0) {
            mListReviews.setVisibility(View.GONE);
            return;
        }

        cursor.moveToFirst();

        for (int i = mListReviews.getChildCount() - 1; i >= 1; i--) {
            mListReviews.removeViewAt(i);
        }

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        do {
            final View itemView = inflater.inflate(R.layout.movie_detail_reviews_item, mListReviews, false);
            final TextView textContent = (TextView)itemView.findViewById(R.id.text_content);
            final TextView textAuthor = (TextView)itemView.findViewById(R.id.text_author);

            textContent.setText(MovieReviewData.getContent(cursor));
            textAuthor.setText(MovieReviewData.getAuthor(cursor));

            mListReviews.addView(itemView);
        }
        while (cursor.moveToNext());

        mListReviews.setVisibility(View.VISIBLE);
    }

    //*** Code found online: it prevents animation from triggering before element has been rendered
    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        getActivity().supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }
}