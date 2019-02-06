package com.example.myinstagram.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myinstagram.adapters.FollowerPagerAdapter;
import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.R;

import static com.example.myinstagram.activitys.MyPageActivity.followerCount;
import static com.example.myinstagram.activitys.MyPageActivity.followingCount;

public class FollowerActivity extends AppCompatActivity {

    private Context context;
    private TabLayout followerTabLayout;
    private ViewPager viewPager;
    private FollowerPagerAdapter followerPagerAdapter;

    private HttpConnection httpConnection;

    ImageView imgHome, imgSearch, imgPost, imgHeart, imgProfile;
    TextView txtUserId;
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        context = getApplicationContext();

        init();
    }

    private void init(){
        httpConnection = new HttpConnection();

        followerTabLayout=(TabLayout) findViewById(R.id.layout_tab);
        followerTabLayout.addTab(followerTabLayout.newTab().setCustomView(createTabView("팔로워", String.valueOf(followerCount))));
        followerTabLayout.addTab(followerTabLayout.newTab().setCustomView(createTabView("팔로잉", String.valueOf(followingCount))));

        viewPager = (ViewPager) findViewById(R.id.followerPager);
        followerPagerAdapter = new FollowerPagerAdapter(getSupportFragmentManager(), followerTabLayout.getTabCount());
        viewPager.setAdapter(followerPagerAdapter);
        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(followerTabLayout));

        followerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imgHome = findViewById(R.id.imgHome);
        imgSearch = findViewById(R.id.imgSearch);
        imgPost = findViewById(R.id.imgPost);
        imgHeart = findViewById(R.id.imgHeart);
        imgProfile = findViewById(R.id.imgProfile);
        txtUserId = findViewById(R.id.txtUserId);

        txtUserId.setText("아이디로설정");

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK,resultIntent);
                finish();
            }

        });

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(FollowerActivity.this,ImageSelectActivity.class);
                startActivity(intent);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private View createTabView(String tab, String count) {
        View tabView = LayoutInflater.from(context).inflate(R.layout.custom_follower_tab, null);
        TextView txtTab = (TextView) tabView.findViewById(R.id.txtTab);
        TextView txtCount = (TextView) tabView.findViewById(R.id.txtFollowerCount);
        txtTab.setText(tab);
        txtCount.setText(count);
        return tabView;
    }


}
