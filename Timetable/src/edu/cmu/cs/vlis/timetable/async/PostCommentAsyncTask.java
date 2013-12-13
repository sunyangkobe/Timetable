package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import edu.cmu.cs.vlis.timetable.CommentViewActivity;
import edu.cmu.cs.vlis.timetable.CourseUpdatesActivity;
import edu.cmu.cs.vlis.timetable.CourseViewActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.fragment.GenericComposeFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class PostCommentAsyncTask extends GenericComposeFragment.ComposeCallback {
    private Activity currentActivity;
    private String commentContent;
    private Integer courseId;
    private Integer parentCommentId;
    private NetworkStatus status = NetworkStatus.NETWORK_ERROR;

    public PostCommentAsyncTask(Activity currentActivity, Integer courseId) {
        super(currentActivity);
        this.currentActivity = currentActivity;
        this.courseId = courseId;
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Posting...";
    }

    @Override
    protected Void doInBackground(Void... params) {
        JsonDataProvider jsonDataProvider = new JsonDataProvider(currentActivity);
        Map<String, String> httpParams = new HashMap<String, String>();

        httpParams.put("content", commentContent);
        httpParams.put("course_id", String.valueOf(courseId));
        if (parentCommentId != null) {
            httpParams.put("parent", String.valueOf(parentCommentId));
        }

        jsonDataProvider.readJsonNode(APIAddr.POST_COMMENT, httpParams);
        status = jsonDataProvider.getLastNetworkStatus();
        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, getActivity().getApplicationContext());
            return;
        }
        if (currentActivity.getClass().equals(CourseViewActivity.class)) {
            new SyncCourseViewTask(currentActivity, courseId).execute();
            currentActivity.findViewById(R.id.viewAllUpdatesTextView).setVisibility(View.VISIBLE);
        }
        else if (currentActivity.getClass().equals(CommentViewActivity.class)) {
            new SyncCommentViewTask(currentActivity, parentCommentId).execute();
            ((CommentViewActivity) currentActivity).closeKeyboardRemoveContent();
        }
        else if (currentActivity.getClass().equals(CourseUpdatesActivity.class)) {
            new SyncCourseUpdatesTask(currentActivity, courseId).execute();
        }
    }

    @Override
    public void setContent(String content) {
        this.commentContent = content;     
    }
    
    public void setParentCommentId(int parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

}
