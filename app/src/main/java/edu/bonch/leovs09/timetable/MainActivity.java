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

import android.widget.TableLayout;
import android.widget.TableRow;
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

    ProgressDialog progress;

    private AsyncTask backTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        backTask = new HttpRequestSetCurrentWeek().execute();
        try {
            String response = new HttpRequestSetCurrentWeek().execute().get();
            progress.dismiss();
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
            try {
                day = objectMapper.readValue(getArguments().getString(ARG_SECTION_DAY), Day.class);
                times = objectMapper.readValue(getArguments().getString(ARG_SECTION_TIMES),
                        objectMapper.getTypeFactory()
                                .constructCollectionType(ArrayList.class, String.class));

                dayName.setText(day.getName());

                TableRow row = (TableRow) rootView.findViewById(R.id.lesson);

                TextView time = (TextView) rootView.findViewById(R.id.time);
                TextView lessonName = (TextView) rootView.findViewById(R.id.lessonName);
                TextView lessonRoom = (TextView) rootView.findViewById(R.id.lessonRoom);

                time.setText(times.get(0));
                Lesson lesson = day.getLessons().get(0);
                if (lesson.getName().equals("none")) {
                    lessonName.setText("--------");
                    lessonRoom.setText("---");
                } else {
                    lessonName.setText(lesson.getName());
                    lessonRoom.setText(lesson.getRoom());
                }

                TableLayout table = (TableLayout) rootView.findViewById(R.id.section_table);

                for(int i = 1;i<times.size();i++){
                    TableRow newRow = new TableRow(getContext());
                    newRow.setLayoutParams(row.getLayoutParams());
                    newRow.setId(R.id.lessonRoom);

                    TextView newTime = new TextView(getContext());
                    newTime.setLayoutParams(time.getLayoutParams());
                    newTime.setPadding(time.getPaddingLeft(),
                            time.getPaddingTop(),
                            time.getPaddingRight(),
                            time.getPaddingBottom());
                    newTime.setBackground(time.getBackground());
                    newTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    newTime.setTextSize(TypedValue.COMPLEX_UNIT_PX,time.getTextSize());
//                    newTime.setMinWidth(time.getMinWidth());
                    newTime.setId(R.id.lessonRoom);
                    newTime.setText(times.get(i));
                    newRow.addView(newTime);

                    TextView newLessonName = new TextView(getContext());
                    newLessonName.setLayoutParams(lessonName.getLayoutParams());
                    newLessonName.setPadding(lessonName.getPaddingLeft(),
                            lessonName.getPaddingTop(),
                            lessonName.getPaddingRight(),
                            lessonName.getPaddingBottom());
                    newLessonName.setBackground(lessonName.getBackground());
                    newLessonName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    newLessonName.setTextSize(TypedValue.COMPLEX_UNIT_PX,lessonName.getTextSize());
                    newLessonName.setId(R.id.lessonRoom);
                    TextView newLessonRoom = new TextView(getContext());
                    newLessonRoom.setLayoutParams(lessonRoom.getLayoutParams());
                    newLessonRoom.setPadding(lessonRoom.getPaddingLeft(),
                            lessonRoom.getPaddingTop(),
                            lessonRoom.getPaddingRight(),
                            lessonRoom.getPaddingBottom());
                    newLessonRoom.setBackground(lessonRoom.getBackground());
                    newLessonRoom.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    newLessonRoom.setTextSize(TypedValue.COMPLEX_UNIT_PX,lessonRoom.getTextSize());

                    newLessonRoom.setId(R.id.lessonRoom);

                    lesson = day.getLessons().get(i);
                    if (lesson.getName().equals("none")) {
                        newLessonName.setText("--------");
                        newLessonRoom.setText("---");
                    } else {
                        newLessonName.setText(lesson.getName());
                        newLessonRoom.setText(lesson.getRoom());
                    }

                    newRow.addView(newLessonName);
                    newRow.addView(newLessonRoom);

                    table.addView(newRow);
                }

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
            publishProgress(null);
            try {

                RestRequest rest = new RestRequest();
                return restRequest.in("currentTimeTable", "ИКПИ-53","2")
                        .GetObjAndStatus(String.class).toString();
            } catch (Exception e) {
                Log.e("HttpRequest::Start", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//            progress.show();
        }

//        @Override
//        protected void onPostExecute(String response) {
//            progress.dismiss();
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
//            while(backTask.getStatus() != AsyncTask.Status.FINISHED){}
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
