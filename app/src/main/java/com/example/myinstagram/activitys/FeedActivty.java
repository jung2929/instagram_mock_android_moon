package com.example.myinstagram.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.adapters.FeedAdapter;
import com.example.myinstagram.R;
import com.example.myinstagram.data.TimeLine;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myinstagram.activitys.MainActivity.myProfileUrl;

public class FeedActivty extends AppCompatActivity {

    ArrayList<TimeLine> feed = new ArrayList<>();
    ImageView imgHome, imgSearch, imgPost, imgHeart, imgProfile;

    SharedPreferences pref;
    String myProfileImageUrl;

    String profileUrl;
    String name;
    ArrayList<String> imageUrlList;
    String location;
    String like;
    String comment;
    String comment2;
    int postNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_activty);

        init();

        Intent intent = getIntent();

        profileUrl =intent.getExtras().getString("profile");;
        name = intent.getExtras().getString("name");
        imageUrlList = intent.getExtras().getStringArrayList("imageList");
        location = intent.getExtras().getString("location");
        like = intent.getExtras().getString("like");
        comment = intent.getExtras().getString("comment");
        comment2 = intent.getExtras().getString("comment2");
        postNum = intent.getExtras().getInt("postNum");

        TimeLine timeLine  = new TimeLine(postNum, profileUrl, name, location, comment, comment2, new Date(), like);
        for(int i=0; i<imageUrlList.size(); i++){
            timeLine.addImageUrl(imageUrlList.get(i));
        }
        feed.add(timeLine);

    }
    private void init(){
        imgHome = findViewById(R.id.imgHome);
        imgSearch = findViewById(R.id.imgSearch);
        imgPost = findViewById(R.id.imgPost);
        imgHeart = findViewById(R.id.imgHeart);
        imgProfile = findViewById(R.id.imgProfile);

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //resultIntent.putExtra("result","연산 결과는 "+result+" 입니다.");
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(FeedActivty.this,ImageSelectActivity.class);
                startActivity(intent);
            }
        });

        Glide.with(this).load(myProfileUrl).apply(new RequestOptions().centerCrop().circleCrop()).into(imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        myProfileImageUrl = pref.getString("myProfileImageUrl", "");

        RecyclerView rv = (RecyclerView) findViewById(R.id.feed);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        FeedAdapter feedAdapter = new FeedAdapter(getSupportFragmentManager(), feed, this, profileUrl);
        rv.setAdapter(feedAdapter);

    }
}
