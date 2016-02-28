/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Extends the base Application to allow custom behavior
 * */
public class MoviesApplication extends Application {
    private int mGridCoverHeight = 0;

    public int getGridCoverHeight(){
        return mGridCoverHeight;
    }

    public void setGridCoverHeight(int gridCoverHeight){
        mGridCoverHeight = gridCoverHeight;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Stetho to provide Chrome inspect integration
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(getApplicationContext()));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }
}