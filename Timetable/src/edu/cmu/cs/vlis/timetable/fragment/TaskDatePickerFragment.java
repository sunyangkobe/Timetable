package edu.cmu.cs.vlis.timetable.fragment;

import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity;

public class TaskDatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        ((AddEditTaskActivity) getActivity()).updateField(getTag(), year, month + 1, day);
    }

    @SuppressWarnings("deprecation")
    public void setInitialValue(Date date) {
        this.year = date.getYear();
        this.month = date.getMonth();
        this.day = date.getDate();
    }

}