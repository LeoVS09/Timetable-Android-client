package edu.bonch.leovs09.timetable;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
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
import java.util.concurrent.ExecutionException;

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

    private int displasement = 2;

    private Week[] mWeeks = new Week[10];

    private PlaceholderFragment[] fragments = new PlaceholderFragment[10];

    private ProgressDialog progress;

    public Week[]getWeeks() {
        return mWeeks;
    }

    public void setWeeks(Week[] Weeks) {
        this.mWeeks = Weeks;
    }

    public ProgressDialog getProgress() {
        return progress;
    }

    public PlaceholderFragment[] getFragments(){
        return fragments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

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
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.download, container, false);
            LinearLayout fragmentLayout;
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            ObjectMapper objectMapper = new ObjectMapper();



            try {
                int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

//                int sectionNumber = 2;
                MainActivity mainActivity = (MainActivity) getActivity();
                mWeeks  = mainActivity.getWeeks();
                if(mWeeks[sectionNumber] == null){
                    new HttpRequestSetCurrentWeek().id(sectionNumber).activity(mainActivity)
                            .execute( "ИКПИ-53", Integer.toString(sectionNumber));
                }else {
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    fragmentLayout = (LinearLayout) rootView.findViewById(R.id.lin_layout);
                    showWeek(sectionNumber, inflater, fragmentLayout);
                }
                Log.i("PlaceholderFragment","Created");
            }catch (Exception e){
                Log.e("onCreateView::readJson", e.getMessage(), e);
            }
//            textView.setText(getArguments().getString(ARG_SECTION_TEXT));
            return rootView;
        }

        private void showWeek(int sectionNumber,LayoutInflater inflater,LinearLayout fragmentLayout){
            Week week = mWeeks[sectionNumber];
            ArrayList<String> times;
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

        public void refresh(){
//            if (! this.isDetached()) {
                getFragmentManager().beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();
//            }
        }

    }

    public static class DownloadFragment extends Fragment{
        private Week[] mWeeks;


        private static final String ARG_SECTION_NUMBER = "section_number";

        public static DownloadFragment newInstance(int sectionNumber) {
            DownloadFragment fragment = new DownloadFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.download, container, false);
            MainActivity mainActivity = (MainActivity) getActivity();
            mWeeks  = mainActivity.getWeeks();
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

//            if(mWeeks[sectionNumber] == null){
                View downloadContainer = rootView.findViewById(R.id.download_container);
                    int id = View.generateViewId();
                    downloadContainer.setId(id);

                try {
                    new HttpRequestSetCurrentWeek().id(id)
                            .activity((MainActivity) getActivity())
                            .execute( "ИКПИ-53", Integer.toString(sectionNumber));
                } catch (Exception e) {
                    Log.e("PageAdapter:", e.getMessage(), e);
                }
                return rootView;

//            }
//            return PlaceholderFragment.newInstance(sectionNumber).getView();

        }
    }
    private static class HttpRequestSetCurrentWeek extends AsyncTask<String, String, WeekWrapper> {

        private RestRequest restRequest = new RestRequest();
        private MainActivity activity;
        private int idForReplace;
        public HttpRequestSetCurrentWeek activity(MainActivity activity){
            this.activity = activity;
            return this;
        }
        public HttpRequestSetCurrentWeek id(int idForReplace){
            this.idForReplace = idForReplace;
            return this;
        }



        @Override
        protected WeekWrapper doInBackground(String... params) {
            try {
                Log.i("HttpRequest", "Start");
                RestRequest rest = new RestRequest();
                String response = restRequest.in("currentTimeTable", params[0], params[1])
                        .GetObjAndStatus(String.class).toString();
                Log.i("HttpRequest", "Response received");

                return new WeekWrapper(response,params[1]);

            } catch (Exception e) {
                Log.e("HttpRequest::StartError", e.getMessage(), e);
            }

            return null;
        }


        @Override
        protected void onPostExecute(WeekWrapper response) {
//            super.onPostExecute(response);
            Log.i("HttpRequest", "onPost start");
            try {
                Week[] mWeeks = activity.getWeeks();
                Log.i("onPostId",Integer.toString(idForReplace));
                mWeeks[response.getNumOfWeek()] = response.getWeek();
                activity.getFragments()[idForReplace].refresh();
                Log.i("HttpRequest", "finished");
            } catch (Exception e) {
                Log.e("HttpRequest::OnPost", e.getMessage(), e);
            }
        }
    }
    private  static class WeekWrapper{
        private Week week;
        private int numOfWeek;

        public WeekWrapper(String week, String numOfWeek) throws Exception {
            this.week = new WeekBuilder().buildWeek(week);
            this.numOfWeek = Integer.parseInt(numOfWeek);
        }

        public Week getWeek(){
            return week;
        }

        public int getNumOfWeek() {
            return numOfWeek;
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


            fragments[position+displasement] = PlaceholderFragment.newInstance(position+displasement);
            return fragments[position+displasement];
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
