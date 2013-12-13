package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.NavigationActivity;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Token;
import edu.cmu.cs.vlis.timetable.obj.User;
import edu.cmu.cs.vlis.timetable.persistentdatamanager.SessionManager;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class SignUpTask extends LoadingDialogAsyncTask<User, Void, NetworkStatus> {

    public SignUpTask(Activity activity) {
        super(activity);
    }

    @Override
    protected NetworkStatus doInBackground(User... params) {
        User user = params[0];
        // initialize the requestBody
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("username", user.getEmail());
        requestBody.put("password", user.getPassword());
        requestBody.put("first_name", user.getFname());
        requestBody.put("last_name", user.getLname());
        requestBody.put("university", user.getSchool());
        requestBody.put("gender", user.getGender());

        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        Token token = dataProvider.readObject(APIAddr.REGISTER, requestBody, Token.class);
        NetworkStatus statusCode = dataProvider.getLastNetworkStatus();

        if (statusCode == NetworkStatus.CREATED) {
            String sessionId = token.getToken();
            int userId = token.getId();
            // set the shared preference and session Id in memory
            SessionManager sessionManager = new SessionManager(getActivity());
            sessionManager.setSessionId(sessionId);
            sessionManager.setUserId(userId);
        }

        return statusCode;
    }

    @Override
    protected void postExecute(NetworkStatus result) {
        if (result == NetworkStatus.CREATED) {
            new GetSchoolCoursesAsyncTask(getActivity())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Intent intent = new Intent(getActivity(), NavigationActivity.class);
            getActivity().startActivity(intent);
        }
        else if (result == NetworkStatus.DUPLICATED) {
            Toast.makeText(getActivity(), "User already exists...", Toast.LENGTH_LONG).show();
        }
        else if (result == NetworkStatus.BAD_REQUEST) {
            Toast.makeText(getActivity(), "Some fields are invalid...", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
    }
}
