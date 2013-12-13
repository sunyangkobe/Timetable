package edu.cmu.cs.vlis.timetable.listener;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import edu.cmu.cs.vlis.timetable.async.RespondFriendRequestAsyncTask;
import edu.cmu.cs.vlis.timetable.async.RespondFriendRequestAsyncTask.ResponseType;
import edu.cmu.cs.vlis.timetable.obj.Message;

public class RespondFriendRequestClickListener implements View.OnClickListener {
    private Fragment currentFragment;
    private Message message;
    private View rowView;
    private ResponseType responseType;

    public RespondFriendRequestClickListener(Fragment currentFragment, Message message, View rowView,
            ResponseType responseType) {
        this.currentFragment = currentFragment;
        this.message = message;
        this.rowView = rowView;
        this.responseType = responseType;
    }

    @Override
    public void onClick(View arg0) {
        Log.d("cc", "2 " + String.valueOf(message.getUser_from()));
        new RespondFriendRequestAsyncTask(currentFragment, message, rowView, responseType)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
