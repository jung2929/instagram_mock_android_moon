package com.example.myinstagram.data;

import java.io.Serializable;
import java.util.Date;

public class Follower implements Serializable {
    String profileUrl;
    String name;
    Boolean amIFollowYou;

    public Follower(String profileUrl, String name, Boolean amIFollowYou) {
        this.profileUrl = profileUrl;
        this.name = name;
        this.amIFollowYou = amIFollowYou;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getName() {
        return name;
    }

    public Boolean getYouFollowMe() {
        return amIFollowYou;
    }

    public void setYouFollowMe(Boolean youFollowMe) {
        amIFollowYou = youFollowMe;
    }
}
