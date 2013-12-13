package edu.cmu.cs.vlis.timetable.async;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import edu.cmu.cs.vlis.timetable.NavigationActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.adapter.DrawerItemArrayAdapter;
import edu.cmu.cs.vlis.timetable.obj.DataProvider;
import edu.cmu.cs.vlis.timetable.obj.DrawerItem;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Notification;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants;
import edu.cmu.cs.vlis.timetable.util.Constants.NavigationTargets;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public final class FetchNotificationDaemonTask {
    private static FetchNotificationDaemonTask instance = null;
    private Activity currentActivity;
    private int intervalTimeInMillis = Constants.DEFAULT_CHECK_INTERVAL;
    private boolean isRunning = false;
    private Thread daemonThread = null;
    private Handler handler;
    private boolean vibrateEnabled = true;
    private boolean soundEnabled = true;

    private FetchNotificationDaemonTask() {
        // exist to defeat instantiation
    }

    private FetchNotificationDaemonTask(Activity currentActivity) {
        this.currentActivity = currentActivity;
        this.handler = new Handler();
    }

    public static FetchNotificationDaemonTask getInstance(Activity currentActivity) {
        if (instance == null) {
            instance = new FetchNotificationDaemonTask(currentActivity);
        }
        return instance;
    }

    public void run() {
        if (isRunning) return;

        isRunning = true;
        daemonThread = new Thread(new DaemonThread(), "getNotificationDaemonThread");
        daemonThread.start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        if (daemonThread != null) {
            daemonThread.interrupt();
            daemonThread = null;
            isRunning = false;
        }
    }

    public void setCheckInterval(int intervalTimeInMillis) {
        this.intervalTimeInMillis = intervalTimeInMillis;
    }
    
    public int getCheckInterval() {
        return this.intervalTimeInMillis;
    }
    
    public void setVibrateEnabled(boolean vibrateEnabled) {
        this.vibrateEnabled = vibrateEnabled;
        Log.d("cc", "vibrateEnabled " + String.valueOf(this.vibrateEnabled));
    }
    
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    private class DaemonThread implements Runnable {
        private DataProvider dataProvider;
        private NetworkStatus status;
        private static final int NOTIFICATION_FRIEND_ID = 1;
        private static final int NOTIFICATION_COMMENT_ID = 2;
        private static final String NOTIFICATION_FRIEND_TITLE = "You have new friend requests";
        private static final String NOTIFICATION_COMMENT_TITLE = "You get new comments";

        public DaemonThread() {
            dataProvider = new JsonDataProvider(currentActivity);
        }

        @Override
        public void run() {
            Notification notification;
            int currentUnreadCount;
            int prevUnreadCount = 0;

            while (true) {
                Log.d("cc", "threadRunning");
                // get notification from server
                notification = dataProvider.readObject(APIAddr.GET_NOTIFICATION, null,
                        Notification.class);
                status = dataProvider.getLastNetworkStatus();

                if (status == NetworkStatus.VALID && notification != null) {
                 // show friend notification if necessary
                    if (notification.getFriend() != null && !notification.getFriend().isEmpty()) {
                        setNotification(NOTIFICATION_FRIEND_ID, NOTIFICATION_FRIEND_TITLE,
                                notification.getFriend());
                    }

                    // show comment notification if necessary
                    if (notification.getComment() != null && !notification.getComment().isEmpty()) {
                        setNotification(NOTIFICATION_COMMENT_ID, NOTIFICATION_COMMENT_TITLE,
                                notification.getComment());
                    }

                    // set counter for the notification drawer item
                    currentUnreadCount = notification.getUnread();
                    if (currentUnreadCount != prevUnreadCount) {
                        prevUnreadCount = currentUnreadCount;
                        // get the singleton drawer item list and set the counter of notification drawer
                        // item
                        DrawerItem[] drawerItems = DrawerItemArrayAdapter.getInstance(currentActivity)
                                .getArray();
                        drawerItems[2].counter = notification.getUnread();
                        // notify the drawer item adapter to update drawer ListView in main UI
                        // thread using handler
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                DrawerItemArrayAdapter.getInstance(currentActivity)
                                        .notifyDataSetChanged();
                            }
                        });
                    }
                }                
                
                // fetch notification from server every intervalTimeInMillis milli-seconds.
                try {
                    Thread.sleep(intervalTimeInMillis);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void setNotification(int notificationId, String notificationTitle,
                String notificationContent) {
            Intent resultIntent = new Intent(currentActivity, NavigationActivity.class);
            resultIntent.putExtra("navigation_target", NavigationTargets.NOTIFICATIONS);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(currentActivity);
            // Adds the back stack
            stackBuilder.addParentStack(NavigationActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity)
                    .setContentTitle(notificationTitle).setContentText(notificationContent)
                    .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true);
            // set sound and vibrate according to the setting
            if (soundEnabled) {
                Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(soundUri);
            }
            
            if (vibrateEnabled) {
                builder.setVibrate(new long[] { 100, 200, 100, 200 });
            }

            // set intent
            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager) currentActivity
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            // set content on notification bar
            builder.setTicker(notificationContent);

            // update or create notification
            notificationManager.notify(notificationId, builder.build());
        }

    }
}
