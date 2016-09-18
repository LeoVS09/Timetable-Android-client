package edu.bonch.leovs09.timetable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;

import edu.bonch.leovs09.timetable.Adapters.GroupListAdapter;
import edu.bonch.leovs09.timetable.AsynkTasks.HttpRequest;
import edu.bonch.leovs09.timetable.REST.RestRequest;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignInActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
//    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
//    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private ArrayList<String> groups = new ArrayList<>();
    private ListView list;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements

            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
    private Resources resources;

    private SharedPreferences prefs;
    private String KEY_GROUP;
    private String PREFERENCES_FILE_NAME;
    private String CHANGE_GROUP_KEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResources();
        prefs = getSharedPreferences(PREFERENCES_FILE_NAME,MODE_PRIVATE);

        setContentView(R.layout.activity_sign_in);

        mVisible = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Выберете вашу группу");
    }

    private void setResources(){
        resources = getResources();
        KEY_GROUP = resources.getString(R.string.key_group);
        PREFERENCES_FILE_NAME = resources.getString(R.string.preferences_file_name);
        CHANGE_GROUP_KEY = resources.getString(R.string.change_group_key);
    }


    private void setList(){
        View v = findViewById(R.id.progressBarSignIn);
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = parent.indexOfChild(v);

        View rel = getLayoutInflater().inflate(R.layout.fragment_sign_in,parent,false);
        parent.removeView(v);
        parent.addView(rel,index);

        list = (ListView) findViewById(R.id.groups);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        GroupListAdapter<String> groupsAdapter = new GroupListAdapter<>(this,groups);

        list.setAdapter(groupsAdapter);
        list.setSelection(0);

        Button button = (Button) parent.findViewById(R.id.choice_button);
        button.setOnClickListener(new CheckedListener());
    }

    private class CheckedListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String choised = groups.get(list.getCheckedItemPosition());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_GROUP,choised);
            editor.putBoolean(CHANGE_GROUP_KEY,false);
            editor.commit();
            toggle();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        boolean change = prefs.getBoolean(CHANGE_GROUP_KEY,true);
        if(!change){
            String staticGroup = prefs.getString(KEY_GROUP,"null");
            if(!staticGroup.equals("null")) toggle();
        }
        new HttpRequestSetListOfGroups().activity(this).execute();
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);


    }

    private static class HttpRequestSetListOfGroups extends HttpRequest<Object,Void,String> {
        private ArrayList<String> groups;
        @Override
        protected String doInBackground(Object... params){
            try{
                String[] response = new RestRequest().in("ListOfGroups")
                        .GetObjAndStatus(String[].class);
                Log.i("HttpResponseGroups",response[0]);
                JSONParser parser = new JSONParser();
//                groups = (ArrayList<String>) parser.parse(response);
                groups = new ArrayList<>(Arrays.asList(response));

                return response[0];
            }catch (Exception e){
                Log.e("HttpRequest::ListGroups",e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response){
            SignInActivity signInActivity = (SignInActivity) activity;
            signInActivity.groups = groups;
            signInActivity.setList();
        }
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first

//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);

    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
