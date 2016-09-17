package edu.bonch.leovs09.timetable.Listners;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.bonch.leovs09.timetable.ODT.Lesson;
import edu.bonch.leovs09.timetable.R;

/**
 * Created by LeoVS09 on 14.09.2016.
 */
public class OnClickToggle implements View.OnClickListener{
    public static String startOfLesson(String fullTime){
        int dr = fullTime.indexOf("-");
        return fullTime.substring(0,dr);
    }
    public static String endOfLesson(String fullTime){
        int dr = fullTime.indexOf("-");
        return fullTime.substring(dr+1);
    }
    private boolean nowIsShort = true;
    private int i;
    private Lesson lesson;
    private LayoutInflater inflater;
    private String time;
    public OnClickToggle(int indexOfLesson, Lesson lesson, LayoutInflater inflater, String time, boolean nowIsShort){
        this.i = indexOfLesson;
        this.lesson = lesson;
        this.inflater = inflater;
        this.time = time;
        this.nowIsShort = nowIsShort;
    }

    @Override
    public void onClick(View v){
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = parent.indexOfChild(v);
        parent.removeView(v);

        if(nowIsShort) v = inflater.inflate(R.layout.lesson_full,parent,false);
        else v = inflater.inflate(R.layout.lesson_short,parent,false);


        TextView timeStart = (TextView) v.findViewById(R.id.timeStart);
        TextView lessonName = (TextView) v.findViewById(R.id.lessonName);
        TextView lessonRoom = (TextView) v.findViewById(R.id.lessonRoom);
        timeStart.setText(startOfLesson(time));
        lessonName.setText(lesson.getName());
        lessonRoom.setText((lesson.getRoom() == null || lesson.getRoom().equals("null"))
                ? " " : lesson.getRoom());

        if(nowIsShort){
            TextView timeEnd = (TextView) v.findViewById(R.id.timeEnd);
            TextView teacher = (TextView) v.findViewById(R.id.teacher);
            TextView type = (TextView) v.findViewById(R.id.type);
            timeEnd.setText(endOfLesson(time));

            type.setText(((lesson.getType() == null || lesson.getType().equals("null"))
                    ? " " : lesson.getType()));
            teacher.setText((lesson.getTeacher() == null || lesson.getTeacher().equals("null"))
                    ? " " : lesson.getTeacher());
            v.setOnClickListener(new OnClickToggle(i,lesson,inflater,time,false));

        }else v.setOnClickListener(new OnClickToggle(i,lesson,inflater,time,true));
        parent.addView(v,index);
    }



}
