package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class QuitCourseAsyncTask extends LoadingDialogAsyncTask<Integer, Void, NetworkStatus> {

    public QuitCourseAsyncTask(Activity activity) {
        super(activity);
    }

    @Override
    protected NetworkStatus doInBackground(Integer... params) {
        int courseId = params[0];
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("id", courseId + "");
        dataProvider.readJsonNode(APIAddr.QUIT_ENROLLED_COURSE, requestParams);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus status) {
        if (status != NetworkStatus.VALID) {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
        else {
            getActivity().finish();
        }
    }
}
