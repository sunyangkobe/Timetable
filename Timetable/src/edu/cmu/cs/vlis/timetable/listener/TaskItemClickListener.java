package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity.MODE;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity.TYPE;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.Task;

public class TaskItemClickListener implements OnClickListener {

    private Activity curActivity;
    private Task task;
    private Course course;

    public TaskItemClickListener(Activity currentActivity, Task task, Course course) {
        this.curActivity = currentActivity;
        this.task = task;
        this.course = course;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(curActivity, AddEditTaskActivity.class);
        intent.putExtra("type", task.getType().equals("E") ? TYPE.EXAM : TYPE.TASK);
        intent.putExtra("mode", MODE.EDIT);
        intent.putExtra("data", task);
        intent.putExtra("course", course);
        curActivity.startActivity(intent);

    }
}
