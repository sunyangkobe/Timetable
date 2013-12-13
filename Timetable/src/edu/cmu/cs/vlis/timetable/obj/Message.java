package edu.cmu.cs.vlis.timetable.obj;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Message {
    private int id;
    private int user_from;
    private int user_to;
    private boolean read;
    private boolean fetched;
    private String title;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "GMT")
    private Date sent_date;
    private int reference_id;
    private String type;
    private boolean display;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUser_from() {
        return user_from;
    }
    
    public void setUser_from(int user_from) {
        this.user_from = user_from;
    }
    
    public int getUser_to() {
        return user_to;
    }
    
    public void setUser_to(int user_to) {
        this.user_to = user_to;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public boolean isFetched() {
        return fetched;
    }
    
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getSent_date() {
        return sent_date;
    }
    
    public void setSent_date(Date sent_date) {
        this.sent_date = sent_date;
    }
    
    public int getReference_id() {
        return reference_id;
    }
    
    public void setReference_id(int reference_id) {
        this.reference_id = reference_id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
    
}
