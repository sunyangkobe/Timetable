package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.model.GraphUser;
import com.fasterxml.jackson.databind.JsonNode;

import edu.cmu.cs.vlis.timetable.LoginActivity;
import edu.cmu.cs.vlis.timetable.NavigationActivity;
import edu.cmu.cs.vlis.timetable.RegisterActivity;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class FBUserLoginTask extends LoadingDialogAsyncTask<GraphUser, Void, Class<?>> {

    private GraphUser user;

    public FBUserLoginTask(Activity activity) {
        super(activity);
    }

    @Override
    protected Class<?> doInBackground(GraphUser... users) {
        this.user = users[0];
        String email = user.getProperty("email").toString();

        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        JsonNode responseNode = dataProvider.readJsonNode(APIAddr.CHECK_USER + email, null);
        NetworkStatus statusCode = dataProvider.getLastNetworkStatus();

        if (statusCode != NetworkStatus.VALID) {
            return null;
        }

        boolean userExists = responseNode.get("check_exist").asBoolean();
        if (!userExists) {
            return RegisterActivity.class;
        }
        else {
            if (((LoginActivity) getActivity()).loginUser(email, Utils.getFBUserMasterPWD()) != NetworkStatus.VALID) {
                return null;
            }
            else {
                return NavigationActivity.class;
            }
        }
    }

    @Override
    protected void postExecute(Class<?> result) {
        if (result == null) {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(getActivity(), result);
        if (user != null && result == RegisterActivity.class) {
            Utils.setUserData(intent, user);
        }
        getActivity().startActivity(intent);
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Validating...";
    }
}
