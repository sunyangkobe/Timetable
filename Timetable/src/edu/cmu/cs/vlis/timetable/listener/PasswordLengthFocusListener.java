package edu.cmu.cs.vlis.timetable.listener;

import android.view.View;
import android.widget.EditText;

public class PasswordLengthFocusListener implements View.OnFocusChangeListener {
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            String passwordText = ((EditText) view).getText().toString();
            if (passwordText.length() < 6) {
                ((EditText) view).setError("password should be at least 6 digits");
            }
            else {
                ((EditText) view).setError(null);
            }
        }
    }
}