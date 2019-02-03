package com.example.myinstagram.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.myinstagram.FeedAdapter;
import com.example.myinstagram.R;
import com.example.myinstagram.TimeLine;

import java.util.ArrayList;
import java.util.Date;

public class FeedActivty extends AppCompatActivity {

    ArrayList<TimeLine> feed = new ArrayList<>();
    ImageView imgHome, imgSearch, imgPost, imgHeart, imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_activty);

        init();

        Intent intent = getIntent();

        String profileUrl = intent.getExtras().getString("profile");
        String name = intent.getExtras().getString("name");
        ArrayList<String> imageUrlList = intent.getExtras().getStringArrayList("imageList");
        String location = intent.getExtras().getString("location");
        String like = intent.getExtras().getString("like");
        String comment = intent.getExtras().getString("comment");
        String comment2 = intent.getExtras().getString("comment2");

        TimeLine timeLine  = new TimeLine(profileUrl, name, location, comment, comment2, new Date(), 15+"개");
        for(int i=0; i<imageUrlList.size(); i++){
            timeLine.addImageUrl(imageUrlList.get(i));
        }
        feed.add(timeLine);
        RecyclerView rv = (RecyclerView) findViewById(R.id.feed);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        FeedAdapter feedAdapter = new FeedAdapter(getSupportFragmentManager(), feed, this);
        rv.setAdapter(feedAdapter);
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
    }
}
