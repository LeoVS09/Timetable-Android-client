package edu.bonch.leovs09.timetable.ODT;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by LeoVS09 on 05.09.2016.
 */
@Table(name = "Days")
public class Day extends Model{
    @Column
    private String name;

    @Column(name = "Week")
    private Week week;

    private ArrayList<Lesson> lessons;

    public Day(){
        super();
    }

    public Day(String name) {
        this();
        this.name = name;
    }

    public Day(String name, ArrayList<Lesson> lessons) {
        this(name);
        this.save();
        for(Lesson lesson: lessons) {
            lesson.setDay(this).save();
            Log.d("DayCreated"+name,"save lesson --> " + lesson);
        }
        this.lessons = lessons;
    }

    public String getName() {
        return name;
    }

    public Day setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public Day setLessons(ArrayList<Lesson> lessons) {
        for(Lesson lesson: this.lessons) lesson.delete();
        for(Lesson lesson: lessons) lesson.setDay(this).save();
        this.lessons = lessons;
        return this;
    }
    public Day refresh(){
        if(lessons != null) lessons.clear();
        lessons = new ArrayList<>();
        lessons.addAll(getMany(Lesson.class,"Day"));
        Log.d("RefreshDay"+name,lessons.toString());
        return this;
    }

    public Week getWeek() {
        return week;
    }

    public Day setWeek(Week week) {
        this.week = week;
        return this;
    }

    public boolean haveLessons(){
        boolean have = false;
        for(Lesson lesson: lessons) if(!lesson.isNameEmpty()) have = true;
        if(!lessons.isEmpty()) Log.d("Day","haveLessons :" + lessons.toString());
        return have;
    }

    @Override
    public String toString(){
        String res = name + ": {\n";
        if(lessons != null)
            for(Lesson lesson: lessons) res += lesson.toString() + "\n";
        else res += "null";
        res += "}";
        return res;
    }


}
