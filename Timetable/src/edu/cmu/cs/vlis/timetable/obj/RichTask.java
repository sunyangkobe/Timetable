package edu.cmu.cs.vlis.timetable.obj;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class RichTask {
    private Task task;

    public RichTask(Task task) {
        this.task = task;
    }

    public String getTaskName() {
        return task.getName();
    }

    /**
     * get time of the task in "hh:mm a" format
     * 
     * @return time of the task in "hh:mm a" format
     */
    @SuppressLint("SimpleDateFormat")
    public String getSimpleDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(task.getStart_time());
    }

    /**
     * get time of the task in "MM-dd hh:mm a" format, the "MM-dd" will be replaced by "Today" or
     * "Tomorrow" according to the time of the task
     * 
     * @return time of the task in "MM-dd/Today/Tomorrow hh:mm a" format
     */
    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    public String getDetailedDate() {
        String dateText;
        Date currentTime = new Date();
        Date todayDate = new Date(currentTime.getYear(), currentTime.getMonth(),
                currentTime.getMinutes());

        long timeDiff = task.getDate().getTime() - todayDate.getTime();
        int dayDiff = (int) (timeDiff / 1000 / 3600 / 24);

        if (dayDiff == 0) dateText = "Today";
        else if (dayDiff == 1) dateText = "Tomorrow";
        else dateText = new SimpleDateFormat("MM-dd").format(task.getDate());

        return dateText + " " + getSimpleDate();
    }
}
