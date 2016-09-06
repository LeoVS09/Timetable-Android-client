package edu.bonch.leovs09.timetable.ODT;

import org.json.simple.JSONObject;

import java.util.Objects;

/**
 * Created by LeoVS09 on 06.09.2016.
 */
public class Lesson {
    String name;
    String type;
    String room;
    String teacher;


    public Lesson (){}

    public Lesson(String name){
        this.name = name;
    }

    public Lesson(String name, String type, String teacher, String room) {
        this.name = name;
        this.type = type;
        this.teacher = teacher;
        this.room = room;
    }

    public Lesson(JSONObject json){
        Object obj = json.get("name");
        this.name = obj == null ? "null" : obj.toString();
         obj = json.get("type");
        this.type = obj == null ? "null" : obj.toString();
        obj = json.get("teacher");
        this.teacher = obj == null ? "null" : obj.toString();
        obj = json.get("room");
        this.room = obj == null ? "null" : obj.toString();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
