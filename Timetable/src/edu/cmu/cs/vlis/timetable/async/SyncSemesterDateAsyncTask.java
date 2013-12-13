package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.fragment.PlannerDatePickerFragment.DatePickerType;
import edu.cmu.cs.vlis.timetable.fragment.PlannerFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class SyncSemesterDateAsyncTask extends LoadingDialogAsyncTask<String, Void, NetworkStatus> {

    private DatePickerType type;
    private String targetDate;
    private Fragment fragment;

    public SyncSemesterDateAsyncTask(Fragment fragment, DatePickerType type) {
        super(fragment.getActivity());
        this.type = type;
        this.fragment = fragment;
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Syncing...";
    }

    @Override
    protected NetworkStatus doInBackground(String... params) {
        targetDate = params[0];
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put(type.getName(), targetDate);
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        dataProvider.readJsonNode(APIAddr.SYNC_SEMESTER_DATE, requestParams);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus result) {
        if (result != NetworkStatus.VALID) {
            Toast.makeText(getActivity(), "Network or server error....", Toast.LENGTH_LONG).show();
            return;
        }
        if (type == DatePickerType.START_DATE_PICKER) {
            ((PlannerFragment) fragment).updateDatePicker( targetDate.replace(":", "-"), null);
        }
        else {
            ((PlannerFragment) fragment).updateDatePicker(null, targetDate.replace(":", "-"));
        }
    }
}
