package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;

public class LectureTimeSlotItemDrawer extends ItemDrawer {

    public LectureTimeSlotItemDrawer(Activity currentActivity, ViewGroup rootView) {
        LayoutInflater inflater = LayoutInflater.from(currentActivity);
        itemView = inflater.inflate(R.layout.lecture_time_slot, rootView, false);
    }

    @Override
    public void setElement(int index, CharSequence content) {
        switch (index) {
        case 0:
            TextView weekDayTextView = (TextView) itemView
                    .findViewById(R.id.lectureWeekDayTextView);
            weekDayTextView.setText(content);
        case 1:
            TextView durationTextView = (TextView) itemView
                    .findViewById(R.id.lectureDurationTextView);
            durationTextView.setText(content);
        }
    }

    @Override
    public View getElement(int index) {
        switch (index) {
        case 0:
            return itemView.findViewById(R.id.lectureWeekDayTextView);
        case 1:
            return itemView.findViewById(R.id.lectureDurationTextView);
        default:
            return null;
        }
    }

}
