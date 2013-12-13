package edu.cmu.cs.vlis.timetable.persistentdatamanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import edu.cmu.cs.vlis.timetable.MyApplication;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class SessionManager {
    private Activity currentActivity;
    private SharedPreferences settings;
    private MyApplication myApplication;

    private final String SESSION_ID_FIELD = "session_id";
    private final String USER_ID_FIELD = "user_id";

    public SessionManager(Activity activity) {
        currentActivity = activity;
        settings = currentActivity.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        myApplication = (MyApplication) currentActivity.getApplication();
    }

    public void setSessionId(String sessionId) {
        // set session id in shared preference
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SESSION_ID_FIELD, sessionId);
        // commit the edits!
        editor.commit();

        // set session id in memory
        myApplication.setSessionId(sessionId);
    }

    public boolean hasSessionId() {
        return (myApplication.getSessionId() != null || (settings.getString(SESSION_ID_FIELD, null)) != null);
    }

    public String getSessionId() {
        if (myApplication.getSessionId() != null) return myApplication.getSessionId();
        else return settings.getString(SESSION_ID_FIELD, null);
    }

    public void syncSessionIdToMemory() {
        if (myApplication.getSessionId() == null) myApplication.setSessionId(settings.getString(
                SESSION_ID_FIELD, null));
    }

    public void clearSessionId() {
        // remove session id from memory
        myApplication.setSessionId(null);
        // remove session id from shared preference
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(SESSION_ID_FIELD);
        editor.commit();
    }
    
    public boolean hasUserId() {
        return (settings.getInt(USER_ID_FIELD, -1) != -1);
    }
    
    public int getUserId() {
        return settings.getInt(USER_ID_FIELD, -1);
    }
        
    public void setUserId(int userId) {
        // set user id in shared preference
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(USER_ID_FIELD, userId);
        // commit the edits!
        editor.commit();
    }
}
