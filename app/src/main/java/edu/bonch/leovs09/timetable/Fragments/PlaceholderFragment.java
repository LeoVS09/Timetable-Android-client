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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import edu.bonch.leovs09.timetable.AsynkTasks.HttpRequestSetCurrentWeek;
//import edu.bonch.leovs09.timetable.Listners.OnClickShortToFull;
import edu.bonch.leovs09.timetable.Listners.OnClickToggle;
import edu.bonch.leovs09.timetable.MainActivity;
import edu.bonch.leovs09.timetable.ODT.Day;
import edu.bonch.leovs09.timetable.ODT.Lesson;
import edu.bonch.leovs09.timetable.ODT.Week;
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
            if(mWeeks[sectionNumber] == null){
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
                LinearLayout dayLayout = (LinearLayout) inflater.inflate(R.layout.day, fragmentLayout, false);
//                TextView dayName = (TextView) inflater.inflate(R.layout.day_name, fragmentLayout, false);
                TextView dayName = (TextView) dayLayout.findViewById(R.id.dayName);
                dayName.setText(day.getName());
//                fragmentLayout.addView(dayName);

//                TableLayout table = (TableLayout) inflater.inflate(R.layout.day_table, fragmentLayout, false);
                addLessons(dayLayout, day, times, inflater);
//                fragmentLayout.addView(table);
                fragmentLayout.addView(dayLayout);
            }
        }
    }

    private void addLessons(LinearLayout dayLayout, final Day day, ArrayList<String> times, final LayoutInflater inflater){
        for(int i = 0;i<times.size();i++){
            Lesson lesson = day.getLessons().get(i);
            if(lesson.getName().equals("none")) continue;

            RelativeLayout row = (RelativeLayout) inflater.inflate(R.layout.lesson_short,dayLayout,false);

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







    public void refresh(){
        if (! this.isDetached()) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

}