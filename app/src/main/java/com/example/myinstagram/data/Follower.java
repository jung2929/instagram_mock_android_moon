package com.example.myinstagram.data;

import java.io.Serializable;
import java.util.Date;

public class Follower implements Serializable {
    String profileUrl;
    String name;
    Boolean isYouFollowMe;

    public Follower(String profileUrl, String name, Boolean isYouFollowMe) {
        this.profileUrl = profileUrl;
        this.name = name;
        this.isYouFollowMe = isYouFollowMe;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getName() {
        return name;
    }

    public Boolean getYouFollowMe() {
        return isYouFollowMe;
    }

    public void setYouFollowMe(Boolean youFollowMe) {
        isYouFollowMe = youFollowMe;
    }
}
