package edu.cmu.cs.vlis.timetable.obj;

import java.util.Date;

public class RichMessage {
    private Message message;

    public RichMessage(Message message) {
        this.message = message;
    }

    public String getTimeIntervalFromSentTimeToNow() {
        long timeIntervalInSeconds = (new Date().getTime() - message.getSent_date().getTime()) / 1000;
        long day = 0, hour = 0, minute = 0;
        
        day = timeIntervalInSeconds / (3600 * 24);
        timeIntervalInSeconds %= (3600 * 24);
        hour = timeIntervalInSeconds / (3600);
        timeIntervalInSeconds %= 3600;
        minute = timeIntervalInSeconds / 60;

        if (day != 0) {
            if (day == 1) return "1 day ago";
            else return String.valueOf(day) + " days ago";
        }
        
        if (hour != 0) {
            if (hour == 1) return "1 hour ago";
            else return String.valueOf(hour) + " hours ago";
        }
        
        if (minute != 0) {
            if (minute == 1) return "1 minute ago";
            else return String.valueOf(minute) + " minutes ago";
        }
        
        return "just now";
    }
}
