package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import edu.cmu.cs.vlis.timetable.R;

public class MultiColorLinearLayoutAdapter extends BaseColorLinearLayoutAdapter {
    private int currentColorIndex;
    private int[] colors;
            
    public MultiColorLinearLayoutAdapter(Activity currentActivity, ViewGroup rootView) {
        super(currentActivity, rootView);
        this.currentColorIndex = 0;
        this.colors = currentActivity.getResources().getIntArray(R.array.task_lecture_color);
    }
    
    public void addView(View view) {
        view.setBackgroundColor(colors[(currentColorIndex ++) % colors.length]);
        rootView.addView(view);
    }
}
