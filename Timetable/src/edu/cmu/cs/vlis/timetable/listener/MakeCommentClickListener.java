package edu.cmu.cs.vlis.timetable.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import edu.cmu.cs.vlis.timetable.CommentViewActivity;
import edu.cmu.cs.vlis.timetable.obj.Post;

public class MakeCommentClickListener implements View.OnClickListener {
    private Activity currentActivity;
    private Post post;
    
    public MakeCommentClickListener(Activity currentActivity, Post post) {
        this.currentActivity = currentActivity;
        this.post = post;
    }
    
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(currentActivity, CommentViewActivity.class);
        intent.putExtra("post_id", post.getId());       
        currentActivity.startActivity(intent);
    }
    
}
