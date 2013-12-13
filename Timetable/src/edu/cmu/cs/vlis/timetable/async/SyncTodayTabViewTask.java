package edu.cmu.cs.vlis.timetable.async;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.fragment.HomeTabFragment;
import edu.cmu.cs.vlis.timetable.obj.DataProvider;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SyncTodayTabViewTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private HomeTabFragment currentFragment;
    private NetworkStatus status = NetworkStatus.NETWORK_ERROR;
    private Task[] tasks;
    private Lecture[] lectures;

    public SyncTodayTabViewTask(HomeTabFragment currentFragment) {
        super(currentFragment.getActivity(), currentFragment.getView());
        this.currentFragment = currentFragment;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Void doInBackground(Void... loginInfo) {
        DataProvider dataProvider = new JsonDataProvider(currentFragment.getActivity());

        String timeStamp = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date());
        tasks = dataProvider.readObject(APIAddr.GET_TASK + "?timestamp=" + timeStamp
                + "&task_today=True", null, Task[].class);
        status = dataProvider.getLastNetworkStatus();

        lectures = dataProvider.readObject(APIAddr.GET_LECTURE + "?timestamp=" + timeStamp, null,
                Lecture[].class);
        status = dataProvider.getLastNetworkStatus();

        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentFragment.getActivity().getApplicationContext());
            return;
        }

        if (tasks.length == 0 && lectures.length == 0) {
            ((TextView) currentFragment.getActivity().findViewById(R.id.remainingTaskCountTextView))
                    .setText("You have no tasks or lectures today");
        }
        else {
            if (tasks.length == 0) {
                ((TextView) currentFragment.getActivity().findViewById(R.id.remainingTaskCountTextView))
                        .setVisibility(View.GONE);
            }
            else {
                currentFragment.setTaskViewTopic(tasks);
                currentFragment.setTaskViewContent(tasks);
            }

            if (lectures != null && lectures.length != 0) {
                currentFragment.setLectureViewTopic(lectures);
                currentFragment.setLectureViewContent(lectures);
            }
        }

    }
}
