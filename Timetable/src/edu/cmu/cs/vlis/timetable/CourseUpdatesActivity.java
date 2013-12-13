package edu.cmu.cs.vlis.timetable;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.async.PostCommentAsyncTask;
import edu.cmu.cs.vlis.timetable.async.SyncCourseUpdatesTask;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller.POST_MODE;
import edu.cmu.cs.vlis.timetable.fragment.GenericComposeFragment;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class CourseUpdatesActivity extends Activity {
    private Course course;
    private boolean syncTaskComplete = false;

    private LinearLayout postsLinearLayout;
    private TextView courseUpdatesTitleTextView;

    private SyncCourseUpdatesTask syncCourseUpdatesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_updates);
        this.course = (Course) getIntent().getSerializableExtra("course");
        setupActionBar();

        postsLinearLayout = (LinearLayout) findViewById(R.id.courseUpdatesContentContainer);
        courseUpdatesTitleTextView = (TextView) findViewById(R.id.courseUpdatesTitle);

        if (course == null) {
            Toast.makeText(this, "Course id invalid", Toast.LENGTH_LONG).show();
        }
        else {
            setCourseUpdateTitle();
            syncCourseUpdatesTask = new SyncCourseUpdatesTask(this, course.getId());
            syncCourseUpdatesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void onDestroy() {
        if (syncCourseUpdatesTask != null && syncCourseUpdatesTask.getStatus() == Status.RUNNING) {
            syncCourseUpdatesTask.cancel(true);
        }
        super.onDestroy();
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(course.getCourse_code() + course.getSection());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_updates_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.add_post:
            composePost();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void composePost() {
        if (!syncTaskComplete) {
            return;
        }

        PostCommentAsyncTask postCommentAsyncTask = new PostCommentAsyncTask(this, course.getId());
        GenericComposeFragment composePostFragment = new GenericComposeFragment(
                postCommentAsyncTask, "Write your post", "Post it!",
                Constants.POST_MAX_CHARACTER_NUMBER);
        composePostFragment.show(this.getFragmentManager(), "compose_post");
    }

    public void setCourseUpdatePosts(Post[] posts) {
        // remove all child views before adding new ones
        postsLinearLayout.removeAllViews();

        PostItemsContentFiller postItemsContentFiller = new PostItemsContentFiller(this,
                postsLinearLayout, posts, course.getId(), POST_MODE.COMPLETE_MODE);
        postItemsContentFiller.fillContent();

    }

    private void setCourseUpdateTitle() {
        courseUpdatesTitleTextView.setText("Updates for " + course.getCourse_code()
                + course.getSection());
    }

    public void setSyncTaskComplete(boolean isComplete) {
        this.syncTaskComplete = isComplete;
    }

}
