package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.widget.ListView;
import edu.cmu.cs.vlis.timetable.adapter.CommentArrayAdapter;
import edu.cmu.cs.vlis.timetable.obj.Post;

public class CommentItemsContentFiller extends ItemsContentFiller {
    private ListView listView;
    private Post[] comments;
    
    public CommentItemsContentFiller(Activity currentActivity, ListView listView, Post[] comments) {
        super(currentActivity, listView);
        this.listView = listView;
        this.comments = comments;
    }

    @Override
    public void fillContent() {
        CommentArrayAdapter commentArrayAdapter = new CommentArrayAdapter(currentActivity, comments);
        listView.setAdapter(commentArrayAdapter);       
    }    
}
