package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import edu.cmu.cs.vlis.timetable.CourseUpdatesActivity;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SyncCourseUpdatesTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Activity currentActivity;
    private int courseId;
    private Post[] posts;
    private NetworkStatus status;
    
    public SyncCourseUpdatesTask(Activity currentActivity, int courseId) {
        super(currentActivity);
        this.currentActivity = currentActivity;
        this.courseId = courseId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // get all related posts from server
        JsonDataProvider dataProvider = new JsonDataProvider(currentActivity);
        posts = dataProvider.readObject(
                APIAddr.GET_POST + String.valueOf(courseId), null, Post[].class);
        status = dataProvider.getLastNetworkStatus();
        return null;
    }
    
    @Override
    protected void postExecute(Void result) {
        if (status == NetworkStatus.VALID) {
            ((CourseUpdatesActivity) currentActivity).setCourseUpdatePosts(posts);
            ((CourseUpdatesActivity) currentActivity).setSyncTaskComplete(true);
        }
        else {
            Utils.displayNetworkErrorMessage(status, currentActivity);
        }
    }

}
