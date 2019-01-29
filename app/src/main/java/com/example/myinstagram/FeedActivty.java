package com.example.myinstagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class FeedActivty extends AppCompatActivity {

    ArrayList<TimeLine> feed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_activty);

        Intent intent = getIntent();

        String profileUrl = intent.getExtras().getString("profile");
        String name = intent.getExtras().getString("name");
        ArrayList<String> imageUrlList = intent.getExtras().getStringArrayList("imageList");
        String location = intent.getExtras().getString("location");
        String like = intent.getExtras().getString("like");
        String comment = intent.getExtras().getString("comment");
        String comment2 = intent.getExtras().getString("comment2");

        TimeLine timeLine  = new TimeLine(profileUrl, name, location, comment, comment2, "방금전", 15+"개");
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
}
