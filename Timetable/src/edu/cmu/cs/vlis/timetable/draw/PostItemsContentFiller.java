package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.PostCommentAsyncTask;
import edu.cmu.cs.vlis.timetable.fragment.GenericComposeFragment;
import edu.cmu.cs.vlis.timetable.listener.MakeCommentClickListener;
import edu.cmu.cs.vlis.timetable.listener.PostLikeButtonListner;
import edu.cmu.cs.vlis.timetable.listener.PosterNameImageClickListener;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.obj.RichPost;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class PostItemsContentFiller extends ItemsContentFiller {
    public enum POST_MODE {
        COMPLETE_MODE, HIDE_COMMENT_BUTTON;
    }

    private Post[] posts;
    private int courseId;
    private POST_MODE mode;

    public PostItemsContentFiller(Activity curreActivity, ViewGroup rootView, Post[] posts,
            int courseId, POST_MODE mode) {
        super(curreActivity, rootView);
        this.posts = posts;
        this.courseId = courseId;
        this.mode = mode;
    }

    @Override
    public void fillContent() {
        /*
         * if there is no posts exist for this course, create a new TextView indicating the user to
         * click to compose the first post
         */
        if (posts == null || posts.length == 0) {
            /*
             * if courseId <= 0, which means it's not in the course view (probably profile view or
             * comment view), then don't show the "write first post" text
             */
            if (courseId > 0) {
                LayoutInflater inflater = (LayoutInflater) currentActivity.getLayoutInflater();
                TextView noPostTextView = (TextView) inflater.inflate(R.layout.nopost_textview,
                        rootView, false);
                rootView.addView(noPostTextView);

                // set listener for noPostTextView
                noPostTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostCommentAsyncTask postCommentAsyncTask = new PostCommentAsyncTask(
                                currentActivity, courseId);
                        GenericComposeFragment composePostFragment = new GenericComposeFragment(
                                postCommentAsyncTask, "Write your post", "Post it!",
                                Constants.POST_MAX_CHARACTER_NUMBER);
                        composePostFragment.show(currentActivity.getFragmentManager(),
                                "compose_post");
                    }
                });
            }
        }
        else {
            BaseColorLinearLayoutAdapter colorLinearLayoutAdapter = new SingleColorLinearLayoutAdapter(
                    currentActivity, rootView, android.R.color.transparent);

            RichPost richPost;
            PostItemDrawer postItemDrawer;

            for (Post post : posts) {
                richPost = new RichPost(post);
                postItemDrawer = new PostItemDrawer(currentActivity, rootView);

                // set the profile image of the poster
                if (richPost.getAvatarBitmap() != null) {
                    postItemDrawer.setImage(richPost.getAvatarBitmap());
                }

                postItemDrawer.setElement(0, richPost.getPosterName());
                postItemDrawer.setElement(1, richPost.getNumberOfLikeAndComment());
                postItemDrawer.setElement(2, richPost.getPostContent());

                // set listener on poster profile image
                postItemDrawer.setPosterImageOnClickListener(new PosterNameImageClickListener(
                        currentActivity, post.getUser_id()));
                // set listener on poster name
                postItemDrawer.setPosterNameOnClickListener(new PosterNameImageClickListener(
                        currentActivity, post.getUser_id()));

                // if user already likes post, set the background color of the like button as green
                if (richPost.getIsUserLikePost()) {
                    postItemDrawer.setLikeButtonDrawableResource(R.drawable.shadow_button_clicked);
                }
                postItemDrawer.setLikeButtonOnClickListener(new PostLikeButtonListner(
                        currentActivity, post));

                // set comment button
                if (mode == POST_MODE.HIDE_COMMENT_BUTTON) {
                    postItemDrawer.setCommentButtonVisibility(false);
                }
                else {
                    postItemDrawer.setCommentButtonOnClickListener(new MakeCommentClickListener(
                            currentActivity, post));
                }

                colorLinearLayoutAdapter.addView(postItemDrawer.getView());
            }
        }
    }
}
