package edu.cmu.cs.vlis.timetable.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.FetchNotificationDaemonTask;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class SettingFragment extends PreferenceListFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(Constants.SHARED_PREFERENCE_FILE_NAME);
        preferenceManager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.layout.fragment_settings);
        preferenceManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreference = getPreferenceManager().getSharedPreferences();
        triggerNotificationSettings(sharedPreference.getBoolean("requireBackgroundCheck", false));
        setIntervalSummary(sharedPreference);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("KOBE", key);
        FetchNotificationDaemonTask fetchNotificationDaemonTask = FetchNotificationDaemonTask
                .getInstance(getActivity());
        if (key.equals("requireBackgroundCheck")) {
            boolean isEnabled = sharedPreferences.getBoolean("requireBackgroundCheck", false);
            triggerNotificationSettings(isEnabled);
            daemonNotificationThreadEnable(getActivity(), fetchNotificationDaemonTask,
                    sharedPreferences);
        }
        else if (key.equals("checkInterval")) {
            setIntervalSummary(sharedPreferences);
            setDaemonNotificationThreadInterval(fetchNotificationDaemonTask, sharedPreferences);
        }
        else if (key.equals("soundEnabled")) {
            setDaemonNotificationThreadSound(fetchNotificationDaemonTask, sharedPreferences);
        }
        else if (key.equals("vibrateEnabled")) {
            setsetDaemonNotificationThreadVibrate(fetchNotificationDaemonTask, sharedPreferences);
        }
    }

    private void triggerNotificationSettings(boolean shouldTrigger) {
        getPreferenceScreen().findPreference("checkInterval").setEnabled(shouldTrigger);
        getPreferenceScreen().findPreference("soundEnabled").setEnabled(shouldTrigger);
        getPreferenceScreen().findPreference("vibrateEnabled").setEnabled(shouldTrigger);
    }

    public static void daemonNotificationThreadEnable(Activity currentActivity,
            FetchNotificationDaemonTask fetchNotificationDaemonTask,
            SharedPreferences sharedPreferences) {
        
        boolean isEnabled = sharedPreferences.getBoolean("requireBackgroundCheck", false);
        if (isEnabled) {
            // stop anyway if the thread is running
            if (fetchNotificationDaemonTask.isRunning()) {
                fetchNotificationDaemonTask.stop();
            }

            setDaemonNotificationThreadInterval(fetchNotificationDaemonTask, sharedPreferences);
            Log.d("cc", "interval_Set");
            setDaemonNotificationThreadSound(fetchNotificationDaemonTask, sharedPreferences);
            setsetDaemonNotificationThreadVibrate(fetchNotificationDaemonTask, sharedPreferences);

            fetchNotificationDaemonTask.run();
        }
        else {
            fetchNotificationDaemonTask.stop();
        }
    }

    private void setIntervalSummary(SharedPreferences sharedPreferences) {
        ListPreference intervalPref = (ListPreference) getPreferenceScreen().findPreference(
                "checkInterval");
        String selected = sharedPreferences.getString("checkInterval", null);
        int index = intervalPref.findIndexOfValue(selected);
        intervalPref.setSummary("Current: " + intervalPref.getEntries()[index]);
    }

    public static void setDaemonNotificationThreadInterval(
            FetchNotificationDaemonTask fetchNotificationDaemonTask,
            SharedPreferences sharedPreferences) {

        int checkInterval = Integer.parseInt(sharedPreferences.getString("checkInterval", "60")) * 1000;
        fetchNotificationDaemonTask.setCheckInterval(checkInterval);
        // restart the daemon thread because if the previous interval setting is 60 minute, and user
        // sets it to 1 minute, then the setting may take at most 60 minutes to be effective without
        // restarting the thread
        if (fetchNotificationDaemonTask.isRunning()) {
            fetchNotificationDaemonTask.stop();
            fetchNotificationDaemonTask.run();
        }
    }

    public static void setDaemonNotificationThreadSound(
            FetchNotificationDaemonTask fetchNotificationDaemonTask,
            SharedPreferences sharedPreferences) {

        boolean soundEnabled = sharedPreferences.getBoolean("soundEnabled", true);
        fetchNotificationDaemonTask.setSoundEnabled(soundEnabled);
    }

    public static void setsetDaemonNotificationThreadVibrate(
            FetchNotificationDaemonTask fetchNotificationDaemonTask,
            SharedPreferences sharedPreferences) {

        boolean vibrateEnabled = sharedPreferences.getBoolean("vibrateEnabled", true);
        fetchNotificationDaemonTask.setVibrateEnabled(vibrateEnabled);
    }

}
