package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class PostItemDrawer extends ItemDrawer {

    public PostItemDrawer(Activity currentActivity, ViewGroup rootView) {
        LayoutInflater inflater = LayoutInflater.from(currentActivity);
        itemView = inflater.inflate(R.layout.post_item, rootView, false);
    }

    @Override
    public void setElement(int index, CharSequence content) {
        // TODO Auto-generated method stub
        switch (index) {
        case 0:
            TextView posterNameTextView = (TextView) itemView.findViewById(R.id.posterNameTextView);
            posterNameTextView.setText(content);
            break;
        case 1:
            TextView likeCommentTextView = (TextView) itemView
                    .findViewById(R.id.numberOfLIkesAndCommentsTextView);
            likeCommentTextView.setText(content);
            break;
        case 2:
            TextView postContentTextView = (TextView) itemView
                    .findViewById(R.id.postContentTextView);
            postContentTextView.setText(content);
            break;
        default:
            break;
        }
    }

    public void setImage(Bitmap imageBitmap) {
        ImageView posterProfileImageView = (ImageView) itemView.findViewById(R.id.posterProfileImageView);
        posterProfileImageView.setImageBitmap(imageBitmap);
    }
    
    public void setLikeButtonOnClickListener(View.OnClickListener listener) {
        Button likeButton = (Button) itemView.findViewById(R.id.postLikeButton);
        likeButton.setOnClickListener(listener);
    }

    public void setCommentButtonOnClickListener(View.OnClickListener listener) {
        Button commentButton = (Button) itemView.findViewById(R.id.postCommentButton);
        commentButton.setOnClickListener(listener);
    }

    public void setPosterImageOnClickListener(View.OnClickListener listener) {
        ImageView posterImageView = (ImageView) itemView.findViewById(R.id.posterProfileImageView);
        posterImageView.setOnClickListener(listener);
    }
    
    public void setPosterNameOnClickListener(View.OnClickListener listener) {
        TextView posterNameTextView = (TextView) itemView.findViewById(R.id.posterNameTextView);
        posterNameTextView.setOnClickListener(listener);
    }

    public void setLikeButtonDrawableResource(int resId) {
        Button likeButton = (Button) itemView.findViewById(R.id.postLikeButton);
        Utils.setButtonAsDrawable(likeButton, resId);
    }

    public void setCommentButtonVisibility(boolean visibility) {
        Button commentButton = (Button) itemView.findViewById(R.id.postCommentButton);
        if (visibility) {
            commentButton.setVisibility(View.VISIBLE);
        }
        else {
            commentButton.setVisibility(View.GONE);
        }
    }

    @Override
    public View getElement(int index) {
        switch (index) {
        case 0:
            return itemView.findViewById(R.id.posterNameTextView);
        case 1:
            return itemView.findViewById(R.id.numberOfLIkesAndCommentsTextView);
        case 2:
            return itemView.findViewById(R.id.postContentTextView);
        default:
            return null;
        }
    }
}
