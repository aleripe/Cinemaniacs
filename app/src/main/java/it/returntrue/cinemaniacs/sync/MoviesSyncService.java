/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Provides an implementation of the synchronization service
 */
public class MoviesSyncService extends Service {
    private static MoviesSyncAdapter sMoviesSyncAdapter = null;
    private static final Object sMoviesSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sMoviesSyncAdapterLock) {
            if (sMoviesSyncAdapter == null) {
                sMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sMoviesSyncAdapter.getSyncAdapterBinder();
    }
}