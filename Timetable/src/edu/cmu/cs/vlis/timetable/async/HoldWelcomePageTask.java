package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import edu.cmu.cs.vlis.timetable.LoginActivity;
import edu.cmu.cs.vlis.timetable.persistentdatamanager.SessionManager;

// A subclass of AsyncTask, which is used to sleep for 2s in background, and will navigate to
// Login activity. If the user has logged in before, then will set a flag to tell LoginActivity
// to skip to Navigation Activity directly
public class HoldWelcomePageTask extends AsyncTask<Void, Void, Void> {
    private Activity currentActivity;
    private SessionManager sessionManager;
    boolean isLogin;

    public HoldWelcomePageTask(Activity currentActivity) {
        super();
        this.currentActivity = currentActivity;
        this.sessionManager = new SessionManager(currentActivity);
    }

    @Override
    protected Void doInBackground(Void... arg) {
        // get the session informatino using SessionManager
        if (sessionManager.getSessionId() != null) {
            sessionManager.syncSessionIdToMemory();
            isLogin = true;
        }
        else isLogin = false;

        if (isLogin) {
            new GetSchoolCoursesAsyncTask(currentActivity)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Intent intent;
        intent = new Intent(this.currentActivity, LoginActivity.class);
        // If user has logged in before, set a flag to tell the LoginActivity to skip to
        // NavigationActivity directly, the reason why we don't navigate to the NavigationActivity
        // directly is because we want the LoginActivity to be in the stack any time, so when user
        // logs out we can pop out the stacks on top of LoginActivity rather than initializing a new
        // LoginActivity
        if (this.isLogin) intent.putExtra("ISLOGIN", true);
        currentActivity.startActivity(intent);
    }
}
