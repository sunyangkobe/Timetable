package edu.cmu.cs.vlis.timetable.async;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import edu.cmu.cs.vlis.timetable.obj.CourseList;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class GetSchoolCoursesAsyncTask extends AsyncTask<Void, Void, Void> {
    private Activity curActivity;

    public GetSchoolCoursesAsyncTask(Activity activity) {
        this.curActivity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("KOBE", "Load school courses");
        JsonDataProvider dataProvider = new JsonDataProvider(curActivity);
        JsonNode responseNode = dataProvider.readJsonNode(APIAddr.GET_SCHOOL_COURSES, null);
        NetworkStatus statusCode = dataProvider.getLastNetworkStatus();

        if (statusCode == NetworkStatus.VALID) {
            try {
                CourseList.getInstance().loadData(responseNode);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
