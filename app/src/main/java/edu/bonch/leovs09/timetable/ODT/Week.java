package edu.bonch.leovs09.timetable.ODT;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Created by LeoVS09 on 06.09.2016.
 */
@Table(name = "Weeks")
public class Week extends Model{
    @Column(name="Number",index = true, unique = true)
    private int number;

    private ArrayList<String> times;


    private ArrayList<Day> days;

    public Week(){
        super();
    }
    public Week(int number){
        this();
        this.number = number;
    }

    public Week(int number,ArrayList<String> times){
        this(number);
        this.times = times;
    }

    public Week(int number,ArrayList<String> times,ArrayList<Day> days){
        this(number,times);
        for(Day day: days) day.setWeek(this).save();
        this.days = days;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public Week setTimes(ArrayList<String> times) {
        this.times = times;
        return this;
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public Week refresh(){
        days = (ArrayList<Day>) getMany(Day.class,"Week");
        for(Day day: days) day.refresh();
        return this;
    }
    public Week setDays(ArrayList<Day> days) {
        for(Day day: this.days) {
            for(Lesson lesson: day.getLessons())lesson.delete();
            day.delete();
        }
        for(Day day: days) day.setWeek(this).save();
        this.days = days;
        return this;
    }

    public static Week getWeekFromDB(int number){
        return new Select()
                .from(Week.class)
                .where("Number = ?",number)
                .executeSingle();
    }
    
    @Override
    public String toString(){
        String res = "Week " + number + ": {\n";
        for (Day day: days) res += day + "\n";
        res += "}";
        return res;
    }

}


