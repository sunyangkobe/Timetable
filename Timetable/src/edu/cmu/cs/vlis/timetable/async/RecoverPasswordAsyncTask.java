package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;

import android.app.Activity;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.ForgetPasswordActivity;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class RecoverPasswordAsyncTask extends LoadingDialogAsyncTask<String, Void, NetworkStatus> {

    public RecoverPasswordAsyncTask(Activity activity) {
        super(activity);
    }

    @Override
    protected NetworkStatus doInBackground(String... params) {
        String username = params[0];
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("username", username);
        dataProvider.readJsonNode(APIAddr.RECOVER_PASSWORD, requestBody);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus result) {
        if (result == NetworkStatus.NOT_FOUND) {
            Toast.makeText(getActivity(), "User cannot be found...", Toast.LENGTH_LONG).show();
        }
        else if (result != NetworkStatus.VALID) {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
        else {
            ((ForgetPasswordActivity) getActivity()).notifySuccess();
        }
    }
}
