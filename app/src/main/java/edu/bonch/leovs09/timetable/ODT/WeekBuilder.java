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
        dayNames.add(new DayNames("Monday","Пн"));
        this.times = new ArrayList<>();
        times.add("10.45-12.20");
    }

    public Week buildWeek(String json)throws Exception{
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