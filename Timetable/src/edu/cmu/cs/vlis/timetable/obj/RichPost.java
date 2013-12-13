package edu.cmu.cs.vlis.timetable.obj;

import android.graphics.Bitmap;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class RichPost {
    private Post post;
    
    public RichPost(Post post) {
        this.post = post;
    }
    
    public String getPosterName() {
        return post.getUsername();
    }
    
    public String getNumberOfLikeAndComment() {
        return String.valueOf(post.getLike_num()) + " likes, "
                + String.valueOf(post.getChildren_num()) + " comments";
    }
    
    public String getPostContent() {
        return post.getContent();
    }
    
    public int getPostId() {
        return post.getId();
    }
    
    public boolean getIsUserLikePost() {
        return post.isUser_like_post();
    }
    
    public Bitmap getAvatarBitmap() {
        if (post.getAvatar() == null) return null;
        return Utils.convertBase64StringToBitmap(post.getAvatar());
    }
}
