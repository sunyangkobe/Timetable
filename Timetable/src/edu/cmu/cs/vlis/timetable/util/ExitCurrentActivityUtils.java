package edu.cmu.cs.vlis.timetable.util;

import android.app.Activity;
import android.content.Intent;
import edu.cmu.cs.vlis.timetable.LoginActivity;
import edu.cmu.cs.vlis.timetable.MainActivity;
import edu.cmu.cs.vlis.timetable.persistentdatamanager.SessionManager;

public class ExitCurrentActivityUtils {
    public static void logOutToLoginActivity(Activity currentActivity) {
        // clear the session data
        SessionManager sessionManager = new SessionManager(currentActivity);
        sessionManager.clearSessionId();
        Intent logOutIntent = new Intent(currentActivity, LoginActivity.class);
        // set the FLAG_ACTIVITY_CLEAR_TOP flag so the application will not launching a new instance
        // of that activity, but instead re-initiate the existing activity in stack and pop all
        // activities in the stack which are on top it.
        logOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        currentActivity.startActivity(logOutIntent);
    }
    
    public static void exitApplication(Activity currentActivity) {
        // stop the daemon notification thread
        // FetchNotificationDaemonTask.getInstance(currentActivity).stop();
        
        // create a intent to the root activity
        Intent exitIntent = new Intent(currentActivity, MainActivity.class);
        // set the FLAG_ACTIVITY_CLEAR_TOP flag so the application will not launching a new instance
        // of that activity, but instead re-initiate the existing activity in stack and pop all
        // activities in the stack which are on top it.
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // set an exit flag to tell the root activity
        exitIntent.putExtra("EXIT", true);
        currentActivity.startActivity(exitIntent);
    }
}
