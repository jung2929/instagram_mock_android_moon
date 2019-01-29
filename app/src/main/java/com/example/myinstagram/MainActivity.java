package com.example.myinstagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //co

    ImageView imgHome, imgSearch, imgPost, imgHeart, imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        ArrayList<TimeLine> items = new ArrayList<>();

        String tempImageUrl = "http://stackhouse.s3.amazonaws.com/b2ae375b60d494290d29b56dd1325135_image.png";
        String tempImageUrl2 = "http://file2.nocutnews.co.kr/newsroom/image/2018/03/08/20180308173518466664_0_710_360.jpg";
        String tempImageUrl3 = "http://i1.wp.com/insidestory.kr/wp-content/uploads/2016/11/instagram.jpg?fit=950%2C633&ssl=1";

        TimeLine temp = new TimeLine(tempImageUrl,"dudwls", "스타벅스", "#스타벅스 #아메리카노", "줄바꿈", "1분전", "15");
        temp.addImageUrl(tempImageUrl);
        temp.addImageUrl(tempImageUrl2);

        TimeLine temp2 = new TimeLine(tempImageUrl,"rudtns", "스타벅스", "#스타벅스 #아메리카노", "줄바꿈222","1분전", "10");
        temp2.addImageUrl(tempImageUrl3);
        //temp2.addImageUrl(tempImageUrl3);
        items.add(temp);
        items.add(temp2);


        RecyclerView rv = (RecyclerView) findViewById(R.id.TimeLine);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        TimeLineAdapter timeLineAdapter = new TimeLineAdapter(getSupportFragmentManager(), items, this);
        rv.setAdapter(timeLineAdapter);


    }

    private void init(){
        imgHome = findViewById(R.id.imgHome);
        imgSearch = findViewById(R.id.imgSearch);
        imgPost = findViewById(R.id.imgPost);
        imgHeart = findViewById(R.id.imgHeart);
        imgProfile = findViewById(R.id.imgProfile);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgHome.setImageResource(R.drawable.home_white);
                Intent intent= new Intent(MainActivity.this,MyPageActivity.class);
                startActivity(intent);
            }
        });
    }
}
