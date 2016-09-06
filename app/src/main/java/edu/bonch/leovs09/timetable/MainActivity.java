package edu.bonch.leovs09.timetable;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import edu.bonch.leovs09.timetable.ODT.Day;
import edu.bonch.leovs09.timetable.ODT.Lesson;
import edu.bonch.leovs09.timetable.ODT.Week;
import edu.bonch.leovs09.timetable.ODT.WeekBuilder;
import edu.bonch.leovs09.timetable.REST.RestRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Week week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        try {
            String response = new HttpRequestSetCurrentWeek().execute().get();
            week = new WeekBuilder().buildWeek(response);
        }catch (Exception e){
            Log.e("MainActivity", e.getMessage(), e);
        }
//--------------------------------button for update---------------------------------
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//----------------------------------------------------------------------------------
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_DAY = "section_day";
        private static final String ARG_SECTION_TIMES = "section_times";
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Day day, ArrayList<String> times) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            ObjectMapper objectMapper = new ObjectMapper();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            try {
                args.putString(ARG_SECTION_DAY, objectMapper.writeValueAsString(day));
                args.putString(ARG_SECTION_TIMES, objectMapper.writeValueAsString(times));
            }catch (Exception e){
                Log.e("newInstance::writeJson", e.getMessage(), e);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView dayName = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            ObjectMapper objectMapper = new ObjectMapper();
            Day day ;
            ArrayList<String> times;
            try{
                day = objectMapper.readValue(getArguments().getString(ARG_SECTION_DAY),Day.class);
                times = objectMapper.readValue(getArguments().getString(ARG_SECTION_TIMES),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(ArrayList.class, String.class));

                dayName.setText(day.getName());
                TextView lessonName = (TextView) rootView.findViewById(R.id.lessonName);
                TextView lessonRoom = (TextView) rootView.findViewById(R.id.lessonRoom);
                TextView lessonType = (TextView) rootView.findViewById(R.id.lessonType);
                TextView lessonTeacher = (TextView) rootView.findViewById(R.id.lessonTeacher);
                Lesson lesson = day.getLessons().get(2);
                lessonName.setText(lesson.getName());
                lessonRoom.setText(lesson.getRoom());
                lessonType.setText(lesson.getType());
                lessonTeacher.setText(lesson.getTeacher());
            }catch (Exception e){
                Log.e("onCreateView::readJson", e.getMessage(), e);
            }
//            textView.setText(getArguments().getString(ARG_SECTION_TEXT));
            return rootView;
        }

    }
    private class HttpRequestSetCurrentWeek extends AsyncTask<Void, Void, String> {

        private RestRequest restRequest = new RestRequest();
        @Override
        protected String doInBackground(Void... params) {
            try {

                RestRequest rest = new RestRequest();
                return restRequest.in("currentTimeTable", "ИКПИ-53","2").GetObjAndStatus(String.class).toString();
            } catch (Exception e) {
                Log.e("HttpRequest::Start", e.getMessage(), e);
            }

            return null;
        }

//        @Override
//        protected void onPostExecute(String response) {
//            try {
//                week = new WeekBuilder().buildWeek(response);
//            }catch (Exception e){
//                Log.e("HttpRequest::OnPost",e.getMessage(),e);
//            }
//        }

    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1,week.getDays().get(position),week.getTimes());
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


}
