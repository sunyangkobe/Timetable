package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.fragment.ProfileFragment;

public class FriendsItemClickListener implements View.OnClickListener {
    private Activity currentActivity;
    private int userId;

    public FriendsItemClickListener(Activity currentActivity, int userId) {
        this.currentActivity = currentActivity;
        this.userId = userId;
    }

    @Override
    public void onClick(View arg0) {
        currentActivity.getIntent().putExtra("user_id", userId);
        FragmentManager fragmentManager = ((FragmentActivity) currentActivity).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentFrame, new ProfileFragment(), "ProfileFragment").commit();
    }
}
