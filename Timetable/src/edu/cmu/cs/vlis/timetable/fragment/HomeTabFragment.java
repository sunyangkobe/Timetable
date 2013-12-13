package edu.cmu.cs.vlis.timetable.fragment;

import java.util.Date;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.SyncTodayTabViewTask;
import edu.cmu.cs.vlis.timetable.async.SyncWeekTabViewTask;
import edu.cmu.cs.vlis.timetable.draw.DetailedItemDrawer;
import edu.cmu.cs.vlis.timetable.draw.ItemDrawer;
import edu.cmu.cs.vlis.timetable.draw.ItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.LectureItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.LectureItemsContentFiller.LECTURE_MODE;
import edu.cmu.cs.vlis.timetable.draw.MultiColorLinearLayoutAdapter;
import edu.cmu.cs.vlis.timetable.draw.TaskItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.TaskItemsContentFiller.TASK_MODE;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.obj.WeekSummary;
import edu.cmu.cs.vlis.timetable.obj.WeekSummary.DaySummary;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class HomeTabFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    private int position;

    private SyncWeekTabViewTask syncWeekTabViewFromServer = null;
    private SyncTodayTabViewTask syncTodayTabViewFromServer = null;

    public static HomeTabFragment newInstance(int position) {
        HomeTabFragment homeTabFragment = new HomeTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        homeTabFragment.setArguments(bundle);
        // Log.d("cc", "new instance");
        return homeTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        switch (position) {
        case 0:
            // if it's "week" tab view
            view = inflater.inflate(R.layout.fragment_home_week, container, false);
            break;
        // if it's "today" tab view
        case 1:
            view = inflater.inflate(R.layout.fragment_home_today, container, false);
            break;
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        switch (position) {
        case 0:
            syncWeekTabViewFromServer = new SyncWeekTabViewTask(this);
            syncWeekTabViewFromServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            break;
        case 1:
            syncTodayTabViewFromServer = new SyncTodayTabViewTask(this);
            syncTodayTabViewFromServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            TextView currentDateTextView = (TextView) view.findViewById(R.id.currentDateTextView);
            currentDateTextView.setText(Utils.getFormattedCurrentDateForHomeTabToday());

            TextView currentTemperatureTextView = (TextView) view
                    .findViewById(R.id.currentTemperatureTextView);
            currentTemperatureTextView.setText("16 C");
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (syncWeekTabViewFromServer != null
                && syncWeekTabViewFromServer.getStatus() == Status.RUNNING) {
            syncWeekTabViewFromServer.cancel(true);
        }
        if (syncTodayTabViewFromServer != null
                && syncTodayTabViewFromServer.getStatus() == Status.RUNNING) {
            syncTodayTabViewFromServer.cancel(true);
        }
    }

    public void setTaskViewTopic(Task[] tasks) {
        if (getActivity() == null) {
            return;
        }
        
        TextView remainingTaskCountTextView = (TextView) getActivity().findViewById(
                R.id.remainingTaskCountTextView);

        String remainingTaskCountText = "Your " + String.valueOf(tasks.length) + " tasks today:";
        remainingTaskCountTextView.setText(remainingTaskCountText);
    }

    public void setTaskViewContent(Task[] tasks) {
        if (getActivity() == null) {
            return;
        }
        // set the text to display in remainingTaskCountTextView according to
        // the taskList fetched from server
        LinearLayout rootView = (LinearLayout) getActivity().findViewById(R.id.tasksLinearLayout);

        // TODO: need to get a course instance or at least the course section and replace null with
        // it
        ItemsContentFiller taskItemsContentFiller = new TaskItemsContentFiller(getActivity(),
                rootView, tasks, TASK_MODE.SIMPLE_TIME_MODE, null);
        taskItemsContentFiller.fillContent();
    }

    public void setLectureViewTopic(Lecture[] lectures) {
        if (getActivity() == null) {
            return;
        }
        
        TextView nextLectureTimeTextView = (TextView) getActivity().findViewById(
                R.id.nextLectureTimeTextView);

        String timeRemainingForNextLecture = Utils.getTimeDiffBetweenDates(new Date(),
                lectures[0].getStart_time());

        String nextLectureTimeText = "Your next class is " + lectures[0].getCourse_code() + ", in "
                + timeRemainingForNextLecture;
        nextLectureTimeTextView.setText(nextLectureTimeText);
    }

    public void setLectureViewContent(Lecture[] lectures) {
        if (getActivity() == null) {
            return;
        }
        
        LinearLayout rootView = (LinearLayout) getActivity()
                .findViewById(R.id.lecturesLinearLayout);
        ItemsContentFiller lectureItemsContentFiller = new LectureItemsContentFiller(getActivity(),
                rootView, lectures, null, LECTURE_MODE.SEPERATE_MODE);

        lectureItemsContentFiller.fillContent();
    }

    public void setWeekSummaryView(WeekSummary weekSummary) {
        if (getActivity() == null) {
            return;
        }
        
        TextView weekCurrentDateTextView = (TextView) getView()
                .findViewById(R.id.weekCurrentDateTextView);
        weekCurrentDateTextView.setText(Utils.getFormattedCurrentDateForHomeTabToday());
        
        TextView weekSummaryTextView = (TextView) getView().findViewById(
                R.id.weekSummaryTextView);
        weekSummaryTextView.setText("You still have " + weekSummary.getRemaining_task_num()
                + " tasks and " + weekSummary.getRemaining_lecture_num() + " lectures this week.");

        LinearLayout rootView = (LinearLayout) getView().findViewById(
                R.id.weekDaySummaryLinearLayout);
        MultiColorLinearLayoutAdapter colorLinearLayoutAdapter = new MultiColorLinearLayoutAdapter(
                getActivity(), rootView);
        ItemDrawer itemDrawer;
        int curDay = 0;
        for (DaySummary daySummary : weekSummary.getDay_summaries()) {
            itemDrawer = new DetailedItemDrawer(getActivity(), rootView);

            itemDrawer.setElement(0, daySummary.getDay());
            itemDrawer.setElement(2, daySummary.getTasks());
            itemDrawer.setElement(3, daySummary.getLectures());

            if (curDay++ < weekSummary.getPassed_day()) {
                itemDrawer.getView().setAlpha(0.2f);
            }

            colorLinearLayoutAdapter.addView(itemDrawer.getView());
        }

    }

}
