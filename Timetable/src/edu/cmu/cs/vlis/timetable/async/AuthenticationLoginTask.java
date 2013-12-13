package edu.cmu.cs.vlis.timetable.async;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.LoginActivity;
import edu.cmu.cs.vlis.timetable.NavigationActivity;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class AuthenticationLoginTask extends LoadingDialogAsyncTask<String, Void, NetworkStatus> {
    private View buttonView;

    public AuthenticationLoginTask(LoginActivity currentActivity, View buttonView) {
        super(currentActivity);
        this.buttonView = buttonView;
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Validating";
    }

    @Override
    protected NetworkStatus doInBackground(String... loginInfo) {
        String email = loginInfo[0];
        String password = loginInfo[1];

        // set default status as "NETWORK_ERROR", so if everything goes smoothly then the
        // statusCode will be modified during the communication with server, otherwise the
        // statusCode will remain as NETWORK_ERROR
        return ((LoginActivity) getActivity()).loginUser(email, password);
    }

    @Override
    protected void postExecute(NetworkStatus status) {
        if (status == NetworkStatus.VALID) {
            Intent intent = new Intent(getActivity(), NavigationActivity.class);
            getActivity().startActivity(intent);
        }
        else if (status == NetworkStatus.BAD_REQUEST) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Username and password do not match...", Toast.LENGTH_LONG).show();
        }
        // If invalid, then show a toast indicating the error type, it could be wrong email
        // password or network error
        else {
            Log.d("KOBE", status.name());
            Toast.makeText(getActivity().getApplicationContext(), "Network or server error...",
                    Toast.LENGTH_LONG).show();
        }
        this.buttonView.setEnabled(true);
    }
}
