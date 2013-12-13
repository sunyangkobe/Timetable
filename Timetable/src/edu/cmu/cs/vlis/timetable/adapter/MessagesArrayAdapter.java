package edu.cmu.cs.vlis.timetable.adapter;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.RespondFriendRequestAsyncTask.ResponseType;
import edu.cmu.cs.vlis.timetable.listener.CommentNotificationClickListener;
import edu.cmu.cs.vlis.timetable.listener.RespondFriendRequestClickListener;
import edu.cmu.cs.vlis.timetable.obj.Message;
import edu.cmu.cs.vlis.timetable.obj.RichMessage;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class MessagesArrayAdapter extends ArrayAdapter<Message> {
    private Fragment currentFragment;
    private ArrayList<Message> messageList;

    public MessagesArrayAdapter(Fragment currentFragment, ArrayList<Message> messageList) {
        super(currentFragment.getActivity(), R.layout.message_item, messageList);
        this.currentFragment = currentFragment;
        this.messageList = messageList;
    }

    public static class ViewHolder {
        public TextView messageTitle;
        public TextView messageTimestamp;
        public TextView messageContent;
        public Button confirmButton;
        public Button declineButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        if (rowView == null) {
            LayoutInflater inflater = currentFragment.getActivity().getLayoutInflater();
            rowView = inflater.inflate(R.layout.message_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.messageTitle = (TextView) rowView.findViewById(R.id.messageTitleTextView);
            viewHolder.messageTimestamp = (TextView) rowView
                    .findViewById(R.id.messageTimestampTextView);
            viewHolder.messageContent = (TextView) rowView
                    .findViewById(R.id.messageContentTextView);
            viewHolder.confirmButton = (Button) rowView.findViewById(R.id.messageConfirmButton);
            viewHolder.declineButton = (Button) rowView.findViewById(R.id.messageDeclineButton);

            rowView.setTag(viewHolder);
        }

        final Message message = getItem(position);
        RichMessage richMessage = new RichMessage(message);
        viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.messageTitle.setText(message.getTitle());
        viewHolder.messageTimestamp.setText(richMessage.getTimeIntervalFromSentTimeToNow());
        if (message.getType().equals("C")) {
            viewHolder.messageContent.setVisibility(View.VISIBLE);
            viewHolder.confirmButton.setVisibility(View.GONE);
            viewHolder.declineButton.setVisibility(View.GONE);

            viewHolder.messageContent.setText(message.getContent());
            rowView.setOnClickListener(new CommentNotificationClickListener(currentFragment,
                    message, rowView));
        }
        else if (message.getType().equals("I")) {
            viewHolder.messageContent.setVisibility(View.GONE);
            viewHolder.confirmButton.setVisibility(View.VISIBLE);
            viewHolder.declineButton.setVisibility(View.VISIBLE);

            viewHolder.confirmButton.setText("Confirm");
            Utils.setButtonAsDrawable(viewHolder.confirmButton, R.drawable.shadow_button_unclick);
            Log.d("cc", String.valueOf(message.getUser_from()));
            viewHolder.confirmButton.setOnClickListener(new RespondFriendRequestClickListener(
                    currentFragment, message, rowView, ResponseType.CONFIRM));
            
            viewHolder.declineButton.setText("Decline");
            Utils.setButtonAsDrawable(viewHolder.declineButton, R.drawable.shadow_button_unclick);
            viewHolder.declineButton.setOnClickListener(new RespondFriendRequestClickListener(
                    currentFragment, message, rowView, ResponseType.DECLINE));
            
            rowView.setOnClickListener(null);
        }

        if (message.isRead()) {
            viewHolder.messageTitle.setTypeface(null, Typeface.NORMAL);
            viewHolder.messageContent.setTypeface(null, Typeface.NORMAL);
        }
        else {
            viewHolder.messageTitle.setTypeface(null, Typeface.BOLD);
            viewHolder.messageContent.setTypeface(null, Typeface.BOLD);
        }

        return rowView;
    }

    public ArrayList<Message> getList() {
        return this.messageList;
    }
}
