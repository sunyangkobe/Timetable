package edu.cmu.cs.vlis.timetable.listener;

import android.view.View;
import android.widget.EditText;

public class EditTextNonEmptyFocusListener implements View.OnFocusChangeListener {
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            String textString = ((EditText) view).getText().toString();
            if (textString.isEmpty()) {
                ((EditText) view).setError("should not be empty!");
            }
            else {
                ((EditText) view).setError(null);
            }
        }
    }
}