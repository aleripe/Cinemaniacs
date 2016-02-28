/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.returntrue.cinemaniacs.data.MovieData;
import it.returntrue.cinemaniacs.utilities.Preferences;
import it.returntrue.cinemaniacs.utilities.Utilities;

/*
 * Represents a movie list fragment (popular, top rated or favorites)
*/
public class MoviesListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
            MoviesAdapter.OnItemClickListener,
            SharedPreferences.OnSharedPreferenceChangeListener{
    // Defines allowed modes as a fake enumeration
    @IntDef({ POPULAR_MOVIES_MODE, TOP_RATED_MOVIES_MODE, FAVORITE_MOVIES_MODE })
    public @interface Mode {}
    public static final int POPULAR_MOVIES_MODE = 1;
    public static final int TOP_RATED_MOVIES_MODE = 2;
    public static final int FAVORITE_MOVIES_MODE = 3;

    public static final String TAG = MoviesListFragment.class.getSimpleName();
    public static final String BUNDLE_MODE = "mode";

    private static final int MOVIES_LOADER = 1;

    private @Mode int mMode;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private OnItemClickListener mListener;

    /** Provides listeners for click events */
    public interface OnItemClickListener {
        void onItemClick(View view, Uri uri);
    }

    public static MoviesListFragment create(@Mode int mode) {
        // Creates a movie list fragment with a specific mode as argument
        MoviesListFragment moviesList = new MoviesListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MoviesListFragment.BUNDLE_MODE, mode);
        moviesList.setArguments(arguments);
        return moviesList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //noinspection ResourceType (we're sure BUNDLE_MODE is one of the allowed values)
        mMode = getArguments().getInt(BUNDLE_MODE);

        // Registers listener for preference change
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movies, container, false);
        final int spanCount = getResources().getInteger(R.integer.movies_grid_columns);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list_movies);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(getContext());
        mAdapter.setListener(this);

        // Sets RecyclerView's side objects
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mRecyclerView.addItemDecoration(new PaddingItemDecoration(getContext(), spanCount, 2));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup the available loader
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);

        // Sets empty view
        setEmptyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnItemClickListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnItemClickListener");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mMode) {
            case TOP_RATED_MOVIES_MODE:
                return MovieData.loadTopRatedMovies(getContext());
            case FAVORITE_MOVIES_MODE:
                return MovieData.loadFavoriteMovies(getContext());
            default:
                return MovieData.loadPopularMovies(getContext());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.setCursor(cursor);
        setEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    @Override
    public void onFavoriteClick(View view, final long id) {
        Cursor cursor = MovieData.getMovie(getContext(), id);

        if (cursor.moveToFirst()) {
            final boolean isFavorite = MovieData.getIsFavorite(cursor);
            final int rowsAffected = MovieData.setMovieAsFavorite(getContext(), id, !isFavorite);

            if (rowsAffected > 0) {
                final View contentView = getView().getRootView().findViewById(android.R.id.content);
                final String text = getContext().getString(Utilities.toggleStringResource(!isFavorite,
                        R.string.set_as_favorite, R.string.removed_from_favorites));
                final int colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);

                Snackbar.make(contentView, text, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MovieData.setMovieAsFavorite(getContext(), id, isFavorite);
                            }
                        })
                        .setActionTextColor(colorAccent)
                        .show();
            }
        }
    }

    @Override
    public void onItemClick(View view, Uri uri) {
        if (mListener != null) {
            mListener.onItemClick(view, uri);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getActivity() != null) {
            if (key == getString(R.string.preference_is_first_sync_completed)) {
                setEmptyView();
            }
        }
    }

    /** Shows the empty view if necessary */
    private void setEmptyView() {
        TextView textEmpty = (TextView)getView().findViewById(R.id.text_empty);

        if (textEmpty != null) {
            if (mAdapter.getItemCount() == 0) {
                textEmpty.setVisibility(View.VISIBLE);

                if (mMode == FAVORITE_MOVIES_MODE) {
                    textEmpty.setText(getString(R.string.no_available_favorites));
                    return;
                }

                if (Preferences.getIsFirstSyncCompleted(getContext())) {
                    textEmpty.setText(getString(R.string.no_available_movies));
                }
                else {
                    textEmpty.setText(getString(R.string.loading_movies));
                }
            } else {
                textEmpty.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Provides specific RecyclerView item decoration (for padding)
     */
    public static class PaddingItemDecoration extends RecyclerView.ItemDecoration {
        private final Context mContext;
        private final int mSpanCount;
        private final int mSpacing;

        public PaddingItemDecoration(Context context, int spanCount, int spacing) {
            mContext = context;
            mSpanCount = spanCount;
            mSpacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % mSpanCount;

            if (column == 0) {
                outRect.right = dpToPx(mSpacing);
            }
            else if (column == mSpanCount - 1) {
                outRect.left = dpToPx(mSpacing);
            }
            else {
                outRect.left = dpToPx(mSpacing);
                outRect.right = dpToPx(mSpacing);
            }
        }

        /** Converts pixels to dips */
        private int dpToPx(int dp) {
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }
}