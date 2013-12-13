package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.LikePostAsyncTask;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.obj.RichPost;

public class PostLikeButtonListner implements View.OnClickListener {
    Activity currentActivity;
    private Post post;

    public PostLikeButtonListner(Activity currentActivity, Post post) {
        this.currentActivity = currentActivity;
        this.post = post;
    }

    @Override
    public synchronized void onClick(View view) {
        // update the information stored on server
        new LikePostAsyncTask(currentActivity, this, view, post.getId()).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR, new Boolean[] { !post.isUser_like_post() });
    }

    // this call back method will be called by LikePostAsyncTask after successfully communicating
    // with server
    public void setButtonColorAndLikeNumberText(View view) {
        post.setUser_like_post(!post.isUser_like_post());
        /*
         * Fucking android! it will lose the padding information declared in XML file after invoking
         * setBackgroundResource! Which causes the layout to change! (Usually shrink) Especially
         * when you are using layer list to form a button! So a tricky solution is to store the
         * padding information and restore them to the view after setBackgroundResource, which is
         * ugly but do works...
         */
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();

        if (post.isUser_like_post()) {
            view.setBackgroundResource(R.drawable.shadow_button_clicked);
            post.setLike_num(post.getLike_num() + 1);
        }
        else {
            view.setBackgroundResource(R.drawable.shadow_button_unclick);
            post.setLike_num(post.getLike_num() - 1);
        }

        // restore the padding information
        view.setPadding(left, top, right, bottom);

        // update the number of likes and comments text view
        RichPost richPost = new RichPost(post);
        RelativeLayout parentView = (RelativeLayout) view.getParent();
        TextView numberOfLikesAndCommentsTextView = (TextView) parentView
                .findViewById(R.id.numberOfLIkesAndCommentsTextView);
        numberOfLikesAndCommentsTextView.setText(richPost.getNumberOfLikeAndComment());
    }
}
