package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import edu.cmu.cs.vlis.timetable.R;

public class HelpOverlayClickListner implements View.OnClickListener {
    private Activity currentActivity;
    private int clickNum;
    private Dialog overlayDialog;

    public HelpOverlayClickListner(Activity currentActivity, Dialog overlayDialog) {
        this.currentActivity = currentActivity;
        this.overlayDialog = overlayDialog;
        clickNum = 0;
    }

    @Override
    public void onClick(View view) {
        ImageView imageView1 = (ImageView) overlayDialog.findViewById(R.id.overlayImageView1);
        ImageView imageView2 = (ImageView) overlayDialog.findViewById(R.id.overlayImageView2);
        // if it's the first click, then open the drawer, make image1 invisible and image2 visible
        if (clickNum == 0) {
            imageView1.setVisibility(View.INVISIBLE);
            openDrawer();
            imageView2.setVisibility(View.VISIBLE);
            clickNum ++;
        }
        // if it's the second click
        else if (clickNum == 1) {
            overlayDialog.dismiss();
        }
    }

    private void openDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) currentActivity.findViewById(R.id.homeDrawerLayout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }
}
