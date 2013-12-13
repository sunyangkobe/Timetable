package edu.cmu.cs.vlis.timetable.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import edu.cmu.cs.vlis.timetable.listener.PostLikeButtonListner;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class LikePostAsyncTask extends AsyncTask<Boolean, Void, Void> {
    private Activity currentActivity;
    private PostLikeButtonListner postLikeButtonListener;
    private View view;
    private int postId;
    private NetworkStatus status;
    
    public LikePostAsyncTask(Activity currentActivity, PostLikeButtonListner postLikeButtonListener, View view, int postId) {
        this.currentActivity = currentActivity;
        this.postLikeButtonListener = postLikeButtonListener;
        this.view = view;
        this.postId = postId;
    }
    
    @Override
    protected Void doInBackground(Boolean... isUserLikePostInfo) {
        boolean isUserLikePost = isUserLikePostInfo[0];
        String APIAddress;
        
        if (isUserLikePost) {
            APIAddress = APIAddr.LIKE_POST;
        }
        else {
            APIAddress = APIAddr.UNLIKE_POST;
        }
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("post_id", String.valueOf(postId));
        
        JsonDataProvider jsonDataProvider = new JsonDataProvider(currentActivity);
        jsonDataProvider.readJsonNode(APIAddress, params);
        status = jsonDataProvider.getLastNetworkStatus();
        
        return null;
    }
    
    @Override
    protected void onPostExecute(Void result) {
        if (status != NetworkStatus.VALID) {
            Utils.displayNetworkErrorMessage(status, currentActivity
                    .getApplicationContext());            
        }
        else {
            postLikeButtonListener.setButtonColorAndLikeNumberText(view);
        }
    }
    

}
