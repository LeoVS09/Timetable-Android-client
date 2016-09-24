package edu.bonch.leovs09.timetable.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
                else if(dayIsCurrent == 0) synchroniseLesson(row, times.get(i));

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
        int dayNumber = calendar.get(calendar.DAY_OF_WEEK) - 1;
        dayNumber = dayNumber == 0 ? 7 : dayNumber;
        String[] dayNames = getResources().getStringArray(R.array.day_names_full);
        for(int i = 0; i<dayNames.length;i++)
            if(name == dayNames[i]) {
                if(i < dayNumber) return -1;
                else if(i == dayNumber) return 0;
                else return 1;
            }
        return -1;
    }

    private void synchroniseLesson(RelativeLayout lesson,String time){
        int[] iTime = parseTime(time);
        int lessonIsCurrent = isCurrentLesson(iTime);
        if(lessonIsCurrent < 0) {
            lesson.setBackgroundResource(R.drawable.line_bottom_lesson_dark);
            return;
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        // TODO: set future change background
    }

    private int isCurrentLesson(int[] iTime){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(calendar.HOUR,iTime[0]);
        calendar.set(calendar.MINUTE,iTime[1]);
        long startLesson = calendar.getTimeInMillis();
        if(startLesson > now) return 1;
        calendar.set(calendar.HOUR,iTime[2]);
        calendar.set(calendar.MINUTE,iTime[3]);
        long endLesson = calendar.getTimeInMillis();
        if(endLesson < now) return -1;
        return 0;
    }

    private int[] parseTime(String time){
        int[] result = new int[4];
        int dot = time.indexOf(".");
        result[0] = Integer.parseInt(time.substring(0,dot));
        int dash = time.indexOf("-");
        result[1] = Integer.parseInt(time.substring(dot,dash));
        dot = time.lastIndexOf(".");
        result[2] = Integer.parseInt(time.substring(dash,dot));
        result[3] = Integer.parseInt(time.substring(dot));
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