package edu.cmu.cs.vlis.timetable.async;

import android.support.v4.app.Fragment;
import edu.cmu.cs.vlis.timetable.fragment.FriendsFragment;
import edu.cmu.cs.vlis.timetable.obj.DataProvider;
import edu.cmu.cs.vlis.timetable.obj.Friend;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class GetFriendsAsyncTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Fragment currentFragment;
    private Friend[] friends;
    private NetworkStatus status;
    
    public GetFriendsAsyncTask(Fragment currentFragment) {
        super(currentFragment.getActivity(), currentFragment.getView());
        this.currentFragment = currentFragment;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        DataProvider dataProvider = new JsonDataProvider(getActivity());
        friends = dataProvider.readObject(APIAddr.GET_FRIEND_INFO, null, Friend[].class);
        status = dataProvider.getLastNetworkStatus();
        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, getActivity());
        }
        else {
            ((FriendsFragment) currentFragment).drawFriends(friends);
        }
    }
    
}
