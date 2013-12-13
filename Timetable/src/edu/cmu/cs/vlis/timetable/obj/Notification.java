package edu.cmu.cs.vlis.timetable.obj;

public class Notification {
    private String friend;
    private String comment;
    private int unread;
    
    public String getFriend() {
        return friend;
    }
    public void setFriend(String friend) {
        this.friend = friend;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public int getUnread() {
        return unread;
    }
    public void setUnread(int unread) {
        this.unread = unread;
    }    
}
