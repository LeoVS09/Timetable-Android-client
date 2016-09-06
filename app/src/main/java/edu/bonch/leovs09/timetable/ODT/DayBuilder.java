package edu.bonch.leovs09.timetable.ODT;

import org.json.simple.JSONObject;

import java.util.ArrayList;


/**
 * Created by LeoVS09 on 06.09.2016.
 */
public class DayBuilder {
    private String name;
    private ArrayList<String> times;

    public DayBuilder(){
        name = "Monday";
    }

    public Day buildDay(JSONObject json){
        ArrayList<Lesson> lessons = new ArrayList<>();
        for(String time: times){
            Object obj =  json.get(time);
            if(obj == null) lessons.add(new Lesson("none"));
            else {
                Lesson lesson = (Lesson) obj;
                lessons.add(lesson);
            }
        }
        return new Day(name,lessons);
    }
    public DayBuilder name(String name){
        this.name = name;
        return this;
    }

    public DayBuilder times(ArrayList<String> times){
        this.times = times;
        return this;
    }
}
