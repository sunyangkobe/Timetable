package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Button;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.UserProfile;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SendFriendRequestAsyncTask extends AsyncTask<Boolean, Void, Void> {
    private Activity currentActivity;
    private UserProfile friendProfile;
    private NetworkStatus status;

    public SendFriendRequestAsyncTask(Activity currentActivity, UserProfile friendProfile) {
        this.currentActivity = currentActivity;
        this.friendProfile = friendProfile;
    }

    @Override
    protected Void doInBackground(Boolean... arg0) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("id", Integer.toString(friendProfile.getId()));
        JsonDataProvider jsonDataProvider = new JsonDataProvider(currentActivity);
        jsonDataProvider.readJsonNode(APIAddr.SEND_FRIEND_REQUEST, params);
        status = jsonDataProvider.getLastNetworkStatus();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentActivity.getApplicationContext());
        }

        // set status to friend request sent
        friendProfile.setFriend_status(2);

        Button friendOrEditBtn = (Button) currentActivity
                .findViewById(R.id.profile_friend_or_edit_button);
        friendOrEditBtn.setText("Request Sent");
    }
}
