package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import edu.cmu.cs.vlis.timetable.async.UpdateStatusAsyncTask;
import edu.cmu.cs.vlis.timetable.fragment.GenericComposeFragment;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class ProfileEditClickListener implements View.OnClickListener {
	final Activity activity;

	public ProfileEditClickListener(FragmentActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View view) {
		GenericComposeFragment composeFragment = new GenericComposeFragment(new UpdateStatusAsyncTask(activity),
				"Write your status", "Update!", Constants.STATUS_MAX_CHARACTER_NUMBER);
		composeFragment.show(activity.getFragmentManager(), "update_status");
	}
}
