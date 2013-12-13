package edu.cmu.cs.vlis.timetable.async;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;
import edu.cmu.cs.vlis.timetable.fragment.HomeTabFragment;
import edu.cmu.cs.vlis.timetable.obj.DataProvider;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.WeekSummary;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SyncWeekTabViewTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private HomeTabFragment currentFragment;
    private NetworkStatus status = NetworkStatus.NETWORK_ERROR;
    private WeekSummary weekSummary;

    public SyncWeekTabViewTask(HomeTabFragment currentFragment) {
        super(currentFragment.getActivity(), currentFragment.getView());
        this.currentFragment = currentFragment;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Void doInBackground(Void... loginInfo) {
        DataProvider dataProvider = new JsonDataProvider(getActivity());
        String timeStamp = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date());

        weekSummary = dataProvider.readObject(APIAddr.GET_WEEK_SUMMARY + "?datetime=" + timeStamp,
                null, WeekSummary.class);
        status = dataProvider.getLastNetworkStatus();

        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, getActivity().getApplicationContext());
            return;
        }

        Log.d("cc", "week post");
        currentFragment.setWeekSummaryView(weekSummary);
    }
}
