package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import edu.cmu.cs.vlis.timetable.R;

public abstract class LoadingBarAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {
    private Activity currentActivity;
    private View progressIndicator;
    private View mainContent;
    
    // super constructor for sub-AsyncTask in Activity    
    public LoadingBarAsyncTask(Activity currentActivity) {
        this.currentActivity = currentActivity;
        this.progressIndicator = currentActivity.findViewById(R.id.progressIndicator);
        this.mainContent = currentActivity.findViewById(R.id.mainContent);
    }
    
    // super constructor for sub-AsyncTask in Fragment
    public LoadingBarAsyncTask(Activity currentActivity, View view) {
        this.currentActivity = currentActivity;
        this.progressIndicator = view.findViewById(R.id.progressIndicator);
        this.mainContent = view.findViewById(R.id.mainContent);
    }
    
    public Activity getActivity() {
        return this.currentActivity;
    }

    @Override
    protected void onPreExecute() {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.VISIBLE);
        }
        if (mainContent != null) {
            mainContent.setVisibility(View.GONE);
        }  
        preExecute();
    }
    
    @Override
    protected void onPostExecute(Result result) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.GONE);
        }
        if (mainContent != null) {
            mainContent.setVisibility(View.VISIBLE); 
        }
        postExecute(result);
    }
   
    protected void preExecute() {
        
    }
    
    protected void postExecute(Result result) {
        
    }
}
