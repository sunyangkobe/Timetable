package edu.cmu.cs.vlis.timetable.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.obj.DrawerItem;

/**
 * Adapter that responsible for setting the title and counter on each drawer item.
 * 
 * @author Chun Chen
 * 
 */
public final class DrawerItemArrayAdapter extends ArrayAdapter<DrawerItem> {
    private static DrawerItemArrayAdapter drawerItemArrayAdapter = null;
    private Activity currentActivity;
    private DrawerItem[] drawerItems;

    private DrawerItemArrayAdapter(Activity currentActivity, DrawerItem[] drawerItems) {
        super(currentActivity, R.layout.drawer_list_item, drawerItems);
        this.currentActivity = currentActivity;
        this.drawerItems = drawerItems;
    }

    public static DrawerItemArrayAdapter getInstance(Activity currentActivity) {
        if (drawerItemArrayAdapter == null) {
            DrawerItem[] drawerItems = new DrawerItem[] { new DrawerItem("HOME", 0),
                    new DrawerItem("PLANNER", 0), new DrawerItem("NOTIFICATIONS", 0),
                    new DrawerItem("CHAT", 0), new DrawerItem("FRIENDS", 0),
                    new DrawerItem("PROFILE", 0), new DrawerItem("SETTINGS", 0) };
            drawerItemArrayAdapter = new DrawerItemArrayAdapter(currentActivity, drawerItems);
        }
        return drawerItemArrayAdapter;
    }

    public DrawerItem[] getArray() {
        return drawerItems;
    }

    public void setUnreadMessageCounter(int counter) {
        drawerItems[2].counter = counter;
        this.notifyDataSetChanged();
    }

    public void decreaseUnreadMessageCounter() {
        drawerItems[2].counter--;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.drawer_list_item, null);

        DrawerItem rowItem = getItem(position);

        TextView titleTextView = (TextView) rowView.findViewById(R.id.drawerRowTitle);
        TextView counterTextView = (TextView) rowView.findViewById(R.id.drawerRowCounter);

        titleTextView.setText(rowItem.title);
        if (rowItem.counter == 0) {
            counterTextView.setVisibility(View.GONE);
        }
        else {
            counterTextView.setVisibility(View.VISIBLE);
            counterTextView.setText(String.valueOf(rowItem.counter));
        }

        return rowView;
    }

}
