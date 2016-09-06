package edu.bonch.leovs09.timetable.ODT;

/**
 * Created by LeoVS09 on 06.09.2016.
 */
public class Lesson {
    String name;
    int type;
    String teacher;
    String room;

    public Lesson (){}

    public Lesson(String name){
        this.name = name;
    }

    public Lesson(String name, int type, String teacher, String room) {
        this.name = name;
        this.type = type;
        this.teacher = teacher;
        this.room = room;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
