package edu.cmu.cs.vlis.timetable.listener;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import edu.cmu.cs.vlis.timetable.CommentViewActivity;
import edu.cmu.cs.vlis.timetable.async.RespondCommentNotificationAsyncTask;
import edu.cmu.cs.vlis.timetable.obj.Message;

public class CommentNotificationClickListener implements View.OnClickListener {
    private Fragment currentFragment;
    private Message message;
    private View rowView;

    public CommentNotificationClickListener(Fragment currentFragment, Message message, View rowView) {
        this.currentFragment = currentFragment;
        this.message = message;
        this.rowView = rowView;
    }

    @Override
    public void onClick(View arg0) {
        if (!message.isRead()) {
            new RespondCommentNotificationAsyncTask(currentFragment, message, rowView)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        Intent intent = new Intent(currentFragment.getActivity(), CommentViewActivity.class);
        intent.putExtra("post_id", message.getReference_id());
        currentFragment.getActivity().startActivity(intent);
    }
}
