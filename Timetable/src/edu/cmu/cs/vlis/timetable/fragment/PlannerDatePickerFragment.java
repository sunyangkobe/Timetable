package edu.cmu.cs.vlis.timetable.fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.async.SyncSemesterDateAsyncTask;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class PlannerDatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    public enum DatePickerType {
        START_DATE_PICKER("start_date"), END_DATE_PICKER("end_date");
        private String name;

        private DatePickerType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private TextView targetView;
    private DatePickerType type;
    private PlannerFragment parentFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if (targetView == null) throw new IllegalArgumentException();

        Calendar cal = Calendar.getInstance();
        cal.setTime(Utils.parseDate(targetView.getText().toString()));
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        String date = year + ":" + (month + 1) + ":" + day;
        new SyncSemesterDateAsyncTask(parentFragment, type).execute(date);
    }

    public void setTargetView(TextView b) {
        this.targetView = b;
    }

    public void setDatePickerType(DatePickerType type) {
        this.type = type;
    }

    public void setParentFragment(PlannerFragment plannerFragment) {
        this.parentFragment = plannerFragment;
    }

}