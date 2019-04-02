package com.example.myinstagram.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

public class TimeLine {
    String profielUrl, postName, location, postComment,postComment2, timeCheck, like;
    ArrayList<String> imageUrl;
    ArrayList<Uri> imageUri;
    ArrayList<Comment> commentList;
    int index;
    Date postTime;

    public TimeLine(int index, String profielUrl, String postName, String location, String postComment, String postComment2, Date postTime, String like) {
        this.profielUrl = profielUrl;
        this.postName = postName;
        this.location = location;
        this.postComment = postComment;
        this.like = like;
        this.index = index;
        imageUrl=new ArrayList<>();
        commentList=new ArrayList<>();
        imageUri=new ArrayList<>();
        //imageUrl.add("http://file2.nocutnews.co.kr/newsroom/image/2018/03/08/20180308173518466664_0_710_360.jpg");

        //int index = postComment.indexOf("\n");
        //postComment2 = postComment.substring(index+1);
        this.postComment2 = postComment2;

        /////서버에서 받아온 작성시간 계싼
        timeCheck = "1시간 전";
    }

    public TimeLine(String image, int index, String profielUrl, String postName, String location, String postComment, String postComment2, Date postTime, String like) {
        this.profielUrl = profielUrl;
        this.postName = postName;
        this.location = location;
        this.postComment = postComment;
        this.like = like;
        this.index = index;
        imageUrl=new ArrayList<>();
        commentList=new ArrayList<>();
        imageUri=new ArrayList<>();
        //imageUrl.add("http://file2.nocutnews.co.kr/newsroom/image/2018/03/08/20180308173518466664_0_710_360.jpg");

        //int index = postComment.indexOf("\n");
        //postComment2 = postComment.substring(index+1);
        this.postComment2 = postComment2;

        /////서버에서 받아온 작성시간 계싼
        timeCheck = "1시간 전";

        ///이미지 추가
        imageUrl.add(image);
    }
    public void addImageUrl(String url){
        imageUrl.add(url);
    }

    public void addIComment(Comment comment){
        commentList.add(comment);
    }

    public void addIimageUri(Uri uri){
        imageUri.add(uri);
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

    public int getIndex() {
        return index;
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

    public ArrayList<Comment> getCommentList() {
        return commentList;
    }

    public ArrayList<Uri> getImageUri() {
        return imageUri;
    }

    public void setProfielUrl(String profielUrl) {
        this.profielUrl = profielUrl;
    }

    public void addLike(){
        int likes = Integer.parseInt(like) + 1;
        like = String.valueOf(likes);
    }
}
