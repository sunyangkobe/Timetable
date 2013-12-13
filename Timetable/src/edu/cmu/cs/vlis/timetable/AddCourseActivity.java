package edu.cmu.cs.vlis.timetable;

import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.async.EnrollCourseAsyncTask;
import edu.cmu.cs.vlis.timetable.async.GetCourseDetailsAsyncTask;
import edu.cmu.cs.vlis.timetable.async.LoadingDialogAsyncTask;
import edu.cmu.cs.vlis.timetable.draw.LectureItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.LectureItemsContentFiller.LECTURE_MODE;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.CourseList;

public class AddCourseActivity extends Activity implements OnItemClickListener {
    private AutoCompleteTextView courseCodeView;
    private HashMap<Long, Course> enrolledCourses;
    private Course course;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        enrolledCourses = (HashMap<Long, Course>) getIntent().getExtras().getSerializable(
                "enrolledCourses");
        courseCodeView = (AutoCompleteTextView) findViewById(R.id.course_code_auto_complete);
        new LoadingDialogAsyncTask<Void, Void, Void>(this) {
            @Override
            protected String getProgressDialogTitle() {
                return "Fetching available courses...";
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (CourseList.getInstance().getCourseArray() == null) {
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void postExecute(Void result) {
                ArrayAdapter<Course> adapter = new ArrayAdapter<Course>(getActivity(),
                        R.layout.autocomplete_list_item, CourseList.getInstance().getCourseArray());
                courseCodeView.setAdapter(adapter);
            };
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        courseCodeView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.actionbar_done:
            if (course != null) {
                if (enrolledCourses.containsKey(course.getId())) {
                    Toast.makeText(this, "The course " + course + " is already in your list",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    new EnrollCourseAsyncTask(this).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, course);
                }
            }
            else {
                Toast.makeText(this, "Please select a valid course", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Course selected = (Course) parent.getAdapter().getItem(position);
        new GetCourseDetailsAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selected);
    }

    public void reDrawView(Course course) {
        if (course == null) return;
        this.course = course;

        drawCourseNameArea();
        drawInstructorArea();
        drawLectureArea();
    }

    private void drawInstructorArea() {
        LinearLayout instructorArea = (LinearLayout) findViewById(R.id.instructor_area);
        TextView instructorView = (TextView) findViewById(R.id.instructor_name);
        instructorView.setText(course.getInstructor());
        instructorArea.setVisibility(View.VISIBLE);
    }

    private void drawCourseNameArea() {
        LinearLayout courseNameAreaView = (LinearLayout) findViewById(R.id.course_name_area);
        TextView courseNameView = (TextView) findViewById(R.id.course_name);
        courseNameView.setText(course.getCourse_name());
        courseNameAreaView.setVisibility(View.VISIBLE);
    }

    private void drawLectureArea() {
        if (course.getLectures() == null || course.getLectures().length == 0) return;

        TextView lectureTitle = (TextView) findViewById(R.id.lectures);
        lectureTitle.setVisibility(View.VISIBLE);

        LinearLayout lectureArea = (LinearLayout) findViewById(R.id.lecture_list);
        lectureArea.removeAllViews();
        lectureArea.setVisibility(View.VISIBLE);

        LectureItemsContentFiller lectureItemsContentFiller = new LectureItemsContentFiller(this,
                lectureArea, course.getLectures(), course.getInstructor(),
                LECTURE_MODE.COMBINED_MODE);

        lectureItemsContentFiller.fillContent();
    }
}
