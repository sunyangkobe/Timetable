package edu.cmu.cs.vlis.timetable.persistentdatamanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class VersionManager {
    private Activity currentActivity;
    private SharedPreferences settings;
    private String currentVersion = null;

    private final String VERSION_NAME_FIELD = "version_name";

    public VersionManager(Activity activity) {
        currentActivity = activity;
        settings = currentActivity.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME,
                Context.MODE_PRIVATE);
        try {
            this.currentVersion = currentActivity.getPackageManager().getPackageInfo(
                    currentActivity.getPackageName(), 0).versionName;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getVersionName() {
        return settings.getString(VERSION_NAME_FIELD, null);
    }

    private void setVersionName(String versionName) {
        // set session id in shared preference
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(VERSION_NAME_FIELD, versionName);
        // commit the edits!
        editor.commit();
    }

    public boolean isUpdate() {
        String storedVersion = getVersionName();
        if (currentVersion != null
                && (storedVersion == null || !storedVersion.equals(currentVersion))) {
            return true;
        }
        return false;
    }

    public void updateVersion() {
        setVersionName(this.currentVersion);
    }
}
