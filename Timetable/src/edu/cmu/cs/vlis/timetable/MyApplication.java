package edu.cmu.cs.vlis.timetable;

import android.app.Application;

public class MyApplication extends Application {
    private String sessionId = null;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
