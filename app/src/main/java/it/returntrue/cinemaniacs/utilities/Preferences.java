/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs.utilities;

import android.content.Context;
import android.preference.PreferenceManager;

import it.returntrue.cinemaniacs.R;

/**
 * Contains direct access methods to the application preferences
 */
public final class Preferences {
    public static int getSelectedTab(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                context.getString(R.string.preference_selected_tab), 0);
    }

    public static void setSelectedTab(Context context, int selectedTab) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(context.getString(R.string.preference_selected_tab), selectedTab)
                .commit();
    }

    public static boolean getIsFirstSyncCompleted(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.preference_is_first_sync_completed), false);
    }

    public static void setIsFirstSyncCompleted(Context context, boolean isFirstSyncCompleted) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(context.getString(R.string.preference_is_first_sync_completed),
                        isFirstSyncCompleted)
                .commit();
    }
}