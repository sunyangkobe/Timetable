package edu.cmu.cs.vlis.timetable;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.async.UpdateProfileAsyncTask;
import edu.cmu.cs.vlis.timetable.db.USUniversityNameDatabase;
import edu.cmu.cs.vlis.timetable.obj.UserProfile;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class EditProfileActivity extends Activity implements OnFocusChangeListener, OnClickListener {

    private ImageView profileImageView;
    private EditText fNameField;
    private EditText lNameField;
    private AutoCompleteTextView schoolField;
    private EditText oldPwdField;
    private EditText newPwdField;
    private UserProfile userProfile;
    private Set<String> universityNamesSet;
    private USUniversityNameDatabase univNameDB;
    private Cursor univNameCursor;

    private boolean isSame = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getIntent() != null) {
            userProfile = (UserProfile) getIntent().getSerializableExtra("userProfile");
        }

        profileImageView = (ImageView) findViewById(R.id.edit_profile_user_image);
        fNameField = (EditText) findViewById(R.id.edit_profile_first_name_field);
        lNameField = (EditText) findViewById(R.id.edit_profile_last_name_field);
        schoolField = (AutoCompleteTextView) findViewById(R.id.edit_profile_univ_field);
        oldPwdField = (EditText) findViewById(R.id.edit_profile_old_pwd_field);
        newPwdField = (EditText) findViewById(R.id.edit_profile_new_pwd_field);

        if (userProfile != null) {
            String profileImageBase64 = userProfile.getAvatar();
            Bitmap profileImageBitmap;
            if (profileImageBase64 != null) {
                profileImageBitmap = Utils.convertBase64StringToBitmap(profileImageBase64);
            }
            else {
                profileImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kobe);
            }
            profileImageView.setImageBitmap(profileImageBitmap);
            fNameField.setText(userProfile.getFirst_name());
            lNameField.setText(userProfile.getLast_name());
            schoolField.setText(userProfile.getUniversity());
        }

        schoolField.setAdapter(new ArrayAdapter<String>(this, R.layout.autocomplete_list_item,
                extractUniversitiesFromDB()));
        schoolField.setOnFocusChangeListener(this);
        profileImageView.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageURI = data.getData();
                Bitmap profileImageBitmap = null;
                if ((Build.VERSION.SDK_INT < 19)) {
                    String realPath = getRealPathFromURI(selectedImageURI);
                    profileImageBitmap = BitmapFactory.decodeFile(realPath);
                }
                else {
                    try {
                        profileImageBitmap = getBitmapFromUri(selectedImageURI);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // currImageURI is the global variable I'm using to hold the content:
                updateProfileImage(profileImageBitmap);
            }
        }
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
        List<String> universityNamesList = Arrays.asList(universityNames);
        universityNamesSet = new HashSet<String>(universityNamesList);
        return universityNames;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.actionbar_done:
            if (userProfile != null && !isChanged()) {
                Toast.makeText(this, "Profile has nothing changed", Toast.LENGTH_LONG).show();
                finish();
                return true;
            }

            if (formValidation()) {
                // convert the bitmap to base64 String and store in userProfile object
                Bitmap profileImageBitmap = ((BitmapDrawable) profileImageView.getDrawable())
                        .getBitmap();
                userProfile.setAvatar(Utils
                        .compressAndConvertBitmapToBase64String(profileImageBitmap));

                userProfile.setFirst_name(fNameField.getText().toString().trim());
                userProfile.setLast_name(lNameField.getText().toString().trim());
                userProfile.setUniversity(schoolField.getText().toString().trim());
                if (oldPwdField.getText().toString().length() == 0
                        || newPwdField.getText().toString().length() == 0) {
                    new UpdateProfileAsyncTask(this, null, null).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, userProfile);
                }
                else {
                    new UpdateProfileAsyncTask(this, oldPwdField, newPwdField).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, userProfile);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean formValidation() {
        StringBuilder sb = new StringBuilder();
        if (fNameField.getText() == null || fNameField.getText().length() == 0) {
            sb.append("First Name cannot be empty\n");
        }
        if (lNameField.getText() == null || lNameField.getText().length() == 0) {
            sb.append("Last Name cannot be empty\n");
        }
        if (schoolField.getText() == null
                || !universityNamesSet.contains(schoolField.getText().toString())) {
            sb.append("The university you selected is not in our list, please contact us\n");
        }
        if (oldPwdField.getText() == null
                || (oldPwdField.getText().length() < 8 && oldPwdField.getText().length() > 0)) {
            sb.append("Old password is invalid, has to be equal or greater than 8 chars or digits\n");
        }
        if (newPwdField.getText() == null
                || (newPwdField.getText().length() < 8 && newPwdField.getText().length() > 0)) {
            sb.append("New password is invalid, has to be equal or greater than 8 chars or digits\n");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        univNameDB.close();
        univNameCursor.close();
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

    private boolean isChanged() {
        isSame &= fNameField.getText().toString().equals(userProfile.getFirst_name());
        isSame &= lNameField.getText().toString().equals(userProfile.getLast_name());
        isSame &= schoolField.getText().toString().equals(userProfile.getUniversity());
        isSame &= oldPwdField.getText().toString().length() == 0;
        isSame &= newPwdField.getText().toString().length() == 0;
        return !isSame;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case (R.id.edit_profile_user_image):
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            break;
        }
    }

    private void updateProfileImage(Bitmap newProfileImageBitmap) {
        // resize the image
        double width = newProfileImageBitmap.getWidth();
        double height = newProfileImageBitmap.getHeight();
        double ratio = 100 / width;
        int newHeight = (int) (ratio * height);

        Bitmap resizedNewProfileImageBitmap = Bitmap.createScaledBitmap(newProfileImageBitmap, 100,
                newHeight, true);

        // set the profile image view
        profileImageView.setImageBitmap(resizedNewProfileImageBitmap);

        // set the isSame flag as false
        isSame = false;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri,
                "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    
    // Convert the image URI to the direct file system path of the image file
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        android.database.Cursor cursor = getContentResolver().query(contentUri, 
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
