package edu.cmu.cs.vlis.timetable.fragment;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.adapter.DrawerItemArrayAdapter;
import edu.cmu.cs.vlis.timetable.adapter.MessagesArrayAdapter;
import edu.cmu.cs.vlis.timetable.adapter.MessagesArrayAdapter.ViewHolder;
import edu.cmu.cs.vlis.timetable.async.ClearAllMessagesAsyncTask;
import edu.cmu.cs.vlis.timetable.async.GetUnreadMessageAsyncTask;
import edu.cmu.cs.vlis.timetable.obj.Message;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class NotificationsFragment extends Fragment {
    private MessagesArrayAdapter messagesArrayAdapter;
    private Activity parentActivity;
    private ListView messageListView;

    private GetUnreadMessageAsyncTask getUnreadMessageAsyncTask;
    private boolean getUnreadMessageComplete = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        messageListView = (ListView) getView().findViewById(R.id.messagesListView);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        getUnreadMessageAsyncTask = new GetUnreadMessageAsyncTask(this);
        getUnreadMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        parentActivity.getActionBar().setTitle("NOTIFICATIONS");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (getUnreadMessageAsyncTask != null
                && getUnreadMessageAsyncTask.getStatus() == Status.RUNNING) {
            getUnreadMessageAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification_actionbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.clearAllNotification:
            clearAllMessage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void drawMessages(Message[] messages) {
        if (getActivity() != null) {
            messagesArrayAdapter = new MessagesArrayAdapter(this, new ArrayList<Message>(
                    Arrays.asList(messages)));
            messageListView.setAdapter(messagesArrayAdapter);
        }

        int unreadMessageCounter = 0;
        for (Message message : messages) {
            if (!message.isRead()) unreadMessageCounter++;
        }
        // update number of unread message counter in drawer
        DrawerItemArrayAdapter.getInstance(parentActivity).setUnreadMessageCounter(
                unreadMessageCounter);
    }

    public void clearAllMessageInAdapter() {
        // clear message list
        ArrayList<Message> messageList = messagesArrayAdapter.getList();
        messageList.clear();
        messagesArrayAdapter.notifyDataSetChanged();

        // update the unread notification counter
        DrawerItemArrayAdapter.getInstance(parentActivity).setUnreadMessageCounter(0);
    }

    public void confirmFriendRequest(View rowView) {
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        Button confirmButton = viewHolder.confirmButton;

        // set the confirm button background from unclicked to clicked
        Utils.setButtonAsDrawable(confirmButton, R.drawable.shadow_button_clicked);

        confirmButton.setText("Confirmed");

        // set two buttons as un-clickable
        confirmButton.setOnClickListener(null);
        viewHolder.declineButton.setOnClickListener(null);

        setMessageAsRead(rowView);
    }

    public void declineFriendRequest(View rowView) {

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        Button declineButton = viewHolder.declineButton;

        // set the decline button background from unclicked to clicked
        Utils.setButtonAsDrawable(declineButton, R.drawable.shadow_button_clicked);

        declineButton.setText("Declined");

        // set two buttons as un-clickable
        viewHolder.confirmButton.setOnClickListener(null);
        declineButton.setOnClickListener(null);

        setMessageAsRead(rowView);
    }

    public void setMessageAsRead(View rowView) {
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.messageTitle.setTypeface(null, Typeface.NORMAL);
        viewHolder.messageContent.setTypeface(null, Typeface.NORMAL);
        DrawerItemArrayAdapter.getInstance(parentActivity).decreaseUnreadMessageCounter();
    }

    public void setGetUnreadMessageCompelte(boolean isComplete) {
        getUnreadMessageComplete = isComplete;
    }

    private void clearAllMessage() {
        if (!getUnreadMessageComplete) {
            return;
        }
        new ClearAllMessagesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
