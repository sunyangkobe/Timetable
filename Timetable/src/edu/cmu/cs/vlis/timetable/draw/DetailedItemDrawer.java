package edu.cmu.cs.vlis.timetable.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;

public class DetailedItemDrawer extends ItemDrawer {

    public DetailedItemDrawer(Context context, ViewGroup rootView) {
        LayoutInflater inflater = LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.four_slot_item, rootView, false);
    }

    @Override
    public void setElement(int index, CharSequence content) {
        switch (index) {
        case 0: // top left slot
            TextView topLeftSlotTextView = (TextView) itemView
                    .findViewById(R.id.topLeftSlotTextView);
            topLeftSlotTextView.setText(content);
            break;
        case 1: // bottom left slot
            TextView bottomLeftSlotTextView = (TextView) itemView
                    .findViewById(R.id.bottomLeftSlotTextView);
            bottomLeftSlotTextView.setText(content);
            break;
        case 2:
            TextView topRightSlotTextView = (TextView) itemView
                    .findViewById(R.id.topRightSlotTextView);
            topRightSlotTextView.setText(content);
            break;
        case 3:
            TextView bottomRightSlotTextView = (TextView) itemView
                    .findViewById(R.id.bottomRightSlotTextView);
            bottomRightSlotTextView.setText(content);
            break;
        default:
            break;
        }
    }

    @Override
    public View getElement(int index) {
        switch (index) {
        case 0: // top left slot
            return itemView.findViewById(R.id.topLeftSlotTextView);
        case 1: // bottom left slot
            return itemView.findViewById(R.id.bottomLeftSlotTextView);
        case 2:
            return itemView.findViewById(R.id.topRightSlotTextView);
        case 3:
            return itemView.findViewById(R.id.bottomRightSlotTextView);
        default:
            return null;
        }
    }

}
