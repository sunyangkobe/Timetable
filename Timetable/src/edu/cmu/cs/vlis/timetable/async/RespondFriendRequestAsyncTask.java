package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.view.View;
import edu.cmu.cs.vlis.timetable.fragment.NotificationsFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Message;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class RespondFriendRequestAsyncTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Fragment currentFragment;
    private Message message;
    private View rowView;
    private NetworkStatus status;
    private ResponseType responseType;

    public static enum ResponseType {
        CONFIRM, DECLINE;
    }

    public RespondFriendRequestAsyncTask(Fragment currentFragment, Message message, View rowView,
            ResponseType responseType) {
        super(currentFragment.getActivity(), currentFragment.getView());
        this.currentFragment = currentFragment;
        this.rowView = rowView;
        this.responseType = responseType;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... args) {
        JsonDataProvider dataProvider = new JsonDataProvider(currentFragment.getActivity());
        Map<String, String> params = new HashMap<String, String>();

        // we should mark the message as read and mute no matter user confirms or declines it
        params.put("id", String.valueOf(message.getId()));
        // mark as read
        dataProvider.readJsonNode(APIAddr.MARK_MESSAGE_AS_READ, params);
        status = dataProvider.getLastNetworkStatus();
        if (status != NetworkStatus.VALID) return null;

        // mark as mute
        dataProvider.readJsonNode(APIAddr.MARK_MESSAGE_AS_MUTE, params);
        status = dataProvider.getLastNetworkStatus();
        if (status != NetworkStatus.VALID) return null;
        
        // get the from_user id
        String fromUserId = String.valueOf(message.getUser_from());
        params.put("id", fromUserId);
        
        // if user confirms the friend request
        if (responseType == ResponseType.CONFIRM) {
            // add friend
            dataProvider.readJsonNode(APIAddr.CONFIRM_FRIEND_REQUEST, params);
            status = dataProvider.getLastNetworkStatus();
        }
        // if user declines the friend request
        else {
            dataProvider.readJsonNode(APIAddr.DECLINE_FRIEND_REQUEST, params);
            status = dataProvider.getLastNetworkStatus();
        }

        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentFragment.getActivity());
        }
        else {
            if (responseType == ResponseType.CONFIRM) {
                ((NotificationsFragment) currentFragment).confirmFriendRequest(rowView); 
            }
            else {
                ((NotificationsFragment) currentFragment).declineFriendRequest(rowView); 
            }
        }
    }
}
