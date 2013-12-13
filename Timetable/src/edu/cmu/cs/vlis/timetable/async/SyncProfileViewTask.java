package edu.cmu.cs.vlis.timetable.async;

import android.annotation.SuppressLint;
import edu.cmu.cs.vlis.timetable.fragment.ProfileFragment;
import edu.cmu.cs.vlis.timetable.obj.DataProvider;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.UserProfile;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SyncProfileViewTask extends LoadingBarAsyncTask<Void, Void, Void> {
	private ProfileFragment currentFragment;
	private NetworkStatus status = NetworkStatus.NETWORK_ERROR;
	private int userId;
	private UserProfile userProfile;

	public SyncProfileViewTask(ProfileFragment currentFragment) {
		super(currentFragment.getActivity(), currentFragment.getView());
		this.currentFragment = currentFragment;
		this.userId = currentFragment.getUserId();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Void doInBackground(Void... loginInfo) {
		DataProvider dataProvider = new JsonDataProvider(getActivity());

		if (userId != -1) {
			userProfile = dataProvider.readObject(APIAddr.GET_PROFILE + "?id=" + userId, null, UserProfile.class);
		} else {
			userProfile = dataProvider.readObject(APIAddr.GET_PROFILE, null, UserProfile.class);
		}
		status = dataProvider.getLastNetworkStatus();

		return null;
	}

	@Override
	protected void postExecute(Void result) {
		if (status != NetworkStatus.VALID) {
			if (status == NetworkStatus.NOT_FOUND) {
				Utils.displayErrorMessage("No such user...", getActivity().getApplicationContext());
			} else {
				Utils.displayNetworkErrorMessage(status, getActivity().getApplicationContext());
			}
			return;
		}

		currentFragment.setProfileView(userProfile);
	}
}
