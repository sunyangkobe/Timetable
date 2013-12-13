package edu.cmu.cs.vlis.timetable;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import edu.cmu.cs.vlis.timetable.async.GetSchoolCoursesAsyncTask;
import edu.cmu.cs.vlis.timetable.fragment.LoginFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Token;
import edu.cmu.cs.vlis.timetable.persistentdatamanager.SessionManager;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.ExitCurrentActivityUtils;

public class LoginActivity extends FragmentActivity {

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // if the user has logged in before, then skip to the NavigationActivity directly
        if (getIntent().getBooleanExtra("ISLOGIN", false)) {
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
        }

        Log.d("cc", "LoginCreate");
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment)
                    .commit();
        }
        else {
            // Or set the fragment from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(
                    android.R.id.content);
        }
    }

    public NetworkStatus loginUser(String email, String password) {
        // initialize a httpClientJsonHelper to send a JSON format POST request to the server
        JsonDataProvider dataProvider = new JsonDataProvider(this);

        // initialize the requestBody
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", email);
        params.put("password", password);

        // send request to server
        Token token = dataProvider.readObject(APIAddr.API_TOKEN_AUTH, params, Token.class);
        NetworkStatus statusCode = dataProvider.getLastNetworkStatus();
        if (statusCode == NetworkStatus.VALID) {
            String sessionId = token.getToken();
            int userId = token.getId();
            
            // set the shared preference and session Id in memory
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.setSessionId(sessionId);
            // set userId in shared preference
            sessionManager.setUserId(userId);            
        }

        new GetSchoolCoursesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return statusCode;
    }

    @Override
    public void onBackPressed() {
        ExitCurrentActivityUtils.exitApplication(this);
    }
}
