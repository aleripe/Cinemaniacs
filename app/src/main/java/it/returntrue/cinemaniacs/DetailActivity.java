/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Represents the application's detail activity (movie informations)
 */
public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // If first creation
        if (savedInstanceState == null) {
            // Creates fragment passing Uri data
            MovieDetailFragment movieDetailFragment =
                    MovieDetailFragment.create(getIntent().getData(), false);

            // Add fragment to interface
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_movie_detail, movieDetailFragment)
                    .commit();

            // Postpone transitions
            supportPostponeEnterTransition();
        }
    }
}