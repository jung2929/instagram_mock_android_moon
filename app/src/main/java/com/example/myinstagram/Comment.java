package com.example.myinstagram;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    String profileUrl;
    String name;
    String comment;
    Date postTime;
    String time;

    public Comment(String name, String comment, Date postTime, String profileUrl)
    {
        this.name = name;
        this.comment = comment;
        this.postTime = postTime;
        this.profileUrl = profileUrl;
        time = "1분 전";
    }

    public String getComment() {
        return comment;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }
}
