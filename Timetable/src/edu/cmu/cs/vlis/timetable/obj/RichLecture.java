package edu.cmu.cs.vlis.timetable.obj;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;

public class RichLecture {
    private Lecture lecture;
    
    public RichLecture(Lecture lecture) {
        this.lecture = lecture;
    }
    
    @SuppressLint("SimpleDateFormat")
    public String getDuration() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(lecture.getStart_time()) + " - " + sdf.format(lecture.getEnd_time());
    }
    
    public String getType() {
        if (lecture.getType().equals("L")) return "Lecture";
        else if (lecture.getType().equals("T")) return "Tutorial";
        else return "Recitation";
    }
    
    public String getLocation() {
        return lecture.getLocation();
    }
    
    public String getCourseCode() {
        return lecture.getCourse_code();
    }
    
    public String getWeekDay() {
        return lecture.getWeekday();
    }
}
