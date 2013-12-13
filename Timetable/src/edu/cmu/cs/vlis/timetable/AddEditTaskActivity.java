package edu.cmu.cs.vlis.timetable;

import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.async.AddEditTaskAsyncTask;
import edu.cmu.cs.vlis.timetable.async.DeleteTaskAsyncTask;
import edu.cmu.cs.vlis.timetable.fragment.TaskDatePickerFragment;
import edu.cmu.cs.vlis.timetable.fragment.TaskTimePickerFragment;
import edu.cmu.cs.vlis.timetable.listener.DeleteTaskDialogOnClickListener;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class AddEditTaskActivity extends FragmentActivity implements OnClickListener {

    public enum MODE {
        EDIT("Edit"), NEW("New");
        private String mode;

        private MODE(String mode) {
            this.mode = mode;
        }

        @Override
        public String toString() {
            return mode;
        }
    }

    public enum TYPE {
        TASK("Task"), EXAM("Exam");
        private String type;

        private TYPE(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    private MODE mode;
    private Task data;
    private TYPE type;
    private Course course;
    private EditText taskNameField;
    private EditText startTimeField;
    private EditText endTimeField;
    private EditText dateField;
    private EditText noteLocationField;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedit_task);
        Bundle bundle = getIntent().getExtras();
        type = (TYPE) bundle.getSerializable("type");
        data = (Task) bundle.getSerializable("data");
        mode = (MODE) bundle.getSerializable("mode");
        course = (Course) bundle.getSerializable("course");
        setTitle(course.toString() + " - " + mode.toString() + " " + type.toString());

        ((TextView) findViewById(R.id.addedit_task_name)).setText(type.toString() + " Name");
        ((TextView) findViewById(R.id.addedit_task_name_field)).setHint("E.g. "
                + (type == TYPE.EXAM ? "Midterm Exam" : "Chp. 3 Reading"));
        ((TextView) findViewById(R.id.addedit_task_course_field)).setText(course.toString());
        ((TextView) findViewById(R.id.addedit_task_notes_tag))
                .setText(type == TYPE.EXAM ? "Location" : "Notes");

        taskNameField = ((EditText) findViewById(R.id.addedit_task_name_field));
        // if user is adding a new task, set focus on the task name field
        if (mode == MODE.NEW) {
            taskNameField.requestFocus();
        }
        noteLocationField = ((EditText) findViewById(R.id.addedit_task_notes_field));
        int targetXML = type == TYPE.EXAM ? R.layout.exam_time_item : R.layout.task_time_item;
        LayoutInflater.from(this).inflate(targetXML,
                ((LinearLayout) findViewById(R.id.addedit_task_time_fields)), true);

        startTimeField = (EditText) findViewById(R.id.addedit_task_start_time_field);
        startTimeField.setFocusable(false);
        startTimeField.setKeyListener(null);
        startTimeField.setOnClickListener(this);
        if (type == TYPE.EXAM) {
            endTimeField = (EditText) findViewById(R.id.addedit_task_end_time_field);
            endTimeField.setFocusable(false);
            endTimeField.setOnKeyListener(null);
            endTimeField.setOnClickListener(this);
            noteLocationField.setHint(getResources().getString(R.string.addedit_task_location_hint));
        }
        else {
            noteLocationField.setHint(getResources().getString(R.string.addedit_task_note_hint));
        }

        dateField = ((EditText) findViewById(R.id.addedit_task_date_field));
        dateField.setFocusable(false);
        dateField.setOnKeyListener(null);
        dateField.setOnClickListener(this);

        if (mode == MODE.EDIT) {
            setUpData(data);
            deleteBtn = (Button) findViewById(R.id.addedit_task_delete_task_btn);
            deleteBtn.setText("Delete this " + type.toString().toLowerCase(Locale.getDefault()));
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(this);
        }
        else {
            data = new Task();
        }
    }

    @SuppressWarnings("deprecation")
    private void setUpData(Task task) {
        taskNameField.setText(task.getName());

        startTimeField.setText(Utils.convertSingleToDouble(task.getStart_time().getHours()) + ":"
                + Utils.convertSingleToDouble(task.getStart_time().getMinutes()));
        if (type == TYPE.EXAM) {
            endTimeField.setText(Utils.convertSingleToDouble(task.getEnd_time().getHours()) + ":"
                    + Utils.convertSingleToDouble(task.getEnd_time().getMinutes()));
        }

        dateField.setText((task.getDate().getYear() + 1900) + "-" + (task.getDate().getMonth() + 1)
                + "-" + task.getDate().getDate());

        noteLocationField.setText(type == TYPE.EXAM ? task.getLocation() : task.getNotes());
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
            if (formValidation()) {
                collectTaskData();
                new AddEditTaskAsyncTask(this, mode).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, data);
                return true;
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    private void collectTaskData() {
        data.setName(taskNameField.getText().toString().trim());
        data.setType(type.name().charAt(0) + "");
        if (type == TYPE.EXAM) {
            data.setLocation(noteLocationField.getText().toString().trim());
        }
        else {
            data.setNotes(noteLocationField.getText().toString().trim());
        }

        data.setStart_time(getParsedTime(startTimeField));
        data.setEnd_time(getParsedTime(type == TYPE.EXAM ? endTimeField : startTimeField));
        Date date = getParsedDate(dateField);
        date.setYear(date.getYear() - 1900);
        data.setDate(date);

        data.setCourse_id(course.getId());
    }

    @Override
    public void onClick(View v) {
        if (v == startTimeField || v == endTimeField) {
            Date time = getParsedTime((EditText) v);
            TaskTimePickerFragment newFragment = new TaskTimePickerFragment();
            newFragment.setInitialValue(time);
            newFragment.show(getSupportFragmentManager(), (v == startTimeField ? "start" : "end")
                    + "TimePicker");
        }
        else if (v == dateField) {
            Date date = getParsedDate(dateField);
            TaskDatePickerFragment newFragment = new TaskDatePickerFragment();
            newFragment.setInitialValue(date);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        }
        else if (v == deleteBtn) {
            new AlertDialog.Builder(this)
                    .setMessage(
                            "Are you sure you want to delete this "
                                    + type.toString().toLowerCase(Locale.getDefault()) + "?")
                    .setPositiveButton(
                            "Yes",
                            new DeleteTaskDialogOnClickListener(new DeleteTaskAsyncTask(this), data))
                    .setNegativeButton("No", null).show();
        }
    }

    @SuppressLint("ShowToast")
    private boolean formValidation() {
        if (taskNameField.getText().length() == 0) {
            Toast.makeText(this, "Task name should not be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (startTimeField.getText().length() == 0
                || (type == TYPE.EXAM && endTimeField.getText().length() == 0)) {
            Toast.makeText(this, "Time should not be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (dateField.getText().length() == 0) {
            Toast.makeText(this, "Date should not be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void updateField(String tag, int hourOfDay, int minute) {
        if (tag.equals("startTimePicker")) {
            startTimeField.setText(Utils.convertSingleToDouble(hourOfDay) + ":"
                    + Utils.convertSingleToDouble(minute));
        }
        else if (tag.equals("endTimePicker")) {
            endTimeField.setText(Utils.convertSingleToDouble(hourOfDay) + ":"
                    + Utils.convertSingleToDouble(minute));
        }

    }

    public void updateField(String tag, int year, int i, int day) {
        dateField.setText(year + "-" + i + "-" + day);
    }

    @SuppressWarnings("deprecation")
    private Date getParsedTime(EditText v) {
        if (v.getText().length() == 0) {
            return new Date();
        }
        Date time = new Date();
        time.setHours(Integer.parseInt(v.getText().toString().split(":")[0]));
        time.setMinutes(Integer.parseInt(v.getText().toString().split(":")[1]));
        time.setSeconds(0);
        return time;
    }

    @SuppressWarnings("deprecation")
    private Date getParsedDate(EditText v) {
        if (v.getText().length() == 0) {
            Date date = new Date();
            date.setYear(date.getYear() + 1900);
            return date;
        }
        Date date = new Date();
        date.setYear(Integer.parseInt(v.getText().toString().split("-")[0]));
        date.setMonth(Integer.parseInt(v.getText().toString().split("-")[1]) - 1);
        date.setDate(Integer.parseInt(v.getText().toString().split("-")[2]));
        return date;
    }
}
