/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.returntrue.cinemaniacs.utilities.Preferences;

/**
 * Represents the movies fragment
 */
public class MoviesFragment extends Fragment {
    public static final String TAG = MoviesFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets fragment has an options menu
        setHasOptionsMenu(true);

        // Reset grid cover height to 0 to allow correct measurement
        ((MoviesApplication)getActivity().getApplicationContext()).setGridCoverHeight(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getContext();
        final View view = inflater.inflate(R.layout.fragment_movies_list, container, false);

        // Adds specific tabs to the TabLayout
        final TabLayout tabs = (TabLayout)view.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(getString(R.string.popular)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.top_rated)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.favorites)));

        // Sets ViewPager and its Adapter
        final ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(
                getActivity().getSupportFragmentManager(),
                tabs.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        // Load selected tab preference
        viewPager.setCurrentItem(Preferences.getSelectedTab(context));

        // Sets TabLayout selected listener to set current tab
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                // Set and save selected tab preference
                viewPager.setCurrentItem(position);
                Preferences.setSelectedTab(context, position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    /**
     * Represents the ViewPager adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        private final int mTabCount;
        private final ArrayList<MoviesListFragment> mFragments;

        public PagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            mTabCount = tabCount;

            // Creates the list of fragments that will be used for navigation
            mFragments = new ArrayList<>(tabCount);
            mFragments.add(MoviesListFragment.create(MoviesListFragment.POPULAR_MOVIES_MODE));
            mFragments.add(MoviesListFragment.create(MoviesListFragment.TOP_RATED_MOVIES_MODE));
            mFragments.add(MoviesListFragment.create(MoviesListFragment.FAVORITE_MOVIES_MODE));
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabCount;
        }
    }
}