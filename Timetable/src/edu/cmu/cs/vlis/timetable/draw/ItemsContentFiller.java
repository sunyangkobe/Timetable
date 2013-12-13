package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.ViewGroup;

public abstract class ItemsContentFiller {
    protected Activity currentActivity;
    protected ViewGroup rootView;
    
    public ItemsContentFiller(Activity curreActivity, ViewGroup rootView) {
        this.currentActivity = curreActivity;
        this.rootView = rootView;
    }
    
    public abstract void fillContent();
}
