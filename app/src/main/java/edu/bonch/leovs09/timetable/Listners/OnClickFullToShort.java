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

//public class OnClickFullToShort extends OnClickToggle {
//    private int i;
//    private Lesson lesson;
//    private LayoutInflater inflater;
//    private String time;
//    public OnClickFullToShort(int indexOfLesson,Lesson lesson,LayoutInflater inflater,String time){
//        this.i = indexOfLesson;
//        this.lesson = lesson;
//        this.inflater = inflater;
//        this.time = time;
//    }
//    @Override
//    public void onClick(View v){
//        ViewGroup parent = (ViewGroup) v.getParent();
//        int index = parent.indexOfChild(v);
//        parent.removeView(v);
//        v = inflater.inflate(R.layout.lesson_short,parent,false);
//        TextView timeStart = (TextView) v.findViewById(R.id.timeStart);
//        TextView lessonName = (TextView) v.findViewById(R.id.lessonName);
//        TextView lessonRoom = (TextView) v.findViewById(R.id.lessonRoom);
//        timeStart.setText(startOfLesson(time));
//        lessonName.setText(lesson.getName());
//        lessonRoom.setText((lesson.getRoom() == null || lesson.getRoom().equals("null"))
//                ? " " : lesson.getRoom());
//        v.setOnClickListener(new OnClickShortToFull(i,lesson,inflater,time));
//        parent.addView(v,index);
//    }
//
//}