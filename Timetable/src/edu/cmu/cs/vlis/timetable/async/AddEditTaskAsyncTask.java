package edu.cmu.cs.vlis.timetable.async;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.AddEditTaskActivity.MODE;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class AddEditTaskAsyncTask extends LoadingDialogAsyncTask<Task, Void, NetworkStatus> {

    private MODE mode;

    public AddEditTaskAsyncTask(Activity activity, MODE mode) {
        super(activity);
        this.mode = mode;
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Saving...";
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected NetworkStatus doInBackground(Task... params) {
        Task task = params[0];
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        Map<String, String> requestParams = new HashMap<String, String>();
        if (mode == MODE.EDIT) {
            requestParams.put("id", task.getId() + "");
        }
        requestParams.put("type", task.getType());
        requestParams.put("name", task.getName());
        if (task.getNotes() != null && !task.getNotes().isEmpty()) {
            requestParams.put("notes", task.getNotes());
        }
        if (task.getLocation() != null && !task.getLocation().isEmpty()) {
            requestParams.put("location", task.getLocation());
        }
        requestParams.put("start_time",
                new SimpleDateFormat("HH:mm:ss").format(task.getStart_time()));
        requestParams.put("end_time", new SimpleDateFormat("HH:mm:ss").format(task.getEnd_time()));
        requestParams.put("date", new SimpleDateFormat("yyyy-MM-dd").format(task.getDate()));
        requestParams.put("course_id", task.getCourse_id() + "");
        Log.d("KOBE", requestParams.toString());

        dataProvider.readJsonNode(mode == MODE.EDIT ? APIAddr.EDIT_USER_TASK
                : APIAddr.CREATE_NEW_TASK, requestParams);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus result) {
        if (result != NetworkStatus.VALID) {
            Log.d("KOBE", result.name());
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
        else {
            getActivity().finish();
        }
    }
}
