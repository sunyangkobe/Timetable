package edu.cmu.cs.vlis.timetable.fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.AddCourseActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.GetDateAsyncTask;
import edu.cmu.cs.vlis.timetable.async.LoadEnrolledCoursesAsyncTask;
import edu.cmu.cs.vlis.timetable.draw.ItemDrawer;
import edu.cmu.cs.vlis.timetable.draw.MultiColorLinearLayoutAdapter;
import edu.cmu.cs.vlis.timetable.draw.SimpleItemDrawer;
import edu.cmu.cs.vlis.timetable.draw.SingleColorLinearLayoutAdapter;
import edu.cmu.cs.vlis.timetable.fragment.PlannerDatePickerFragment.DatePickerType;
import edu.cmu.cs.vlis.timetable.listener.CourseItemClickListener;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class PlannerFragment extends Fragment implements OnClickListener {
    private SimpleItemDrawer startDateDrawer;
    private SimpleItemDrawer endDateDrawer;
    private Button addCourseButton;
    private HashMap<Integer, Course> enrolledCourses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        enrolledCourses = new HashMap<Integer, Course>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("PLANNER");
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout infoContainer = (LinearLayout) view.findViewById(R.id.semester_info_container);
        SingleColorLinearLayoutAdapter colorLinearLayoutAdapter = new SingleColorLinearLayoutAdapter(
                getActivity(), infoContainer, R.color.more_light_gray);

        startDateDrawer = new SimpleItemDrawer(getActivity(), infoContainer);
        startDateDrawer.setElement(0, "Start date: ");

        endDateDrawer = new SimpleItemDrawer(getActivity(), infoContainer);
        endDateDrawer.setElement(0, "End date: ");

        ((TextView) startDateDrawer.getElement(1)).setTextColor(getResources().getColor(
                R.color.blue));
        ((TextView) endDateDrawer.getElement(1))
                .setTextColor(getResources().getColor(R.color.blue));

        colorLinearLayoutAdapter.addView(startDateDrawer.getView());
        colorLinearLayoutAdapter.addView(endDateDrawer.getView());

        addCourseButton = (Button) view.findViewById(R.id.add_course_btn);
        addCourseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                intent.putExtra("enrolledCourses", enrolledCourses);
                getActivity().startActivity(intent);
            }
        });
        new GetDateAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        PlannerDatePickerFragment newFragment = new PlannerDatePickerFragment();
        if (v == startDateDrawer.getElement(1)) {
            Log.d("KOBE", "start clicked");
            newFragment.setTargetView((TextView) startDateDrawer.getElement(1));
            newFragment.setDatePickerType(DatePickerType.START_DATE_PICKER);
        }
        else if (v == endDateDrawer.getElement(1)) {
            Log.d("KOBE", "end clicked");
            newFragment.setTargetView((TextView) endDateDrawer.getElement(1));
            newFragment.setDatePickerType(DatePickerType.END_DATE_PICKER);
        }
        newFragment.setParentFragment(this);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadEnrolledCoursesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void draw(List<Course> courses) {
        // the fragment has been detached from the activity
        if (courses == null || getActivity() == null) return;
        LinearLayout rootView = (LinearLayout) getActivity().findViewById(R.id.course_container);
        rootView.removeAllViews();
        enrolledCourses.clear();

        MultiColorLinearLayoutAdapter colorLinearLayoutAdapter = new MultiColorLinearLayoutAdapter(
                getActivity(), rootView);
        ItemDrawer itemDrawer;
        for (Course course : courses) {
            itemDrawer = new SimpleItemDrawer(getActivity(), rootView);

            itemDrawer.setElement(0, course.toString());
            itemDrawer.setElement(1, ">");
            itemDrawer.setOnClickListener(new CourseItemClickListener(getActivity(),
                    course.getId(), course.getCourse_code()));

            colorLinearLayoutAdapter.addView(itemDrawer.getView());
            enrolledCourses.put(course.getId(), course);
        }

        setAddCourseButtonVisible();
    }

    public void updateDatePicker(String startDate, String endDate) {
        if (getActivity() == null) return;
        
        startDateDrawer.getElement(1).setOnClickListener(this);
        endDateDrawer.getElement(1).setOnClickListener(this);

        if (startDate != null) {
            Calendar cal = Calendar.getInstance();
            if (!startDate.equals("1970-01-01")) {
                String[] fields = startDate.split("-");
                cal.set(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]) - 1,
                        Integer.parseInt(fields[2]));
            }
            ((TextView) startDateDrawer.getElement(1)).setText(Utils.getFormattedDate(cal));
        }

        if (endDate != null) {
            Calendar cal = Calendar.getInstance();
            if (!endDate.equals("1970-01-01")) {
                String[] fields = endDate.split("-");
                cal.set(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]) - 1,
                        Integer.parseInt(fields[2]));
            }
            ((TextView) endDateDrawer.getElement(1)).setText(Utils.getFormattedDate(cal));
        }
    }

    public void setAddCourseButtonGone() {
        addCourseButton.setVisibility(View.GONE);
    }

    public void setAddCourseButtonVisible() {
        addCourseButton.setVisibility(View.VISIBLE);
    }
}
