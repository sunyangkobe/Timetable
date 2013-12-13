package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class EnrollCourseAsyncTask extends LoadingDialogAsyncTask<Course, Void, NetworkStatus> {

    public EnrollCourseAsyncTask(Activity activity) {
        super(activity);
    }

    @Override
    protected NetworkStatus doInBackground(Course... params) {
        Course course = params[0];

        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("id", course.getId() + "");

        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        dataProvider.readJsonNode(APIAddr.ENROLL_COURSE, requestBody);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus status) {
        if (status == NetworkStatus.VALID) {
            getActivity().finish();
            return;
        }
        else if (status == NetworkStatus.BAD_REQUEST) {
            Toast.makeText(getActivity(), "Course cannot be found...", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
    }
}
