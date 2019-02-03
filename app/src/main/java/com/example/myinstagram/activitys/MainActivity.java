package com.example.myinstagram.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.baoyz.widget.PullRefreshLayout;
import com.example.myinstagram.Comment;
import com.example.myinstagram.R;
import com.example.myinstagram.TimeLine;
import com.example.myinstagram.TimeLineAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static ArrayList<TimeLine> timeline = new ArrayList<>();
    static String myProfileUrl = "https://media.treepla.net:447/project/7ac115de-ec05-4ca3-8ea7-f3b70a22a1dc.png";
    static String myName = "youngjinmoon";

    ImageView imgHome, imgSearch, imgPost, imgHeart, imgProfile;
    RecyclerView feedList;
    LinearLayoutManager mLayoutManager;
    static TimeLineAdapter timeLineAdapter;
    PullRefreshLayout pullRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String tempImageUrl = "http://stackhouse.s3.amazonaws.com/b2ae375b60d494290d29b56dd1325135_image.png";
        String tempImageUrl2 = "http://file2.nocutnews.co.kr/newsroom/image/2018/03/08/20180308173518466664_0_710_360.jpg";
        String tempImageUrl3 = "http://i1.wp.com/insidestory.kr/wp-content/uploads/2016/11/instagram.jpg?fit=950%2C633&ssl=1";

        TimeLine temp = new TimeLine(tempImageUrl,"dudwls", "스타벅스", "#스타벅스 #아메리카노", "줄바꿈", new Date(), "15");
        temp.addImageUrl(tempImageUrl);
        temp.addImageUrl(tempImageUrl2);
        Date date = new Date();
        Comment comment = new Comment("user1","첫댓글", date, tempImageUrl);
        Comment comment2 = new Comment("user2","두번째댓글", date, tempImageUrl);
        Comment comment3 = new Comment("user3","세번째댓글", date, tempImageUrl);
        temp.addIComment(comment);
        temp.addIComment(comment2);
        temp.addIComment(comment3);

        TimeLine temp2 = new TimeLine(tempImageUrl,"rudtns", "스타벅스", "#스타벅스 #아메리카노", "줄바꿈222",new Date(), "10");
        temp2.addImageUrl(tempImageUrl3);
        //temp2.addImageUrl(tempImageUrl3);
        timeline.add(temp);
        timeline.add(temp2);

        init();


    }

    @Override
    protected void onStart() {
        super.onStart();
        updateFeed();
    }

    private void updateFeed(){
        //서버를통해 업데이트
            //timeLineAdapter.notifyDataSetChanged();
    }

    private void init(){
        imgHome = findViewById(R.id.imgHome);
        imgSearch = findViewById(R.id.imgSearch);
        imgPost = findViewById(R.id.imgPost);
        imgHeart = findViewById(R.id.imgHeart);
        imgProfile = findViewById(R.id.imgProfile);

        feedList = (RecyclerView) findViewById(R.id.TimeLine);
        mLayoutManager = new LinearLayoutManager(this);
        feedList.setLayoutManager(mLayoutManager);
        timeLineAdapter = new TimeLineAdapter(getSupportFragmentManager(), timeline, this);
        feedList.setAdapter(timeLineAdapter);

        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        // listen refresh event
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //로딩작업수행
                pullRefreshLayout.setRefreshing(false);//로딩 멈추기
            }

        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,MyPageActivity.class);
                startActivity(intent);
            }
        });
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,ImageSelectActivity.class);
                startActivity(intent);
            }
        });
    }



}
