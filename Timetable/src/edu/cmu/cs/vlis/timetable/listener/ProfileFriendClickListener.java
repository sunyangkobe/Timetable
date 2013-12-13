package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import edu.cmu.cs.vlis.timetable.async.SendFriendRequestAsyncTask;
import edu.cmu.cs.vlis.timetable.obj.UserProfile;

public class ProfileFriendClickListener implements View.OnClickListener {
	private Activity currentActivity;
	private UserProfile friendProfile;

	public ProfileFriendClickListener(Activity currentActivity, UserProfile friendProfile) {
		this.currentActivity = currentActivity;
		this.friendProfile = friendProfile;
	}

	@Override
	public void onClick(View view) {
		if (friendProfile.getFriend_status() == 0) {
			return;
		}

		int friendStatus = friendProfile.getFriend_status();
		if (friendStatus == 0 || friendStatus == 1 || friendStatus == 2) {
			return;
		}

		if (friendStatus == 3) {
			// Send friend request
			SendFriendRequestAsyncTask asyncTask = new SendFriendRequestAsyncTask(currentActivity, friendProfile);
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
}
