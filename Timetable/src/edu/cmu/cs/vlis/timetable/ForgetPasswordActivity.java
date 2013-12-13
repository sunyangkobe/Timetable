package edu.cmu.cs.vlis.timetable;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.async.RecoverPasswordAsyncTask;

public class ForgetPasswordActivity extends Activity implements OnClickListener {

    private EditText usernameField;
    private Button recoverPwdBtn;
    private TextView successNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        usernameField = (EditText) findViewById(R.id.fp_username_field);
        recoverPwdBtn = (Button) findViewById(R.id.fp_password_btn);
        recoverPwdBtn.setOnClickListener(this);

        successNotification = (TextView) findViewById(R.id.fp_notification);
    }

    @Override
    public void onClick(View v) {
        new RecoverPasswordAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                usernameField.getText().toString().trim());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(usernameField.getWindowToken(), 0);
    }

    public void notifySuccess() {
        successNotification.setVisibility(View.VISIBLE);
    }

}
