package edu.cmu.cs.vlis.timetable.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.async.LoadingDialogAsyncTask;

@SuppressLint("ValidFragment")
public class GenericComposeFragment extends DialogFragment {
    /**
     * Contains the callback method which would be called after users finishes composing the
     * content.
     */
    public static abstract class ComposeCallback extends LoadingDialogAsyncTask<Void, Void, Void> {
        public ComposeCallback(Activity activity) {
            super(activity);
        }

        /**
         * Called after users finishes composing the content. Must be thread-safe.
         * 
         * @param content
         *            composed by user.
         */
        public abstract void setContent(String content);
    }

    private final ComposeCallback callback;
    private final String titleText;
    private final String positiveButtonText;
    private final int maxCharacterNumber;

    /**
     * Default constructor for this fragment.
     * 
     * @param callback
     *            to be called after user finishes composing.
     * @param titleText
     *            to be displayed in the title.
     * @param positiveButtonText
     *            to be displayed on the button.
     * @param maxCharacterNumber
     *            max number of characters accepted in the dialog
     */
    public GenericComposeFragment(ComposeCallback callback, String titleText,
            String positiveButtonText, int maxCharacterNumber) {
        this.callback = callback;
        this.titleText = titleText;
        this.positiveButtonText = positiveButtonText;
        this.maxCharacterNumber = maxCharacterNumber;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View composePostDialogView = inflater.inflate(R.layout.dialog_compose_post, null);
        final EditText composePostEditText = (EditText) composePostDialogView
                .findViewById(R.id.composePostEditText);
        // the reason why we don't set listener in "setPositiveButton" method is
        // because if we set the listener in this way, we cannot control whether the dialog to be
        // dismissed or not after clicking the button, so we set it null here and override the
        // onClick later
        builder.setView(composePostDialogView).setTitle(titleText)
                .setPositiveButton(positiveButtonText, null);

        final AlertDialog composePostDialog = builder.create();
        final ComposeCallback callback = this.callback;

        // set on show listener
        composePostDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = composePostDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                // set custom listener for positive button because we don't want the dialog to
                // dismiss if the input is invalid
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String content = composePostEditText.getText().toString().trim();
                        if (content == null || content.isEmpty()) {
                            String toastText = "The content should not be empty!";
                            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
                        }
                        else if (content.length() > maxCharacterNumber) {
                            String toastText = "The content should not exceed " + maxCharacterNumber + " characters!";
                            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
                        }
                        else {
                            callback.setContent(content);
                            callback.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            // dismiss the dialog if the input is valid
                            composePostDialog.dismiss();
                        }
                    }
                });
            }
        });

        return composePostDialog;
    }
}