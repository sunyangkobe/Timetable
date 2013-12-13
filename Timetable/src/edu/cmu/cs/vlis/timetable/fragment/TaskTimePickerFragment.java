package edu.cmu.cs.vlis.timetable.fragment;

import java.util.Date;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity;

public class TaskTimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    private int hour;
    private int minute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ((AddEditTaskActivity) getActivity()).updateField(getTag(), hourOfDay, minute);
    }

    @SuppressWarnings("deprecation")
    public void setInitialValue(Date date) {
        this.hour = date.getHours();
        this.minute = date.getMinutes();
    }

}