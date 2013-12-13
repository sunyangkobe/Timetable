package edu.cmu.cs.vlis.timetable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.async.SignUpTask;
import edu.cmu.cs.vlis.timetable.db.USUniversityNameDatabase;
import edu.cmu.cs.vlis.timetable.listener.EditTextNonEmptyFocusListener;
import edu.cmu.cs.vlis.timetable.listener.PasswordLengthFocusListener;
import edu.cmu.cs.vlis.timetable.obj.User;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class RegisterActivity extends Activity implements OnFocusChangeListener {
    private EditText firstNameEditText, lastNameEditText, emailEditText, genderEditText,
            passwordEditText;
    private Set<String> universityNamesSet;
    private USUniversityNameDatabase univNameDB;
    private Cursor univNameCursor;
    private AutoCompleteTextView univTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Spinner is one choice for gender selection, but it seems there is few things we can
        // customize in Spinner UI, so I decide to use EditText instead.
        /*
         * Spinner genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
         * ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
         * R.array.genders, android.R.layout.simple_spinner_item);
         * genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         * genderSpinner.setAdapter(genderAdapter);
         */

        univTextView = (AutoCompleteTextView) this.findViewById(R.id.univNameAutoComplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, extractUniversitiesFromDB());
        univTextView.setAdapter(adapter);
        // Set the on OnFocusChangeListener on the AutoCompleteTextView so it will show an error
        // message
        // if the user doen't select an item from the suggestion list
        univTextView.setOnFocusChangeListener(this);

        // Initialize all edit texts and text views
        firstNameEditText = (EditText) this.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) this.findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) this.findViewById(R.id.registerEmailEditText);
        genderEditText = (EditText) this.findViewById(R.id.genderEditText);
        passwordEditText = (EditText) this.findViewById(R.id.registerPasswordEditText);

        // Initialize the restrictions on each edit text and text view, like the number of digits of
        // password
        firstNameEditText.setOnFocusChangeListener(new EditTextNonEmptyFocusListener());
        lastNameEditText.setOnFocusChangeListener(new EditTextNonEmptyFocusListener());
        genderEditText.setOnFocusChangeListener(new EditTextNonEmptyFocusListener());
        emailEditText.setOnFocusChangeListener(new EditTextNonEmptyFocusListener());
        passwordEditText.setOnFocusChangeListener(new PasswordLengthFocusListener());

        setTextField(firstNameEditText, getIntent().getStringExtra("fname"));
        setTextField(lastNameEditText, getIntent().getStringExtra("lname"));
        if (getIntent().getStringExtra("gender") != null) {
            genderEditText.setText(getIntent().getStringExtra("gender").equals("male") ? "M" : "F");
        }
        setTextField(emailEditText, getIntent().getStringExtra("email"));
    }

    public void setTextField(EditText field, String input) {
        if (input == null) return;
        field.setText(input.trim());
    }

    public void clickSignUp(View view) {
        if (formValidation()) {
            User user = new User();
            user.setEmail(emailEditText.getText().toString())
                    .setFname(firstNameEditText.getText().toString())
                    .setLname(lastNameEditText.getText().toString())
                    .setGender(genderEditText.getText().toString().toUpperCase(Locale.getDefault()))
                    .setSchool(univTextView.getText().toString())
                    .setPassword(passwordEditText.getText().toString());
            new SignUpTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
        }
    }

    // Go back to the login page if user clicks the "Already have an account?" text
    public void clickAlreadyRegistered(View view) {
        this.finish();
    }

    public void onDestroy() {
        super.onDestroy();
        univNameCursor.close();
        univNameDB.close();
    }

    private boolean formValidation() {
        StringBuilder sb = new StringBuilder();
        if (firstNameEditText.getText() == null || firstNameEditText.getText().length() == 0) {
            sb.append("First Name cannot be empty\n");
        }
        if (lastNameEditText.getText() == null || lastNameEditText.getText().length() == 0) {
            sb.append("Last Name cannot be empty\n");
        }
        if (emailEditText.getText() == null
                || !Utils.isValidEmailAddr(emailEditText.getText().toString())) {
            sb.append("Email address is invalid\n");
        }
        if (genderEditText.getText() == null
                || (!genderEditText.getText().toString().equalsIgnoreCase("M") && !genderEditText
                        .getText().toString().equalsIgnoreCase("F"))) {
            sb.append("Gender is invalid, either \"M\" or \"F\"\n");
        }
        if (univTextView.getText() == null
                || !universityNamesSet.contains(univTextView.getText().toString())) {
            sb.append("The university you selected is not in our list, please contact us\n");
        }
        if (passwordEditText.getText() == null || passwordEditText.getText().length() < 8) {
            sb.append("Password is invalid, has to be equal or greater than 8 chars or digits\n");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private String[] extractUniversitiesFromDB() {
        // Initialize the pre-stored database file in "assets" directory to the App's private
        // space using the third-party "SQLiteAssetHelper" class.
        univNameDB = new USUniversityNameDatabase(this);

        // Retrieve the database and store all the name of US universities into universityNames
        // array
        univNameCursor = univNameDB.getAllUSUniversityNames();
        String[] universityNames = new String[univNameCursor.getCount()];
        int i = 0;
        do {
            universityNames[i++] = univNameCursor.getString(univNameCursor.getColumnIndex("name"));
        }
        while (univNameCursor.moveToNext());

        // Initialize the HashSet containing the names of universities so we can easily retrieve
        // and validate
        // the text user inputs in AutoCompleteTextView to force them select item from the
        // suggestion list
        List<String> universityNamesList = Arrays.asList(universityNames);
        universityNamesSet = new HashSet<String>(universityNamesList);
        return universityNames;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            String inputText = ((AutoCompleteTextView) v).getText().toString();
            if (!universityNamesSet.contains(inputText)) {
                ((AutoCompleteTextView) v).setError("invalid university");
            }
            else {
                ((AutoCompleteTextView) v).setError(null);
            }
        }
    }
}
