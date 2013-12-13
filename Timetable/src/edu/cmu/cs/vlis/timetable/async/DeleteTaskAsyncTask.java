package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class DeleteTaskAsyncTask extends LoadingDialogAsyncTask<Task, Void, NetworkStatus> {

    public DeleteTaskAsyncTask(Activity activity) {
        super(activity);
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Deleting...";
    }

    @Override
    protected NetworkStatus doInBackground(Task... params) {
        Task task = params[0];
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("id", task.getId() + "");
        dataProvider.readJsonNode(APIAddr.DELETE_USER_TASK, requestParams);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus result) {
        if (result != NetworkStatus.VALID) {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
            return;
        }
        getActivity().finish();
    }
}
