package edu.bonch.leovs09.timetable.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.bonch.leovs09.timetable.AsynkTasks.HttpRequestSetCurrentWeek;
//import edu.bonch.leovs09.timetable.Listners.OnClickShortToFull;
import edu.bonch.leovs09.timetable.Listners.OnClickToggle;
import edu.bonch.leovs09.timetable.MainActivity;
import edu.bonch.leovs09.timetable.ODT.Day;
import edu.bonch.leovs09.timetable.ODT.Lesson;
import edu.bonch.leovs09.timetable.ODT.Week;
import edu.bonch.leovs09.timetable.ODT.WeekBuilder;
import edu.bonch.leovs09.timetable.R;

/**
 * Created by LeoVS09 on 14.09.2016.
 */

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String WEEK_IN_HEADER = "Неделя № ";
    private static final String WEEK_IS_CURRENT = " (текущая)";
    private static String STATIC_GROUP;
    private Week[] mWeeks;
    private boolean weekIsCurrent = false;
    private int dayIsCurrent = 1;
    private Handler refresher;
    private ProgressBar progressBar;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber,String staticGroup) {
        STATIC_GROUP = staticGroup;
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

        refresher = new RefreshHandler(this);

        try {
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

//                int sectionNumber = 2;
            MainActivity mainActivity = (MainActivity) getActivity();
            mWeeks  = mainActivity.getWeeks();
            weekIsCurrent = sectionNumber == mainActivity.getCurrentWeek();
            if(mWeeks[sectionNumber] == null){
                Week weekInDB = new WeekBuilder(getResources()).getWeekFromDB(sectionNumber);
                if(weekInDB != null){
                    Log.d("onCreatePlaceHolder","Searched in bd:" + weekInDB);
                    mWeeks[sectionNumber] = weekInDB;
                    refresh();
                }
                new HttpRequestSetCurrentWeek().id(sectionNumber).activity(mainActivity)
                        .execute( STATIC_GROUP, Integer.toString(sectionNumber));
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

    private class RefreshHandler extends Handler{
        PlaceholderFragment fragment;

        public RefreshHandler(PlaceholderFragment fragment){
            super();
            this.fragment = fragment;
        }
        //TODO: refactor constance to finals
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("handleMessage",msg.what + ": " + msg.arg1);
            if(msg.what == 1) {
                removeMessages(1);
                fragment.refresh();

            }else if(msg.what == 2){
                //TODO: listening while progress bar is removing
                progressBar.setProgress(msg.arg1);
            } else if(msg.what == 3){
                ViewGroup parent = (ViewGroup) progressBar.getParent();
                parent.removeView(progressBar);
                executorService.shutdownNow();
                progressBar = null;
            }
        }
    }

    private void showWeek(int sectionNumber,LayoutInflater inflater,LinearLayout fragmentLayout){
        Week week = mWeeks[sectionNumber];
        ArrayList<String> times;
//            TextView weekName = (TextView) fragmentLayout.findViewById(R.id.weekName);
        String sTextOfWeekName = WEEK_IN_HEADER + Integer.toString(sectionNumber);
        if(sectionNumber == ((MainActivity)getActivity()).getCurrentWeek())
            sTextOfWeekName += WEEK_IS_CURRENT;
//            weekName.setText(sTextOfWeekName);

        times = week.getTimes();


        Log.i("onCreateView", "showWeek");
        for (Day day : week.getDays()) {
            if(day.haveLessons()) {
                if(weekIsCurrent) dayIsCurrent = isCurrentDay(day.getName());
                LinearLayout dayLayout = (LinearLayout) inflater.inflate(R.layout.day, fragmentLayout, false);
//                TextView dayName = (TextView) inflater.inflate(R.layout.day_name, fragmentLayout, false);
                TextView dayName = (TextView) dayLayout.findViewById(R.id.dayName);
                dayName.setText(day.getName());
//                fragmentLayout.addView(dayName);

//                TableLayout table = (TableLayout) inflater.inflate(R.layout.day_table, fragmentLayout, false);
                addLessons(dayLayout, day, times, inflater);
//                fragmentLayout.addView(table);
                    if(weekIsCurrent && dayIsCurrent < 0)
                        dayLayout.setBackgroundResource(R.drawable.box_shadow_dark);

                fragmentLayout.addView(dayLayout);
            }
        }
    }

    private void addLessons(LinearLayout dayLayout, final Day day, ArrayList<String> times, final LayoutInflater inflater){
        for(int i = 0;i<times.size();i++){
            Lesson lesson = day.getLessons().get(i);
            if(lesson.getName().equals("none")) continue;

            RelativeLayout row = (RelativeLayout) inflater.inflate(R.layout.lesson_short,dayLayout,false);
            if(weekIsCurrent) {
                if(dayIsCurrent < 0)
                    row.setBackgroundResource(R.drawable.line_bottom_lesson_dark);
                else if(dayIsCurrent == 0) synchroniseLesson(row, times.get(i),inflater);

            }
            row.setOnClickListener(new OnClickToggle(i,day.getLessons().get(i),inflater,times.get(i),true));

            TextView time = (TextView) row.findViewById(R.id.timeStart);
            TextView lessonName = (TextView) row.findViewById(R.id.lessonName);
            TextView lessonRoom = (TextView) row.findViewById(R.id.lessonRoom);

            time.setText(OnClickToggle.startOfLesson(times.get(i)));

            lessonName.setText(lesson.getName());

            lessonRoom.setText((lesson.getRoom() == null || lesson.getRoom().equals("null"))
                    ? " " : lesson.getRoom());

            dayLayout.addView(row);

        }
    }



    private int isCurrentDay(String name){
        Calendar calendar = Calendar.getInstance();
        int dayNumber = calendar.get(calendar.DAY_OF_WEEK) - 2;
        dayNumber = dayNumber == -1 ? 6 : dayNumber;
        String[] dayNames = getResources().getStringArray(R.array.day_names_full);
        for(int i = 0; i < dayNames.length; i++)
            if(name.equals(dayNames[i])) {
                if(i < dayNumber) return -1;
                else if(i == dayNumber) return 0;
                else return 1;
            }
        return -1;
    }
    boolean progressUpdaterActive = false;
    private void synchroniseLesson(RelativeLayout lesson,String time,LayoutInflater inflater){
        int[] times = parseTime(time);
        final long leftToEnd = leftBeforeEndLesson(times);
        if(leftToEnd <= 0) {
            lesson.setBackgroundResource(R.drawable.line_bottom_lesson_dark);
            return;
        }
        refresher.sendEmptyMessageDelayed(1,leftToEnd+1000);
        long leftToBegin = leftBeforeBeginLesson(times);
        if(leftToBegin <= 0){
            Log.d("synchroniseLesson","setProgress:" + progressBar);
            if(progressBar != null){
                progressBar = null;}
            progressBar = (ProgressBar) inflater
                    .inflate(R.layout.progress_lesson,lesson,false);
            progressBar.setMax(100);
            progressBar.setProgress(0);
            lesson.addView(progressBar);
            if(!progressUpdaterActive) {
                progressUpdaterActive = true;
                executorService.submit(new ProgressUpdater(startOfLesson(times),
                        endOfLesson(times)));
            }
        }else
            refresher.sendEmptyMessageDelayed(1,leftToBegin+1000);

    }
    private class ProgressUpdater implements Runnable{
        private long start;
        private long end;
        public ProgressUpdater(long start,long end){
            this.start = start;
            this.end = end;
            Log.d("ProgressUpdater","created");
        }
        @Override
        public void run(){
            Log.d("ProgressUpdater","start");
            try {
                long now = Calendar.getInstance().getTimeInMillis();
                while (end > now) {
                    int progress = (int)((((double)(end - now)) / (end - start))*100);
                    progress = 100 - progress;
                    Log.d("ProgressUpdater","progress: " + progress);
                    refresher.sendMessage(refresher.obtainMessage(2, progress,0));
                    TimeUnit.SECONDS.sleep(1);
                    now = Calendar.getInstance().getTimeInMillis();
                }
                refresher.sendEmptyMessage(3);
                executorService.shutdown();
            }catch (Exception e){
                Log.e("ProgressUpdater",e.getMessage(),e);
            }
            finally {
                if (!executorService.isTerminated()) {
                    Log.e("ProgressUpdater","cancel non-finished tasks");
                }
                executorService.shutdownNow();
            }
        }
    }


    //TODO: refresh this method to one method ----
    private long leftBeforeEndLesson(int[] iTime){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(calendar.HOUR_OF_DAY,iTime[2]);
        calendar.set(calendar.MINUTE,iTime[3]);
        long endLesson = calendar.getTimeInMillis();
        return endLesson - now;
    }
    private String arrayToString(int[] iTime){
        return iTime[0] + ":" + iTime[1] + "/" + iTime[2] + ":" + iTime[3];
    }
    private long endOfLesson(int [] iTime){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(calendar.HOUR_OF_DAY,iTime[2]);
        calendar.set(calendar.MINUTE,iTime[3]);
        return calendar.getTimeInMillis();
    }

    private long leftBeforeBeginLesson(int[] iTime){
        Log.d("leftBeforeBeginLesson","start:" + arrayToString(iTime));
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(calendar.HOUR_OF_DAY,iTime[0]);
        calendar.set(calendar.MINUTE,iTime[1]);
        long startLesson = calendar.getTimeInMillis();
        startLesson -= now;
        Log.d("leftBeforeBeginLesson","end:" + startLesson);
        return startLesson;
    }
    private long startOfLesson(int[] iTime){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(calendar.HOUR_OF_DAY,iTime[0]);
        calendar.set(calendar.MINUTE,iTime[1]);
        return calendar.getTimeInMillis();
    }
    //----------------------------------------------
    private int[] parseTime(String time){
        int[] result = new int[4];
        int dot = time.indexOf(".");
        result[0] = Integer.parseInt(time.substring(0,dot));
        int dash = time.indexOf("-");
        result[1] = Integer.parseInt(time.substring(dot+1,dash));
        dot = time.lastIndexOf(".");
        result[2] = Integer.parseInt(time.substring(dash+1,dot));
        result[3] = Integer.parseInt(time.substring(dot+1));
        Log.d("parseTime",result.toString());
        return result;
    }



    public void refresh(){
        if (!this.isDetached()) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

}