package edu.cmu.cs.vlis.timetable.listener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import edu.cmu.cs.vlis.timetable.NavigationActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.fragment.ChatFragment;
import edu.cmu.cs.vlis.timetable.fragment.FriendsFragment;
import edu.cmu.cs.vlis.timetable.fragment.HomeFragment;
import edu.cmu.cs.vlis.timetable.fragment.NotificationsFragment;
import edu.cmu.cs.vlis.timetable.fragment.PlannerFragment;
import edu.cmu.cs.vlis.timetable.fragment.ProfileFragment;
import edu.cmu.cs.vlis.timetable.fragment.SettingFragment;

public class NavigationDrawerItemClickListener implements ListView.OnItemClickListener {
    private NavigationActivity currentActivity;
    private DrawerLayout navigationDrawerLayout;
    private ListView navigationDrawerList;

    public NavigationDrawerItemClickListener(NavigationActivity currentActivity,
            DrawerLayout navigationDrawerLayout, ListView navigationDrawerList) {
        this.currentActivity = currentActivity;
        this.navigationDrawerLayout = navigationDrawerLayout;
        this.navigationDrawerList = navigationDrawerList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    public void selectItem(int position) {
        Fragment navigatedFragment;
        String tag = null;

        switch (position) {
        case 0:
            navigatedFragment = new HomeFragment();
            tag = "HomeFragment";
            break;
        case 1:
            navigatedFragment = new PlannerFragment();
            tag = "PlannerFragment";
            break;
        case 2:
            navigatedFragment = new NotificationsFragment();
            tag = "NotificationsFragment";
            break;
        case 3:
            navigatedFragment = new ChatFragment();
            tag = "ChatFragment";
            break;
        case 4:
            navigatedFragment = new FriendsFragment();
            tag = "FriendsFragment";
            break;
        case 5:
            navigatedFragment = new ProfileFragment();
            // If user user navigate to profile page using navigation drawer, be sure to show the
            // profile of himself not anyone else
            currentActivity.getIntent().putExtra("user_id", -1);
            tag = "ProfileFragment";
            break;
        case 6:
            navigatedFragment = new SettingFragment();
            tag = "SettingsFragment";
            break;
        default:
            navigatedFragment = new HomeFragment();
            tag = "OtherFragment";
            break;
        }

        // replace the FrameLayout with the selected fragment
        FragmentManager fragmentManager = currentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFrame, navigatedFragment, tag)
                .commit();

        navigationDrawerList.setItemChecked(position, true);

        // set the current position in NavigationActivity so it can modify the title of action bar
        // when drawer closes according to the current position
        currentActivity.setCurrentSelectedDrawerItemPosition(position);
        navigationDrawerLayout.closeDrawer(navigationDrawerList);
    }
}
