package edu.cmu.cs.vlis.timetable.fragment;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.adapter.FriendsArrayAdapter;
import edu.cmu.cs.vlis.timetable.async.GetFriendsAsyncTask;
import edu.cmu.cs.vlis.timetable.obj.Friend;

public class FriendsFragment extends Fragment {
    private ListView friendsListView;
    private GetFriendsAsyncTask getFriendsAsyncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        friendsListView = (ListView) getView().findViewById(R.id.friendsListView);
        getFriendsAsyncTask = new GetFriendsAsyncTask(this);
        getFriendsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        if (getFriendsAsyncTask != null && getFriendsAsyncTask.getStatus() == Status.RUNNING) {
            getFriendsAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    public void drawFriends(Friend[] friends) {
        if (getActivity() == null || friends == null) return;

        friendsListView.setAdapter(new FriendsArrayAdapter(this, friends));
    }
}
