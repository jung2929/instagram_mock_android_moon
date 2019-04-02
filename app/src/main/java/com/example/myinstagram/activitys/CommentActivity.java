package com.example.myinstagram.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.data.Comment;
import com.example.myinstagram.adapters.CommentAdapter;
import com.example.myinstagram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.myinstagram.activitys.MainActivity.myId;
import static com.example.myinstagram.activitys.MainActivity.myProfileUrl;
import static com.example.myinstagram.activitys.MainActivity.timeline;

public class CommentActivity extends BaseActivity {

    ArrayList<Comment> commentList;
    RecyclerView commentListView;
    LinearLayoutManager mLayoutManager;
    CommentAdapter commentAdapter;
    ImageView imgMyProfile;
    TextView txtPost;
    EditText editComment;
    int feedIndex;

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentList = (ArrayList<Comment>)getIntent().getSerializableExtra("comment");
        Intent intent = getIntent();
        feedIndex=intent.getExtras().getInt("index");
        init();
        commentListLoad(feedIndex);
    }
//
//    private void init() {
//        commentListView = (RecyclerView) findViewById(R.id.commentList);
//        mLayoutManager = new LinearLayoutManager(this);
//        commentListView.setLayoutManager(mLayoutManager);
//        commentAdapter = new CommentAdapter(getSupportFragmentManager(), commentList, this);
//        commentListView.setAdapter(commentAdapter);
//
//        editComment = findViewById(R.id.editComment);
//        txtPost = findViewById(R.id.txtPost);
//        txtPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //String profileUrl = "https://media.treepla.net:447/project/7ac115de-ec05-4ca3-8ea7-f3b70a22a1dc.png";
//                Comment newComment = new Comment("myNickName", editComment.getText().toString(), new Date(), myProfileUrl);
//                commentList.add(newComment);
//                commentAdapter.notifyDataSetChanged();
//                editComment.setText("");
//                hideKeyBoard();
//                timeline.get(feedIndex).addIComment(newComment); // 로컬테스트용
//            }
//        });
//    }

    public void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);
    }


    @Override
    void init() {
        commentListView = (RecyclerView) findViewById(R.id.commentList);
        mLayoutManager = new LinearLayoutManager(this);
        commentListView.setLayoutManager(mLayoutManager);
        commentAdapter = new CommentAdapter(getSupportFragmentManager(), commentList, this);
        commentListView.setAdapter(commentAdapter);

        editComment = findViewById(R.id.editComment);
        txtPost = findViewById(R.id.txtPost);
        imgMyProfile=findViewById(R.id.imgMyProfile);
        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String profileUrl = "https://media.treepla.net:447/project/7ac115de-ec05-4ca3-8ea7-f3b70a22a1dc.png";
                commentPost(feedIndex, editComment.getText().toString());

                Comment newComment = new Comment(myId, "  " + editComment.getText().toString(), new Date(), myProfileUrl);
                commentList.add(newComment);
                commentAdapter.notifyDataSetChanged();
                editComment.setText("");
                hideKeyBoard();
                //timeline.get(feedIndex).addIComment(newComment); // 로컬테스트용
            }
        });

        Glide.with(context).load(myProfileUrl).apply(new RequestOptions().centerCrop().circleCrop()).into(imgMyProfile);

    }

    private void commentPost(final int postNum, final String comment) {
        new Thread() {
            public void run() {
                HttpConnection httpConnection = new HttpConnection();
                SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                String jwt = pref.getString("jwt", "");
                httpConnection.commentApi(jwt, comment, postNum, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        String data="";

                        result=response.body().string();
                        Log.d("댓글등록", result);
                        //int index = result.indexOf("{");
                        //result = result.substring(index);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("댓글등록 시도", json.toString());
                            resultcode = json.getInt("code");

                            if (resultcode == 100) {
                                Log.d("댓글등록 성공 ", data);
                                //txtId.setText(json.getJSONArray("data").getJSONObject(0).getString("user_id"));
                                //txtName.setText(json.getJSONArray("data").getJSONObject(0).getString("name"));
                                //txtIntro.setText(json.getJSONArray("data").getJSONObject(0).getString("introduction"));
                                //Glide.with(context).load(myProfileImageUrl).apply(new RequestOptions().centerCrop().circleCrop()).into(imgMyProfile);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }


    private void commentListLoad(final int feedNum) {
        new Thread() {
            public void run() {
                HttpConnection httpConnection = new HttpConnection();
                SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                String jwt = pref.getString("jwt", "");
                httpConnection.commentListApi(jwt,feedNum ,new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        JSONArray data;

                        result=response.body().string();
                        Log.d("댓글리스트", result);
                        //int index = result.indexOf("{");
                        //result = result.substring(index);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("댓글리스트 시도", json.toString());
                            resultcode = json.getInt("code");

                            data = json.getJSONArray("data");
                            Log.d("유저정보data", data.toString());

                            if (resultcode == 100) {
                                Log.d("유저정보조회 성공 ", data.toString());
                                for(int i=0; i<data.length(); i++){
                                    JSONObject comment = data.getJSONObject(i);
                                    Comment newComment = new Comment(comment.getString("commentId") + "  ", comment.getString("commentContent"), new Date(), "");
                                    commentList.add(newComment);
                                }
                                for(int i=0; i<commentList.size(); i++) {
                                    Log.d("포스트넘", i+" "+commentList.size());
                                    profileImageLoad(commentList.get(i).getName(), i);
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    public void profileImageLoad(final String id, final int index){
        Thread profileUrlThread = new Thread() {
            public void run() {
                HttpConnection httpConnection = new HttpConnection();
                httpConnection.userInfo(id, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode = 0;
                        String data = "";

                        result = response.body().string();
                        Log.d("피드프로필 조회", result);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            resultcode = json.getInt("code");
                            data = json.getJSONArray("data").toString();
                            Log.d("피드프로필 조회 시도", data);

                            if (resultcode == 100) {
                                Log.d("피드프로필 조회 성공 ", data);
                                String url = (json.getJSONArray("data").getJSONObject(0).getString("profileImage"));
                                commentList.get(index).setProfileUrl(url);
                                Log.d("마지막 피드까지 불러오는지 판단", index + " " +timeline.size());
                                if(index == commentList.size()-1){
                                    //마지막 피드까지 프로필 불러오면
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // UI변경하는 쓰레드
                                            Log.d("마지막 피드까지 불러옴", "로딩멈춤, notify");
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        profileUrlThread.start();
    }

}
