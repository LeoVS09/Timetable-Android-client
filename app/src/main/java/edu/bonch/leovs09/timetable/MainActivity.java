package edu.bonch.leovs09.timetable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
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

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.bonch.leovs09.timetable.Adapters.SectionsPagerAdapter;
import edu.bonch.leovs09.timetable.AsynkTasks.HttpRequestSetCurrentWeek;
import edu.bonch.leovs09.timetable.Fragments.PlaceholderFragment;
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

    private int mStartPage = 0;

    private int CurrentWeek = 2;

    private String STATIC_GROUP;

    private Resources resources;

    private String WEEK_IN_HEADER;
    private String WEEK_IS_CURRENT;
    private String KEY_GROUP;
    private String PREFERENCES_FILE_NAME;
    private String CHANGE_GROUP_KEY;


    private void setCurrentWeek(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        calendar.set(2016,9-1,1);
        Date studyStartDate = calendar.getTime();
        Log.d("setCurrentWeek","studyStartDate: " + studyStartDate.toString());

        calendar.setTimeInMillis(date.getTime() - studyStartDate.getTime());
        Log.d("setCurrentWeek","currentWeek: " + calendar.get(calendar.WEEK_OF_YEAR));
        CurrentWeek = calendar.get(calendar.WEEK_OF_YEAR);
        if(CurrentWeek > 1){
            mStartPage = 1;
            if(CurrentWeek > 2){
                mStartPage = 2;
            }
        }
        displasement = CurrentWeek - mStartPage;

    }


    public int getCurrentWeek() {
        return CurrentWeek;
    }

    private void setResources(){
        resources = getResources();
        WEEK_IN_HEADER = resources.getString(R.string.week_is);
        WEEK_IS_CURRENT = resources.getString(R.string.week_current);
        KEY_GROUP = resources.getString(R.string.key_group);
        PREFERENCES_FILE_NAME = resources.getString(R.string.preferences_file_name);
        CHANGE_GROUP_KEY = resources.getString(R.string.change_group_key);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResources();
        setContentView(R.layout.activity_main);
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCurrentWeek();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE_NAME,MODE_PRIVATE);
        STATIC_GROUP = prefs.getString(KEY_GROUP,"");


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),displasement,fragments,STATIC_GROUP);
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(mStartPage);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setAppBarTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setAppBarTitle(mStartPage);
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

    private void setAppBarTitle(int position){
        int sectionNumber = position+displasement;
        String sTextOfWeekName = WEEK_IN_HEADER + Integer.toString(sectionNumber);
        if(sectionNumber == getCurrentWeek())
            sTextOfWeekName += WEEK_IS_CURRENT;
        getSupportActionBar().setTitle(sTextOfWeekName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_group) {
            return startSignActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean startSignActivity(){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(CHANGE_GROUP_KEY,true);
        editor.commit();
        Intent intent = new Intent(this,SignInActivity.class);
        startActivity(intent);
        return true;
    }







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
}
