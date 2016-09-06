package edu.bonch.leovs09.timetable.ODT;

import android.util.Log;

import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Created by LeoVS09 on 06.09.2016.
 */
public class Week {
    private ArrayList<String> times;
    private ArrayList<Day> days;

    public Week(){}

    public Week(ArrayList<String> times){
        this.times = times;
    }

    public Week(ArrayList<String> times,ArrayList<Day> days){
        this(times);
        this.days = days;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

}


