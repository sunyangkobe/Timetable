package edu.cmu.cs.vlis.timetable.fragment;

import java.util.Arrays;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.EditProfileActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.SyncProfileViewTask;
import edu.cmu.cs.vlis.timetable.draw.ItemDrawer;
import edu.cmu.cs.vlis.timetable.draw.MultiColorLinearLayoutAdapter;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller.POST_MODE;
import edu.cmu.cs.vlis.timetable.draw.SimpleItemDrawer;
import edu.cmu.cs.vlis.timetable.listener.CourseItemClickListener;
import edu.cmu.cs.vlis.timetable.listener.ProfileEditClickListener;
import edu.cmu.cs.vlis.timetable.listener.ProfileFriendClickListener;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.obj.UserProfile;
import edu.cmu.cs.vlis.timetable.persistentdatamanager.SessionManager;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class ProfileFragment extends Fragment {
    private int userId;
    private Button loadMoreBtn;
    private Button friendOrEditBtn;
    private UserProfile userProfile;
    private TextView userNameTextView;
    private TextView userSchoolTextView;
    private TextView userStatusTextView;
    private TextView profileCourseTitleTextView;
    private TextView profilePostTitleTextView;
    private LinearLayout course_rootView;
    private LinearLayout post_rootView;
    private ImageView userProfileImage;

    private SyncProfileViewTask syncProfileViewTask;

    public ProfileFragment() {
        // Empty constructor required for fragment subclasses
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        userId = getActivity().getIntent().getIntExtra("user_id", -1);
        /*
         * if user navigates to his own profile by clicking the poster name of some post, the
         * user_id won't be -1; so we compare the user_id with the user's own user_id store in
         * shared preference to determine if user is viewing his own profile
         */
        SessionManager sessionManager = new SessionManager(getActivity());
        if (userId != -1 && userId == sessionManager.getUserId()) {
            userId = -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Profile");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadMoreBtn = (Button) view.findViewById(R.id.profile_load_more_btn);
        loadMoreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProfile != null) {
                    drawPost(userProfile.getComments(), userProfile.getFirst_name(), false);
                }
            }
        });

        friendOrEditBtn = (Button) view.findViewById(R.id.profile_friend_or_edit_button);
        if (userId == -1) {
            // viewing oneself's profile, the edit case
            // we could directly set the listener
            friendOrEditBtn.setOnClickListener(new ProfileEditClickListener(getActivity()));
            friendOrEditBtn.setText("Update Status");
        }
        else {
            friendOrEditBtn.setText("Loading...");
            // viewing other's profile, the friend case
            // we couldn't not set listener now as we don't know the friend status
        }

        userNameTextView = (TextView) getView().findViewById(R.id.profile_user_name);
        userStatusTextView = (TextView) getView().findViewById(R.id.profile_user_status);
        userSchoolTextView = (TextView) getView().findViewById(R.id.profile_user_school);
        profileCourseTitleTextView = (TextView) getView().findViewById(R.id.profile_course_title);
        profilePostTitleTextView = (TextView) getView().findViewById(R.id.profile_post_title);
        course_rootView = (LinearLayout) getView().findViewById(R.id.profile_course_container);
        post_rootView = (LinearLayout) getView().findViewById(R.id.profile_post_container);
        userProfileImage = (ImageView) getView().findViewById(R.id.user_profile_image);
    }

    @Override
    public void onResume() {
        super.onResume();
        syncProfileViewTask = new SyncProfileViewTask(this);
        syncProfileViewTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (syncProfileViewTask != null && syncProfileViewTask.getStatus() != Status.RUNNING) {
            syncProfileViewTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (userId == -1) {
            inflater.inflate(R.menu.update_info_actionbar, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionbar_update_info) {
            startEditProfileActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public enum FriendStatus {
        USER_SELF("Edit"), IS_FRIEND("Message"), FRIEND_REQUEST_SENT("Request Sent"), NOT_FRIEND(
                "Send Friend Request");

        String hint;

        FriendStatus(String hint) {
            this.hint = hint;
        }

        public String getHint() {
            return hint;
        }
    }

    private String getHint(int statusCode) {
        String hint = "";
        switch (statusCode) {
        case 0:
            hint = "Edit";
            break;
        case 1:
            hint = "Message";
            break;
        case 2:
            hint = "Request Sent";
            break;
        case 3:
        default:
            hint = "Add Friend";
            break;
        }

        return hint;
    }

    public void setProfileView(UserProfile userProfile) {
        this.userProfile = userProfile;

        // the fragment has been detached from the activity
        if (getActivity() == null || userProfile == null) return;

        userProfileImage.setImageBitmap(Utils.convertBase64StringToBitmap(userProfile.getAvatar()));
        userNameTextView.setText(userProfile.getFirst_name() + " " + userProfile.getLast_name());
        userStatusTextView.setText(userProfile.getStatus());
        userSchoolTextView.setText(userProfile.getUniversity());

        if (userProfile.getFriend_status() == 0 || userProfile.getFriend_status() == 1) {
            drawCourse(userProfile.getCourses(), userProfile.getFirst_name());
            drawPost(userProfile.getComments(), userProfile.getFirst_name(), true);
        }
        else {
            profileCourseTitleTextView.setText("You don't have permission to view "
                    + userProfile.getFirst_name() + "'s courses or recent posts.");
        }

        friendOrEditBtn.setVisibility(View.VISIBLE);
        if (userId != -1) {
            friendOrEditBtn.setText(getHint(userProfile.getFriend_status()));
            friendOrEditBtn.setOnClickListener(new ProfileFriendClickListener(getActivity(),
                    userProfile));
        }

    }

    private void drawCourse(Course[] courses, String userFirstName) {
        if (courses == null) {
            profileCourseTitleTextView.setText(userFirstName + " is not enrolled in any course.");
            return;
        }
        profileCourseTitleTextView.setText("Courses " + userFirstName + " is enrolled in:");

        course_rootView.removeAllViews();

        MultiColorLinearLayoutAdapter colorLinearLayoutAdapter = new MultiColorLinearLayoutAdapter(
                getActivity(), course_rootView);
        ItemDrawer itemDrawer;
        if (courses != null) {
            for (Course course : courses) {
                itemDrawer = new SimpleItemDrawer(getActivity(), course_rootView);

                itemDrawer.setElement(0, course.toString());
                itemDrawer.setElement(1, ">");
                itemDrawer.setOnClickListener(new CourseItemClickListener(getActivity(), course
                        .getId(), course.getCourse_code()));

                colorLinearLayoutAdapter.addView(itemDrawer.getView());
            }
        }
    }

    private void drawPost(Post[] posts, String userFirstName, boolean invalidate) {
        if (posts == null) {
            profilePostTitleTextView.setText(userFirstName + " has no recent post.");
            return;
        }
        profilePostTitleTextView.setText(userFirstName + "'s recent posts:");

        if (invalidate) {
            post_rootView.removeAllViews();
        }
        int start = post_rootView.getChildCount();
        loadMoreBtn.setVisibility(posts.length <= start ? View.GONE : View.VISIBLE);
        // if (posts.length <= start) {
        // return;
        // }
        PostItemsContentFiller postItemsContentFiller = new PostItemsContentFiller(getActivity(),
                post_rootView, Arrays.copyOfRange(posts, start, Math.min(start + 5, posts.length)),
                -1, POST_MODE.COMPLETE_MODE);
        postItemsContentFiller.fillContent();
    }

    public void startEditProfileActivity() {
        if (userProfile != null) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("userProfile", userProfile);
            startActivity(intent);
        }
    }
}
