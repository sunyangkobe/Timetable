package edu.cmu.cs.vlis.timetable.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;

public class SimpleItemDrawer extends ItemDrawer {

    public SimpleItemDrawer(Context context, ViewGroup rootView) {
        LayoutInflater inflater = LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.two_slot_item, rootView, false);
    }

    @Override
    public void setElement(int index, CharSequence content) {
        switch (index) {
        case 0:
            TextView leftSlotTextView = (TextView) itemView.findViewById(R.id.leftSlotTextView);
            leftSlotTextView.setText(content);
            break;
        case 1:
            TextView rightSlotTextView = (TextView) itemView.findViewById(R.id.rightSlotTextView);
            rightSlotTextView.setText(content);
            break;
        default:
            break;
        }
    }

    @Override
    public View getElement(int index) {
        switch (index) {
        case 0:
            return itemView.findViewById(R.id.leftSlotTextView);
        case 1:
            return itemView.findViewById(R.id.rightSlotTextView);
        default:
            return null;
        }
    }
}