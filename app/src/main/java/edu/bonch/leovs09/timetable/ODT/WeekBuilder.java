package edu.bonch.leovs09.timetable.ODT;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

/**
 * Created by LeoVS09 on 06.09.2016.
 */
public class WeekBuilder {
    private ArrayList<String> times;
    private String json;
    private String nameField;
    private ArrayList<DayNames> dayNames;

    public WeekBuilder(){
        this.nameField = "days";
        this.dayNames = new ArrayList<>();
        dayNames.add(new DayNames("Monday","Mon"));
        dayNames.add(new DayNames("Tuesday","Tue"));
        dayNames.add(new DayNames("Wednesday","Wed"));
        dayNames.add(new DayNames("Thursday","Thu"));
        dayNames.add(new DayNames("Friday","Fri"));
        dayNames.add(new DayNames("Saturday","Sat"));
        this.times = new ArrayList<>();
        times.add("9.00-10.35");
        times.add("10.45-12.20");
        times.add("13.00-14.35");
        times.add("14.45-16.20");
        times.add("16.30-18.05");
    }

    public Week buildWeek(String json)throws Exception{
        Log.i("buildWeek",json);
        JSONParser parser = new JSONParser();
        ArrayList<Day> days = new ArrayList<>();
        DayBuilder dayBuilder = new DayBuilder();
        try {
            JSONObject obj = (JSONObject) parser.parse(json);
            obj = (JSONObject) obj.get(nameField);
            for(DayNames dayName: dayNames) {
                days.add(dayBuilder.name(dayName.getFullName())
                        .times(times)
                        .buildDay((JSONObject) obj.get(dayName.getJsonName())));
            }
            Log.i("buildWeekDays",days.toString());
            return new Week(times,days);
        }catch (Exception e){
            Log.e("Week::JSONParse:",e.getMessage(),e);
            throw e;
        }
    }
}
class DayNames{
    private String fullName;
    private String jsonName;

    public DayNames() {
    }

    public DayNames(String fullName, String jsonName) {
        this.fullName = fullName;
        this.jsonName = jsonName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJsonName() {
        return jsonName;
    }

    public void setJsonName(String jsonName) {
        this.jsonName = jsonName;
    }
}