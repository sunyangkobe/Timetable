package edu.cmu.cs.vlis.timetable;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import edu.cmu.cs.vlis.timetable.async.HoldWelcomePageTask;

/*
 * The entry activity of this App. This activity will show a welcome screen for 2 second and check
 * whether the session information exists in shared preference. If yes, then navigate to "Home"
 * activity and store the "sessionId" into a global variable so other activities could use it;
 * otherwise navigate to the "Login" activity. Author: Chun Chen
 */

// TODO If the user kills the application when he is asked to fill in the university, then after
// restarting we should navigate him to the "FillInUniversity" activity, we haven't handled this
// situation in this version

public class MainActivity extends Activity {
    private HoldWelcomePageTask holdWelcomePageTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // If the "EXIT" flag is set in intent, finish the root activity directly
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        holdWelcomePageTask = new HoldWelcomePageTask(this);
        holdWelcomePageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (holdWelcomePageTask != null
                && holdWelcomePageTask.getStatus() != AsyncTask.Status.FINISHED) {
            holdWelcomePageTask.cancel(true);
        }
    }

}
