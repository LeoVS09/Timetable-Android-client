package edu.bonch.leovs09.timetable.ODT;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.simple.JSONObject;

import java.util.Objects;

/**
 * Created by LeoVS09 on 06.09.2016.
 */
@Table(name = "Lessons")
public class Lesson extends Model{
    @Column
    private String name;
    @Column
    private String type;
    @Column
    private String room;
    @Column
    private String teacher;
    @Column(name = "Day")
    private Day day;

    public Lesson (){
        super();
    }

    public Lesson(String name){
        this();
        this.name = name;
    }

    public Lesson(String name, String type, String teacher, String room) {
        this(name);
        this.type = type;
        this.teacher = teacher;
        this.room = room;
    }

    public Lesson(JSONObject json){
        this();
        Object obj = json.get("name");
        this.name = obj == null ? "null" : obj.toString();
         obj = json.get("type");
        this.type = formatType(obj);
        obj = json.get("teacher");
        this.teacher = obj == null ? "null" : obj.toString();
        obj = json.get("room");
        this.room = obj == null ? "null" : obj.toString();
    }

    private String formatType(Object obj){
        String type =  obj == null ? "null" : obj.toString();
        if(type.equals("0")) type = "практика";
        if(type.equals("1")) type = "лабораторная";
        if(type.equals("2")) type = "лекция";
        if(type.equals("-1")) type = "null";
        return type;
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
    @Override
    public String toString(){
        return name + ": " + room;
    }

    public  boolean isNameEmpty(){
        return name.isEmpty() || name.equals("none");
    }

    public Day getDay() {
        return day;
    }

    public Lesson setDay(Day day) {
        this.day = day;
        return this;
    }
}
