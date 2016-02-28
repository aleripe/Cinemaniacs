/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Provides an implementation of the account authenticator service
 */
public class MoviesAuthenticatorService extends Service {
    private MoviesAuthenticator mMoviesAuthenticator;

    @Override
    public void onCreate() {
        mMoviesAuthenticator = new MoviesAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMoviesAuthenticator.getIBinder();
    }
}