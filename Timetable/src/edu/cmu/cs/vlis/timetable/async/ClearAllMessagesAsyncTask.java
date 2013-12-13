package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;

import android.support.v4.app.Fragment;
import edu.cmu.cs.vlis.timetable.fragment.NotificationsFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class ClearAllMessagesAsyncTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Fragment currentFragment;
    private NetworkStatus status;
    
    public ClearAllMessagesAsyncTask(Fragment currentFragment) {
        super(currentFragment.getActivity(), currentFragment.getView());
        this.currentFragment = currentFragment;
    }

    @Override
    protected Void doInBackground(Void... args) {
        JsonDataProvider dataProvider = new JsonDataProvider(currentFragment.getActivity());

        dataProvider.readJsonNode(APIAddr.CLEAR_ALL_MESSAGE, new HashMap<String, String>());
        status = dataProvider.getLastNetworkStatus();
        
        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentFragment.getActivity());
        }
        else {
            ((NotificationsFragment) currentFragment).clearAllMessageInAdapter();
        }
    }
    
    
}
