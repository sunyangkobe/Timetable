package edu.cmu.cs.vlis.timetable.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import edu.cmu.cs.vlis.timetable.ForgetPasswordActivity;
import edu.cmu.cs.vlis.timetable.LoginActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.RegisterActivity;
import edu.cmu.cs.vlis.timetable.async.AuthenticationLoginTask;
import edu.cmu.cs.vlis.timetable.async.FBUserLoginTask;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class LoginFragment extends Fragment implements StatusCallback, GraphUserCallback,
        OnClickListener {

    private UiLifecycleHelper uiHelper;
    private EditText emailEditText, passwordEditText;
    private AuthenticationLoginTask authenticationLoginTask;
    private LoginButton authButton;
    private Button loginButton;
    private TextView registerLink;
    private TextView forgetPWDLink;
    private GraphUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), this);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions("email", "user_birthday");

        loginButton = (Button) view.findViewById(R.id.signInButton);
        loginButton.setOnClickListener(this);

        registerLink = (TextView) view.findViewById(R.id.registerTextView);
        registerLink.setOnClickListener(this);

        forgetPWDLink = (TextView) view.findViewById(R.id.forgetPasswordTextView);
        forgetPWDLink.setOnClickListener(this);

        emailEditText = (EditText) view.findViewById(R.id.loginEmailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.loginPasswordEditText);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Session session = Session.getActiveSession();
        // if (session != null && (session.isOpened() || session.isClosed())) {
        // onSessionStateChange(session, session.getState(), null);
        // }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void call(Session session, SessionState state, Exception exception) {
        onSessionStateChange(session, state, exception);
    }

    @Override
    public void onCompleted(GraphUser user, Response response) {
        Log.d("KOBE", user.getUsername());
        this.user = user;
        if (user != null) {
            new FBUserLoginTask((LoginActivity) getActivity()).execute(user);
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            // Request user data and show the results
            Request.newMeRequest(session, this).executeAsync();
        }
        else if (state.isClosed()) {
            user = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == loginButton.getId()) {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Disable the button
            v.setEnabled(false);
            // Execute the authentication task in background thread
            authenticationLoginTask = new AuthenticationLoginTask((LoginActivity) getActivity(), v);
            authenticationLoginTask.execute(new String[] { email, password });
        }
        // When clicking "Don't have an account?" text, navigate to the Register activity.
        else if (v.getId() == registerLink.getId()) {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            if (user != null) {
                Utils.setUserData(intent, user);
            }
            startActivity(intent);
        }

        else if (v.getId() == forgetPWDLink.getId()) {
            Intent intent = new Intent(getActivity(), ForgetPasswordActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (authenticationLoginTask != null
                && authenticationLoginTask.getStatus() != AsyncTask.Status.FINISHED) {
            authenticationLoginTask.cancel(true);
        }
    }

}
