package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import edu.cmu.cs.vlis.timetable.util.ExitCurrentActivityUtils;

// custom listener which is responsible for exiting the application directly
public class ExitApplicationDialogListener implements DialogInterface.OnClickListener {
    private Activity currentActivity;

    public ExitApplicationDialogListener(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d("cc", "clickExit");
        ExitCurrentActivityUtils.exitApplication(currentActivity);
    }

}
