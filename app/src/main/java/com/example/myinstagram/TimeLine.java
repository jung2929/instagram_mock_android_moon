package com.example.myinstagram;

import java.util.ArrayList;

public class TimeLine {
    String profielUrl, postName, location, postComment,postComment2, timeCheck, like;
    ArrayList<String> imageUrl;

    public TimeLine(String profielUrl, String postName, String location, String postComment, String postComment2, String timeCheck, String like) {
        this.profielUrl = profielUrl;
        this.postName = postName;
        this.location = location;
        this.postComment = postComment;
        this.timeCheck = timeCheck;
        this.like = like;
        imageUrl=new ArrayList<>();
        //imageUrl.add("http://file2.nocutnews.co.kr/newsroom/image/2018/03/08/20180308173518466664_0_710_360.jpg");

        //int index = postComment.indexOf("\n");
        //postComment2 = postComment.substring(index+1);
        this.postComment2 = postComment2;
    }
    public void addImageUrl(String url){
        imageUrl.add(url);
    }

    public String getPostName() {
        return postName;
    }

    public String getLocation() {
        return location;
    }

    public String getPostComment() {
        return postComment;
    }

    public String getTimeCheck() {
        return timeCheck;
    }

    public String getLike() {
        return like;
    }

    public ArrayList<String> getImageUrl() {
        return imageUrl;
    }

    public String getPostComment2(){
        return postComment2;
    }

    public String getProfielUrl() {
        return profielUrl;
    }
}
