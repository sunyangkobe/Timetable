package edu.cmu.cs.vlis.timetable;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import edu.cmu.cs.vlis.timetable.async.PostCommentAsyncTask;
import edu.cmu.cs.vlis.timetable.async.SyncCommentViewTask;
import edu.cmu.cs.vlis.timetable.draw.CommentItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.ItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller;
import edu.cmu.cs.vlis.timetable.draw.PostItemsContentFiller.POST_MODE;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.util.Constants;

public class CommentViewActivity extends Activity implements OnClickListener {
    private int postId;
    private Post post;
    private LinearLayout postLinearLayout;
    private ListView commentsListView;
    private EditText commentContentEditText;
    private Button commentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);
        setupActionBar();

        postLinearLayout = (LinearLayout) findViewById(R.id.postContentLinearLayout);
        commentsListView = (ListView) findViewById(R.id.commentContentListView);
        commentContentEditText = (EditText) findViewById(R.id.commentContentEdtiText);
        commentButton = (Button) findViewById(R.id.commentButton);
        
        postId = getIntent().getIntExtra("post_id", -1);
        if (postId == -1) {
            Toast.makeText(this, "post id invalid", Toast.LENGTH_LONG).show();
        }
        else {
            new SyncCommentViewTask(this, postId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.comment_view_action_bar_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comment_view_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPostView(Post post) {
        postLinearLayout.removeAllViews();
        PostItemsContentFiller postItemsContentFiller = new PostItemsContentFiller(this, postLinearLayout,
                new Post[] { post }, -1, POST_MODE.HIDE_COMMENT_BUTTON);
        postItemsContentFiller.fillContent();
    }

    public void setCommentsView(Post[] comments) {
        ItemsContentFiller commentItemsContentFiller = new CommentItemsContentFiller(this,
                commentsListView, comments);
        commentItemsContentFiller.fillContent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.commentButton:
            String commentContent = commentContentEditText.getText().toString().trim();
            if (commentContent.isEmpty()) {
                String toastText = "The comment should not be empty!";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            }
            else if (commentContent.length() > Constants.COMMENT_MAX_CHARACTER_NUMBER) {
                String toastText = "The comment should not exceed "
                        + Constants.COMMENT_MAX_CHARACTER_NUMBER + " characters";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            }
            else {
                PostCommentAsyncTask postCommentAsyncTask = new PostCommentAsyncTask(this,
                        post.getCourse_id());
                postCommentAsyncTask.setContent(commentContent);
                postCommentAsyncTask.setParentCommentId(post.getId());
                postCommentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    public void closeKeyboardRemoveContent() {
        commentContentEditText.setText(new String());

        // hide the soft keyboard manually
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentContentEditText.getWindowToken(), 0);
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setCommentButtonClickListner() {
        commentButton.setOnClickListener(this);
    }
}
