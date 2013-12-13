package edu.cmu.cs.vlis.timetable.obj;

public class PostAndRelatedComments {
    private Post post;
    private Post[] comments;
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        this.post = post;
    }
    
    public Post[] getComments() {
        return comments;
    }
    
    public void setComments(Post[] comments) {
        this.comments = comments;
    }    
}
