package edu.cmu.cs.vlis.timetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity.MODE;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity.TYPE;
import edu.cmu.cs.vlis.timetable.async.PostCommentAsyncTask;
import edu.cmu.cs.vlis.timetable.async.QuitCourseAsyncTask;
import edu.cmu.cs.vlis.timetable.async.SyncCourseViewTask;
import edu.cmu.cs.vlis.timetable.draw.ItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.LectureItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.LectureItemsContentFiller.LECTURE_MODE;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller.POST_MODE;
import edu.cmu.cs.vlis.timetable.draw.TaskItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.TaskItemsContentFiller.TASK_MODE;
import edu.cmu.cs.vlis.timetable.fragment.GenericComposeFragment;
import edu.cmu.cs.vlis.timetable.listener.QuitCourseDialogOnClickListener;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class CourseViewActivity extends Activity implements OnClickListener,
        OnMenuItemClickListener {
    private int courseId;
    private Course course;
    private boolean syncTaskComplete = false;

    private LinearLayout postsLinearLayout;
    private TextView viewAllUpdatesTextView;
    private TextView upcomingTasksTitle;
    private LinearLayout tasksLinearLayout;
    private TextView lectureTitle;
    private LinearLayout lectureLinearLayout;

    private SyncCourseViewTask syncCourseViewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);
        courseId = getIntent().getIntExtra("course_id", -1);

        postsLinearLayout = (LinearLayout) findViewById(R.id.latestUpdatesContentLinearLayout);
        viewAllUpdatesTextView = (TextView) findViewById(R.id.viewAllUpdatesTextView);
        upcomingTasksTitle = (TextView) findViewById(R.id.upcomingTasksTitle);
        tasksLinearLayout = (LinearLayout) findViewById(R.id.upcomingTasksContentLinearLayout);
        lectureTitle = (TextView) findViewById(R.id.lecturesTitle);
        lectureLinearLayout = (LinearLayout) findViewById(R.id.lecturesContentLinearLayout);

        findViewById(R.id.quit_course_btn).setOnClickListener(this);
        findViewById(R.id.viewAllUpdatesTextView).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // if the course_id is not passed to this activity
        if (courseId == -1) {
            Toast.makeText(this, "Course id invalid", Toast.LENGTH_LONG).show();
        }
        else {
            /*
             * the reason why put AsyncTask in onResume rather than onCreate is because we want
             * every time when this activity is popped from stack or resumed from other activity, it
             * will communicate with server and re-sync the content
             */
            syncCourseViewTask = new SyncCourseViewTask(this, courseId);
            syncCourseViewTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    
    @Override
    protected void onDestroy() {
        if (syncCourseViewTask != null && syncCourseViewTask.getStatus() == Status.RUNNING) {
            syncCourseViewTask.cancel(true);
        }
        super.onDestroy();
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    public void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(course.getCourse_code() + course.getSection());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_view_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.related_action:
            openPopupMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void drawPostView(Post[] posts) {
        // remove all child views before adding new ones
        postsLinearLayout.removeAllViews();

        PostItemsContentFiller postItemsContentFiller = new PostItemsContentFiller(this,
                postsLinearLayout, posts, courseId, POST_MODE.COMPLETE_MODE);
        postItemsContentFiller.fillContent();

        // set visibility of ViewAllUpdates text view
        if (posts != null && posts.length > 0) {
            viewAllUpdatesTextView.setVisibility(View.VISIBLE);
        }
        else {
            viewAllUpdatesTextView.setVisibility(View.GONE);
        }
    }

    public void drawTaskView(Task[] tasks) {
        // if there is no upcoming tasks for this course
        String completeCourseCode = course.getCourse_code() + course.getSection();
        if (tasks == null || tasks.length == 0) {
            upcomingTasksTitle.setText("No upcoming tasks for " + completeCourseCode);
            tasksLinearLayout.setVisibility(View.GONE);
        }
        else {
            upcomingTasksTitle.setText("Upcoming tasks for " + completeCourseCode);

            // in case the rootView is set "GONE" before (the course has no task at first, so
            // rootView is set GONE, then user adds a new task and come back to CourseView, the
            // rootView is still GONE, so we should set it as VISIBLE
            tasksLinearLayout.setVisibility(View.VISIBLE);
            // remove all child views before adding new ones
            tasksLinearLayout.removeAllViews();

            TaskItemsContentFiller taskItemsContentFiller = new TaskItemsContentFiller(this,
                    tasksLinearLayout, tasks, TASK_MODE.DETAILED_TIME_MODE, course);
            taskItemsContentFiller.fillContent();
        }
    }

    public void drawLectureView(Lecture[] lectures) {

        if (lectures == null || lectures.length == 0) {
            lectureTitle.setVisibility(View.GONE);
            lectureLinearLayout.setVisibility(View.GONE);
        }
        else {
            // remove all child views before adding new ones
            lectureLinearLayout.removeAllViews();
            ItemsContentFiller lectureItemsContentFiller = new LectureItemsContentFiller(this,
                    lectureLinearLayout, lectures, course.getInstructor(),
                    LECTURE_MODE.COMBINED_MODE);
            lectureItemsContentFiller.fillContent();
        }
    }

    private void composePost() {
        PostCommentAsyncTask postCommentAsyncTask = new PostCommentAsyncTask(this, course.getId());
        GenericComposeFragment composePostFragment = new GenericComposeFragment(
                postCommentAsyncTask, "Write your post", "Post it!",
                Constants.POST_MAX_CHARACTER_NUMBER);
        composePostFragment.show(this.getFragmentManager(), "compose_post");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.viewAllUpdatesTextView:
            Intent intent = new Intent(this, CourseUpdatesActivity.class);
            intent.putExtra("course", course);
            startActivity(intent);
            break;
        case R.id.quit_course_btn:
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to quit this course?")
                    .setPositiveButton(
                            "Yes",
                            new QuitCourseDialogOnClickListener(new QuitCourseAsyncTask(this),
                                    courseId)).setNegativeButton("No", null).show();
            break;
        case R.id.noPostTextView:
            composePost();
            break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.composePostMenuItem:
            composePost();
            return true;
        case R.id.addTaskMenuItem:
            return startNewTaskExamActivity(TYPE.TASK);
        case R.id.addExamMenuItem:
            return startNewTaskExamActivity(TYPE.EXAM);
        default:
            return false;
        }
    }

    private void openPopupMenu() {
        if (!syncTaskComplete) {
            return;
        }

        PopupMenu popup = new PopupMenu(this, findViewById(R.id.related_action));
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.course_view_popup_menu);
        popup.show();
    }

    private boolean startNewTaskExamActivity(TYPE type) {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("mode", MODE.NEW);
        intent.putExtra("course", course);
        startActivity(intent);
        return true;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    
    public void setSyncTaskComplete(boolean isComplete) {
        this.syncTaskComplete = isComplete;
    }

}
