package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import edu.cmu.cs.vlis.timetable.NavigationActivity;
import edu.cmu.cs.vlis.timetable.util.Constants.NavigationTargets;

public class PosterNameImageClickListener implements View.OnClickListener {
    private Activity currentActivity;
    private int userId;
    
    public PosterNameImageClickListener(Activity currentActivity, int userId) {
        this.currentActivity = currentActivity;
        this.userId = userId;
    }
    
    @Override 
    public void onClick(View view) {
        Intent intent = new Intent(currentActivity, NavigationActivity.class);
        
        intent.putExtra("navigation_target", NavigationTargets.PROFILE);
        intent.putExtra("user_id", userId);
        
        currentActivity.startActivity(intent);
    }
}
