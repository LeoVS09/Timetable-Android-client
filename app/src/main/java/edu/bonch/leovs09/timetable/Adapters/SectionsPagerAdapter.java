package edu.bonch.leovs09.timetable.Adapters;

/**
 * Created by LeoVS09 on 14.09.2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.bonch.leovs09.timetable.Fragments.PlaceholderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private int displasement = 2;
    private PlaceholderFragment[] fragments;
    private String STATIC_GROUP;

    public SectionsPagerAdapter(FragmentManager fm,int displasement,PlaceholderFragment[] fragments, String staticGroup) {
        super(fm);
        this.displasement = displasement;
        this.fragments = fragments;
        this.STATIC_GROUP = staticGroup;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        int sectionNumber = position+displasement;
//            String sTextOfWeekName = WEEK_IN_HEADER + Integer.toString(sectionNumber);
//            if(sectionNumber == getCurrentWeek())
//                sTextOfWeekName += WEEK_IS_CURRENT;
//            getSupportActionBar().setTitle(sTextOfWeekName);
        fragments[sectionNumber] = PlaceholderFragment.newInstance(sectionNumber,STATIC_GROUP);
        return fragments[sectionNumber];
    }

    @Override
    public int getCount() {
        // Show 6 total pages.
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
        }
        return null;
    }
}
