package edu.bonch.leovs09.timetable.ODT;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by LeoVS09 on 05.09.2016.
 */
public class Day {
    String name;
    ArrayList<Lesson> lessons;
    public Day(){}

    public Day(String name) {
        this.name = name;
    }

    public Day(String name, ArrayList<Lesson> lessons) {
        this.name = name;
        this.lessons = lessons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }


}
