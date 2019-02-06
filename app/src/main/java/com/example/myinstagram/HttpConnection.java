package com.example.myinstagram;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http2.Header;

public class HttpConnection {

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    public HttpConnection(){
        this.client = new OkHttpClient();
    }


    /** 웹 서버로 요청을 한다. */
    public void accountApi(String id, String password, String name, String email, String introduction, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        try {
            jsonInput.put("id", id);
            jsonInput.put("password", password);
            jsonInput.put("name", name);
            jsonInput.put("email", email);
            jsonInput.put("introduction", introduction) ;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );
        //RequestBody body = new FormBody.Builder()
        //        .add("id", id)
        //        .add("password2", password)
        //        .add("name", name)
        //        .add("email", email)
        //        .add("introduction", introduction)
        //        .build();
        Request request = new Request.Builder()
                .url("http://www.chanbyeol.com/User")
                .post(reqBody)
                .build();
        client.newCall(request).enqueue(callback);
    }



    public void loginApi(String id, String password, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        try {
            jsonInput.put("user_id", id);
            jsonInput.put("user_password", password);
            //jsonInput.put("user_id", "dudwls0113");
            //jsonInput.put("user_password", "ans1152214");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );
        Request request = new Request.Builder()
                .url("http://www.chanbyeol.com/login")
                .post(reqBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void followerListApi(String jwt, Callback callback) {
        Request request = new Request.Builder()
                .header("x-access-token", jwt)
                .url("http://www.chanbyeol.com/follower")
                .get()
                .build();
        client.newCall(request).enqueue(callback);
        //Log.d("로그인토큰시도", jwt);
    }

    public void followingListApi(String jwt, Callback callback) {

        Request request = new Request.Builder()
                .header("x-access-token", jwt)
                .url("http://www.chanbyeol.com/following")
                .get()
                .build();
        client.newCall(request).enqueue(callback);
        //Log.d("로그인토큰시도", jwt);
    }

    public void followApi(String jwt, String friendId, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        try {
            jsonInput.put("friend_id", friendId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );
        Request request = new Request.Builder()
                .url("http://www.chanbyeol.com/follow")
                .header("x-access-token", jwt)
                .post(reqBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void commentApi(String jwt, String comment, int postNum, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        try {
            jsonInput.put("comment_content", comment);
            jsonInput.put("post_number", postNum);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );
        Request request = new Request.Builder()
                .url("http://www.chanbyeol.com/comment")
                .header("x-access-token", jwt)
                .post(reqBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public void imageUpload(String jwt, String sourceImageFile, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        File sourceFile = new File(sourceImageFile);
        Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/")+1);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                .addFormDataPart("userfile", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                //.addFormDataPart("result", "photo_image")
                .build();

        Request request = new Request.Builder()
                .url("http://www.chanbyeol.com/image")
                .header("x-access-token", jwt)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void postApi(String jwt, String content, String imageUrl, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        try {
            jsonInput.put("content", content);
            jsonInput.put("url", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );
        Request request = new Request.Builder()
                .url("http://www.chanbyeol.com/posts")
                .header("x-access-token", jwt)
                .post(reqBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void userInfo(String user_id, Callback callback) {

        JSONObject jsonInput = new JSONObject();

        try {
            jsonInput.put("user_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        //RequestBody reqBody = RequestBody.create(
        //        MediaType.parse("application/json; charset=utf-8"),
        //        jsonInput.toString()
        //);

        String httpUrl = "http://www.chanbyeol.com/userInfo/" + user_id;
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        client.newCall(request).enqueue(callback);
    }

}

