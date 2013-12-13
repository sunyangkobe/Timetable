package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.fragment.GenericComposeFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class UpdateStatusAsyncTask extends GenericComposeFragment.ComposeCallback {
    private Activity currentActivity;
    private String statusContent;
    private NetworkStatus status = NetworkStatus.NETWORK_ERROR;

    public UpdateStatusAsyncTask(Activity currentActivity) {
        super(currentActivity);
        this.currentActivity = currentActivity;
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Updating..." + currentActivity.getClass();
    }

    @Override
    protected Void doInBackground(Void... params) {
        JsonDataProvider jsonDataProvider = new JsonDataProvider(currentActivity);
        Map<String, String> httpParams = new HashMap<String, String>();
        httpParams.put("status", statusContent);
        jsonDataProvider.readJsonNode(APIAddr.POST_STATUS, httpParams);
        status = jsonDataProvider.getLastNetworkStatus();
        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, getActivity().getApplicationContext());
            return;
        }
      
        TextView statusTextView = (TextView) currentActivity.findViewById(R.id.profile_user_status);
        statusTextView.setText(statusContent);
    }

	@Override
	public void setContent(String content) {
		this.statusContent = content;
	}
}