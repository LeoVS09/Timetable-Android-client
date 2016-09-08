package edu.bonch.leovs09.timetable;

import android.app.ProgressDialog;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

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

    private Week[] mWeeks = new Week[10];

    public Week[]getWeeks() {
        return mWeeks;
    }

    public void setWeeks(Week[] Weeks) {
        this.mWeeks = Weeks;
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);




        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
//--------------------------------button for update---------------------------------
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                backTask = new HttpRequestSetCurrentWeek().execute("ИКПИ-53","2");
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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
        private static final String ARG_SECTION_WEEKS = "section_weeks";

        private Week[] mWeeks;
        private ProgressDialog progress;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            ObjectMapper objectMapper = new ObjectMapper();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            try {
//
//            }catch (Exception e){
//                Log.e("newInstance::writeJson", e.getMessage(), e);
//            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            LinearLayout fragmentLayout = (LinearLayout) rootView.findViewById(R.id.lin_layout);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            ObjectMapper objectMapper = new ObjectMapper();

            ArrayList<String> times;
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            try {
                MainActivity mainActivity = (MainActivity) getActivity();
                mWeeks  = mainActivity.getWeeks();
                if(mWeeks[sectionNumber] == null){
                    progress = new ProgressDialog(getContext());
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setMessage("Loading data...");

                    WeekWrapper weekWrapper = new HttpRequestSetCurrentWeek().execute("ИКПИ-53",Integer.toString(sectionNumber)).get();
                    mWeeks[weekWrapper.getNumOfWeek()] =  weekWrapper.getWeek();
                    progress.dismiss();
                }
                    Week week = mWeeks[sectionNumber];
                    TextView weekName = (TextView) fragmentLayout.findViewById(R.id.weekName);
                    weekName.setText(sectionNumber + " week");
                    times = week.getTimes();
                    Log.i("onCreateView", "week size: " + week.getDays().size());
                    for (Day day : week.getDays()) {

                        TextView dayName = (TextView) inflater.inflate(R.layout.day_name, fragmentLayout, false);
                        dayName.setText(day.getName());
                        fragmentLayout.addView(dayName);

                        TableLayout table = (TableLayout) inflater.inflate(R.layout.day_table, fragmentLayout, false);
                        addLessons(table, day, times, inflater);
                        fragmentLayout.addView(table);
                    }

            }catch (Exception e){
                Log.e("onCreateView::readJson", e.getMessage(), e);
            }
//            textView.setText(getArguments().getString(ARG_SECTION_TEXT));
            return rootView;
        }

        private void addLessons(TableLayout table,Day day,ArrayList<String> times,LayoutInflater inflater){
            for(int i = 0;i<times.size();i++){
                Lesson lesson = day.getLessons().get(i);
                if(lesson.getName().equals("none")) continue;

                TableRow row = (TableRow) inflater.inflate(R.layout.row,table,false);

                TextView time = (TextView) row.findViewById(R.id.time);
                TextView lessonName = (TextView) row.findViewById(R.id.lessonName);
                TextView lessonRoom = (TextView) row.findViewById(R.id.lessonRoom);

                time.setText(times.get(i));

                lessonName.setText(lesson.getName());
                lessonRoom.setText(lesson.getRoom());

                table.addView(row);

            }
        }

        private class HttpRequestSetCurrentWeek extends AsyncTask<String, String, WeekWrapper> {

            private RestRequest restRequest = new RestRequest();

            @Override
            protected void onPreExecute(){
                progress.setMessage("Loading data...");
                progress.show();
            }
            @Override
            protected WeekWrapper doInBackground(String... params) {
                publishProgress("Loading data...");
                try {
                    Log.i("HttpRequest", "Start");
                    RestRequest rest = new RestRequest();
                    String response = restRequest.in("currentTimeTable", params[0], params[1])
                            .GetObjAndStatus(String.class).toString();
                    Log.i("HttpRequest", "Response received");
                    publishProgress("Processing data...");

                    return new WeekWrapper(response,params[1]);

                } catch (Exception e) {
                    Log.e("HttpRequest::Start", e.getMessage(), e);
                }

                return null;
            }


            protected void onProgressUpdate(String value) {
//            super.onProgressUpdate(value);
                progress.setMessage(value);
                progress.show();
            }

//            @Override
//            protected void onPostExecute(WeekWrapper response) {
////            super.onPostExecute(response);
//                Log.i("HttpRequest", "onPost start");
//                try {
//                    mWeeks.add(response.getNumOfWeek(), response.getWeek());
//                    Log.i("HttpRequest", "finished");
//                } catch (Exception e) {
//                    Log.e("HttpRequest::OnPost", e.getMessage(), e);
//                }
//                progress.dismiss();
//
//            }
        }
        private class WeekWrapper{
            private String week;
            private String numOfWeek;

            public WeekWrapper(String week, String numOfWeek) {
                this.week = week;
                this.numOfWeek = numOfWeek;
            }

            public Week getWeek()throws Exception {
                return new WeekBuilder().buildWeek(week);
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public int getNumOfWeek() {
                return Integer.parseInt(numOfWeek);
            }

            public void setNumOfWeek(String numOfWeek) {
                this.numOfWeek = numOfWeek;
            }
        }

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

            return PlaceholderFragment.newInstance(position+2);
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
