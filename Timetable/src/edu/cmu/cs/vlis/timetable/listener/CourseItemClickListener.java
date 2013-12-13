package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import edu.cmu.cs.vlis.timetable.CourseViewActivity;

public class CourseItemClickListener implements View.OnClickListener {
    private Activity currentActivity;
    private int courseId;
    private String courseCode;
    
    public CourseItemClickListener(Activity currentActivity, int courseId, String courseCode) {
        this.currentActivity = currentActivity;
        this.courseId = courseId;
        this.courseCode = courseCode;
    }
    
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(currentActivity, CourseViewActivity.class);
        intent.putExtra("course_id", courseId);
        intent.putExtra("course_code", courseCode);
        currentActivity.startActivity(intent);
    }
}
