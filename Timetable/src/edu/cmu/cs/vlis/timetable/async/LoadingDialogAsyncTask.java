package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public abstract class LoadingDialogAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    private Activity curActivity;
    private ProgressDialog pd;

    public LoadingDialogAsyncTask(Activity activity) {
        this.curActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(curActivity);
        pd.setTitle(getProgressDialogTitle());
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        preExecute();
    }

    @Override
    protected void onPostExecute(Result result) {
        if (this.pd != null && this.pd.isShowing()) {
            this.pd.dismiss();
        }
        postExecute(result);
    }

    protected Activity getActivity() {
        return curActivity;
    }

    protected String getProgressDialogTitle() {
        return "Loading...";
    };

    protected void preExecute() {

    };

    protected void postExecute(Result result) {

    };
}
