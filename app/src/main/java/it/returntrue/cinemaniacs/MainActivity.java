/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import it.returntrue.cinemaniacs.sync.MoviesSyncAdapter;

/**
 * Represents the application's main activity (the list of movies)
 */
public class MainActivity extends AppCompatActivity implements MoviesListFragment.OnItemClickListener {
    private static final String FRAGMENT_MOVIE_DETAIL_TAG = "fragment_movie_detail";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar without title and navigation
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Shows appropriate layout
        if (findViewById(R.id.fragment_movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                MovieDetailFragment movieDetailFragment = MovieDetailFragment.create(null, true);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_movie_detail_container, movieDetailFragment,
                                FRAGMENT_MOVIE_DETAIL_TAG)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }

        // Initializes sync adapter
        MoviesSyncAdapter.initialize(this);
    }

    @Override
    public void onItemClick(View view, Uri uri) {
        if (mTwoPane) {
            // Shows fragment for movie details
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.create(uri, true);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_detail_container, movieDetailFragment,
                            FRAGMENT_MOVIE_DETAIL_TAG)
                    .commit();
        }
        else {
            // Opens movie details activity with shared element transition
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);

            String transitionName = getString(R.string.shared_image_transition);
            View viewStart = view.findViewById(R.id.image_cover);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    viewStart,
                    transitionName);

            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }
}