package edu.cmu.cs.vlis.timetable.draw;

import android.view.View;

public abstract class ItemDrawer {
    protected View itemView;

    public abstract void setElement(int index, CharSequence content);

    public abstract View getElement(int index);

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    public void setBackgroundColor(int color) {
        itemView.setBackgroundColor(color);
    }

    public View getView() {
        return itemView;
    }
}
