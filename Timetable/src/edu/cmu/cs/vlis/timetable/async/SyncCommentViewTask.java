package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import edu.cmu.cs.vlis.timetable.CommentViewActivity;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.obj.PostAndRelatedComments;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SyncCommentViewTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Activity currentActivity;
    private int postId;
    private Post post;
    private Post[] comments;
    private NetworkStatus status;

    public SyncCommentViewTask(Activity currentActivity, int postId) {
        super(currentActivity);
        this.currentActivity = currentActivity;
        this.postId = postId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JsonDataProvider dataProvider = new JsonDataProvider(currentActivity);
        String apiUrl = APIAddr.GET_POST_RELATED_COMMENT + "?id=" + String.valueOf(postId);
        PostAndRelatedComments postAndRelatedComments = dataProvider.readObject(apiUrl, null,
                PostAndRelatedComments.class);
        post = postAndRelatedComments.getPost();
        comments = postAndRelatedComments.getComments();
        
        status = dataProvider.getLastNetworkStatus();
        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentActivity);
        }
        else {
            ((CommentViewActivity) currentActivity).setPostView(post);
            ((CommentViewActivity) currentActivity).setCommentsView(comments);
            ((CommentViewActivity) currentActivity).setPost(post);
            ((CommentViewActivity) currentActivity).setCommentButtonClickListner();
        }
    }

}
