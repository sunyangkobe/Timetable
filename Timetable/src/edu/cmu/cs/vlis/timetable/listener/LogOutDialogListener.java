package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.content.DialogInterface;

import com.facebook.Session;

import edu.cmu.cs.vlis.timetable.util.ExitCurrentActivityUtils;

// custom listener which is responsible for exiting the application directly
public class LogOutDialogListener implements DialogInterface.OnClickListener {
    private Activity currentActivity;

    public LogOutDialogListener(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ExitCurrentActivityUtils.logOutToLoginActivity(currentActivity);
        if (Session.getActiveSession() != null) {
            Session.getActiveSession().closeAndClearTokenInformation();
        }
    }

}
