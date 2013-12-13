package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import edu.cmu.cs.vlis.timetable.fragment.NotificationsFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Message;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class RespondCommentNotificationAsyncTask extends AsyncTask<Void, Void, Void> {
    private Fragment currentFragment;
    private Message message;
    private View rowView;
    private NetworkStatus status;
    
    public RespondCommentNotificationAsyncTask(Fragment currentFragment, Message message, View rowView) {
        this.currentFragment = currentFragment;
        this.message = message;
        this.rowView = rowView;
    }
    
    @Override
    protected Void doInBackground(Void... args) {
        JsonDataProvider dataProvider = new JsonDataProvider(currentFragment.getActivity());
        Map<String, String> params = new HashMap<String, String>();
        
        // we only mark the comment notification message as read
        params.put("id", String.valueOf(message.getId()));
        // mark as read
        dataProvider.readJsonNode(APIAddr.MARK_MESSAGE_AS_READ, params);
        status = dataProvider.getLastNetworkStatus();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentFragment.getActivity());
        }
        else {
            ((NotificationsFragment) currentFragment).setMessageAsRead(rowView);
        }
    }
}
