package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.UserProfile;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class UpdateProfileAsyncTask extends
        LoadingDialogAsyncTask<UserProfile, Void, NetworkStatus> {

    private EditText oldPwdField;
    private EditText newPwdField;

    public UpdateProfileAsyncTask(Activity activity, EditText oldPwdField, EditText newPwdField) {
        super(activity);
        this.oldPwdField = oldPwdField;
        this.newPwdField = newPwdField;
    }

    @Override
    protected NetworkStatus doInBackground(UserProfile... params) {
        JsonDataProvider jsonDataProvider = new JsonDataProvider(getActivity());
        if (oldPwdField != null && newPwdField != null) {
            // update password first
            Map<String, String> httpParams = new HashMap<String, String>();
            httpParams.put("old_pwd", oldPwdField.getText().toString().trim());
            httpParams.put("new_pwd", newPwdField.getText().toString().trim());
            jsonDataProvider.readJsonNode(APIAddr.CHANGE_PASSWORD, httpParams);
        }

        if (jsonDataProvider.getLastNetworkStatus() != NetworkStatus.VALID) {
            return jsonDataProvider.getLastNetworkStatus();
        }

        // then update profile
        UserProfile userProfile = params[0];
        Map<String, String> httpParams = new HashMap<String, String>();
        httpParams.put("first_name", userProfile.getFirst_name());
        httpParams.put("last_name", userProfile.getLast_name());
        httpParams.put("university", userProfile.getUniversity());
        httpParams.put("avatar", userProfile.getAvatar());
        jsonDataProvider.readJsonNode(APIAddr.EDIT_PROFILE, httpParams);
        return jsonDataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus result) {

        Log.d("KOBE", result.name());
        if (result == NetworkStatus.UNAUTHORIZED) {
            Toast.makeText(getActivity(), "The old password is invalid", Toast.LENGTH_LONG).show();
        }
        else if (result != NetworkStatus.VALID) {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Profile is updated", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

}
