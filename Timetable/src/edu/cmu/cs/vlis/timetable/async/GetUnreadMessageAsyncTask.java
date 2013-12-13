package edu.cmu.cs.vlis.timetable.async;

import android.support.v4.app.Fragment;
import edu.cmu.cs.vlis.timetable.fragment.NotificationsFragment;
import edu.cmu.cs.vlis.timetable.obj.DataProvider;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Message;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class GetUnreadMessageAsyncTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Fragment currentFragment;
    private NetworkStatus status;
    private Message[] messages;

    public GetUnreadMessageAsyncTask(Fragment currentFragment) {
        super(currentFragment.getActivity(), currentFragment.getView());
        this.currentFragment = currentFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DataProvider dataProvider = new JsonDataProvider(getActivity());
        messages = dataProvider.readObject(APIAddr.GET_MESSAGE, null, Message[].class);
        status = dataProvider.getLastNetworkStatus();
        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, getActivity());
        }
        else {
            ((NotificationsFragment) currentFragment).drawMessages(messages);
            ((NotificationsFragment) currentFragment).setGetUnreadMessageCompelte(true);
        }
    }
}
