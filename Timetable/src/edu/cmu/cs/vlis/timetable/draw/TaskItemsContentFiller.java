package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.ViewGroup;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.listener.TaskItemClickListener;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.RichTask;
import edu.cmu.cs.vlis.timetable.obj.Task;

public class TaskItemsContentFiller extends ItemsContentFiller {
    public enum TASK_MODE {
        DETAILED_TIME_MODE, SIMPLE_TIME_MODE;
    }

    private Task[] tasks;
    private TASK_MODE mode;
    private Course course;

    public TaskItemsContentFiller(Activity currentActivity, ViewGroup rootView, Task[] tasks,
            TASK_MODE mode, Course course) {
        super(currentActivity, rootView);
        this.tasks = tasks;
        this.mode = mode;
        this.course = course;
    }

    @Override
    public void fillContent() {
        BaseColorLinearLayoutAdapter colorLinearLayoutAdapter;
        if (mode == TASK_MODE.DETAILED_TIME_MODE) {
            colorLinearLayoutAdapter = new SingleColorLinearLayoutAdapter(currentActivity,
                    rootView, R.color.yellow);
        }
        else {
            colorLinearLayoutAdapter = new MultiColorLinearLayoutAdapter(currentActivity, rootView);
        }

        RichTask richTask;
        ItemDrawer itemDrawer;

        for (Task task : tasks) {
            richTask = new RichTask(task);
            itemDrawer = new SimpleItemDrawer(currentActivity, rootView);

            itemDrawer.setElement(0, richTask.getTaskName());

            if (mode == TASK_MODE.DETAILED_TIME_MODE) {
                itemDrawer.setElement(1, richTask.getDetailedDate());
            }
            else {
                itemDrawer.setElement(1, richTask.getSimpleDate());
            }

            // TODO: this course instantiation should be removed after the course can be touched
            // in HomeTabFragment.java | at least need to get course section
            if (course == null) {
                course = new Course();
                course.setId(task.getCourse_id());
                course.setCourse_code(task.getCourse_code());
                course.setCourse_name(task.getCourse_name());
                course.setSection("A");
            }
            itemDrawer.setOnClickListener(new TaskItemClickListener(currentActivity, task, course));

            colorLinearLayoutAdapter.addView(itemDrawer.getView());
        }
    }

}
