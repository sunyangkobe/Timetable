package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseColorLinearLayoutAdapter {
    protected ViewGroup rootView;
    protected Activity currentActivity;
    
    public BaseColorLinearLayoutAdapter(Activity currentActivity, ViewGroup rootView) {
        this.currentActivity = currentActivity;
        this.rootView = rootView;
    }
    
    public abstract void addView(View view);
}
