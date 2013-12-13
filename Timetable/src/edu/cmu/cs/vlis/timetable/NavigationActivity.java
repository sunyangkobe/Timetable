package edu.cmu.cs.vlis.timetable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import edu.cmu.cs.vlis.timetable.adapter.DrawerItemArrayAdapter;
import edu.cmu.cs.vlis.timetable.async.FetchNotificationDaemonTask;
import edu.cmu.cs.vlis.timetable.fragment.HomeFragment;
import edu.cmu.cs.vlis.timetable.fragment.NotificationsFragment;
import edu.cmu.cs.vlis.timetable.fragment.ProfileFragment;
import edu.cmu.cs.vlis.timetable.fragment.SettingFragment;
import edu.cmu.cs.vlis.timetable.listener.ExitApplicationDialogListener;
import edu.cmu.cs.vlis.timetable.listener.HelpOverlayClickListner;
import edu.cmu.cs.vlis.timetable.listener.LogOutDialogListener;
import edu.cmu.cs.vlis.timetable.listener.NavigationDrawerItemClickListener;
import edu.cmu.cs.vlis.timetable.listener.OnSwipeTouchListener;
import edu.cmu.cs.vlis.timetable.persistentdatamanager.VersionManager;
import edu.cmu.cs.vlis.timetable.util.Constants;
import edu.cmu.cs.vlis.timetable.util.Constants.NavigationTargets;

public class NavigationActivity extends FragmentActivity {
    private DrawerLayout navigationDrawerLayout;
    private ListView navigationDrawerList;
    private ActionBarDrawerToggle navigationDrawerToggle;
    private int currentSelectedDrawerPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        navigationDrawerLayout = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        navigationDrawerLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                navigationDrawerLayout.openDrawer(navigationDrawerList);
            }
        });
        navigationDrawerList = (ListView) findViewById(R.id.leftDrawer);

        // set a custom shadow that overlays the main content when the drawer opens
        navigationDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set the adapter of DrawerList, defining the layout of each items in the ListView
        navigationDrawerList.setAdapter(DrawerItemArrayAdapter.getInstance(this));
        // set listener on drawer items
        navigationDrawerList.setOnItemClickListener(new NavigationDrawerItemClickListener(this,
                navigationDrawerLayout, navigationDrawerList));

        // enable ActionBar app icon to behave as action to toggle navigation drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        // now the title of action bar is aligned to left, if we want to customize its position,
        // then follow the annotated codes below
        // getActionBar().setIcon(android.R.color.transparent);
        // getActionBar().setDisplayShowCustomEnabled(true);
        // getActionBar().setCustomView(R.layout.abs_layout);

        // set the ActionBarDrawerToggle so we can tie together the functionality of DrawerLayout
        // and the framework ActionBar
        navigationDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            // When drawer is closed, reset the title of the action bar using the name of current
            // page
            @Override
            public void onDrawerClosed(View view) {
                getActionBar()
                        .setTitle(
                                DrawerItemArrayAdapter.getInstance(NavigationActivity.this)
                                        .getArray()[currentSelectedDrawerPosition].title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View view) {
                getActionBar().setTitle(R.string.navigation_text);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // tie the navigationDrawerLayout with the action bar
        navigationDrawerLayout.setDrawerListener(navigationDrawerToggle);
        // set the focusable as false so the activity will call the "onBackPressed" method when
        // pressing the back button
        navigationDrawerLayout.setFocusableInTouchMode(false);

        // show the requested fragment page and help overlay if necessary
        if (savedInstanceState == null) {
            NavigationTargets navigationTarget = (NavigationTargets) getIntent()
                    .getSerializableExtra("navigation_target");

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (navigationTarget == null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrame, new HomeFragment(), "HomeFragment").commit();
            }
            else {
                switch (navigationTarget) {
                case PROFILE:
                    currentSelectedDrawerPosition = 5;
                    fragmentManager.beginTransaction()
                            .replace(R.id.contentFrame, new ProfileFragment(), "ProfileFragment")
                            .commit();
                    break;
                case NOTIFICATIONS:
                    currentSelectedDrawerPosition = 2;
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.contentFrame, new NotificationsFragment(),
                                    "NotificationsFragment").commit();
                    break;
                default:
                    fragmentManager.beginTransaction()
                            .replace(R.id.contentFrame, new HomeFragment(), "HomeFragment")
                            .commit();
                }
            }

            // if it's the first time user launches app or the app gets updates, show the help
            // overlay dialog
            VersionManager versionManager = new VersionManager(this);
            if (versionManager.isUpdate()) {
                versionManager.updateVersion();
                showHelpOverLay();
            }
            // showHelpOverLay();

            // start the fetching notification daemon thread if necessary
            FetchNotificationDaemonTask fetchNotificationDaemonTask = FetchNotificationDaemonTask
                    .getInstance(this);
            if (!fetchNotificationDaemonTask.isRunning()) {
                // load settings in shared preference and start the daemon task
                SharedPreferences sharedPreferences = getSharedPreferences(
                        Constants.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
                SettingFragment.daemonNotificationThreadEnable(this, fetchNotificationDaemonTask,
                        sharedPreferences);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide the items on action bar
        boolean drawerOpen = navigationDrawerLayout.isDrawerOpen(navigationDrawerList);
        // menu.findItem(R.id.action_addtask).setVisible(!drawerOpen);
        menu.setGroupVisible(0, !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (navigationDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and
     * onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navigationDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        navigationDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Customize the reaction when back button is pressed: if the drawer is not open, then
    // open the navigation drawer; if it's already opened, then pop an alert dialog to confirm
    // the exit intention. The default reaction for pressing back button in navigation drawer is
    // to finish the navigation activity directly.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
            navigationDrawerLayout.openDrawer(navigationDrawerList);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (navigationDrawerLayout.isDrawerOpen(navigationDrawerList)) {
                // the custom ExitApplicationDialogListener listener class is responsible for
                // exiting the application directly
                new AlertDialog.Builder(this).setMessage("Exit the Timetable?")
                        .setPositiveButton("Yes", new ExitApplicationDialogListener(this))
                        .setNegativeButton("No", null).show();
            }
        }
        return false;
    }

    // since we declare the listener for navigationDrawerList in an outer class, we must provide
    // an method for that class to modify the position of current selected drawer item.
    public void setCurrentSelectedDrawerItemPosition(int selectedItemPosition) {
        this.currentSelectedDrawerPosition = selectedItemPosition;
    }

    // create the help overlay dialog
    private void showHelpOverLay() {
        Dialog overlayDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        overlayDialog.setContentView(R.layout.help_overlay_view);

        FrameLayout helpOverlayLayout = (FrameLayout) overlayDialog
                .findViewById(R.id.helpOverlayLayout);
        helpOverlayLayout.setOnClickListener(new HelpOverlayClickListner(this, overlayDialog));

        overlayDialog.show();

    }

    public void logout(View v) {
        new AlertDialog.Builder(this).setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new LogOutDialogListener(this))
                .setNegativeButton("No", null).show();
    }
}
