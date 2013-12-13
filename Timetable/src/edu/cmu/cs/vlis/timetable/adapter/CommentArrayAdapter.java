package edu.cmu.cs.vlis.timetable.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.listener.PosterNameImageClickListener;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class CommentArrayAdapter extends ArrayAdapter<Post> {
    private Activity currentActivity;

    private class ViewHolder {
        public ImageView commenterProfileImage;
        public TextView commenterName;
        public TextView commentContent;
    }

    public CommentArrayAdapter(Activity currentActivity, Post[] comments) {
        super(currentActivity, R.layout.comment_item, comments);
        this.currentActivity = currentActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View commentView = convertView;
        ViewHolder viewHolder;

        if (commentView == null) {
            LayoutInflater inflater = currentActivity.getLayoutInflater();
            commentView = inflater.inflate(R.layout.comment_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.commenterProfileImage = (ImageView) commentView
                    .findViewById(R.id.posterProfileImageView);
            viewHolder.commenterName = (TextView) commentView.findViewById(R.id.posterNameTextView);
            viewHolder.commentContent = (TextView) commentView
                    .findViewById(R.id.postContentTextView);

            commentView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) commentView.getTag();
        Post comment = getItem(position);

        viewHolder.commenterProfileImage.setImageBitmap(Utils.convertBase64StringToBitmap(comment
                .getAvatar()));
        viewHolder.commenterName.setText(comment.getUsername());
        viewHolder.commentContent.setText(comment.getContent());

        viewHolder.commenterProfileImage.setOnClickListener(new PosterNameImageClickListener(
                currentActivity, comment.getUser_id()));
        viewHolder.commenterName.setOnClickListener(new PosterNameImageClickListener(
                currentActivity, comment.getUser_id()));

        return commentView;
    }
}
