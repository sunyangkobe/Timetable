package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class SingleColorLinearLayoutAdapter extends BaseColorLinearLayoutAdapter {
    private int color;
    private Context context;

    public SingleColorLinearLayoutAdapter(Activity currentActivity, ViewGroup rootView, int color) {
        super(currentActivity, rootView);
        this.context = currentActivity;
        this.color = color;
    }

    @Override
    public void addView(View view) {
        view.setBackgroundColor(context.getResources().getColor(color));
        rootView.addView(view);
    }

}
