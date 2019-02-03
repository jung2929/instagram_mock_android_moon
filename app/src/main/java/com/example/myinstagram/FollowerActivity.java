package com.example.myinstagram;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.myinstagram.activitys.LoginActivity.jwt;

public class FollowerActivity extends AppCompatActivity {

    private Context context;
    private TabLayout followerTabLayout;
    private ViewPager viewPager;
    private FollowerPagerAdapter followerPagerAdapter;

    private HttpConnection httpConnection;
    ArrayList<Follower> followerList;
    ArrayList<Follower> followingList;


    int followerCount=1;
    int followingCount=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        context = getApplicationContext();

        init();
    }

    private void init(){
        httpConnection = new HttpConnection();
        followerLoad();
        followingLoad();

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
    }
    private View createTabView(String tab, String count) {
        View tabView = LayoutInflater.from(context).inflate(R.layout.custom_follower_tab, null);
        TextView txtTab = (TextView) tabView.findViewById(R.id.txtTab);
        TextView txtCount = (TextView) tabView.findViewById(R.id.txtFollowerCount);
        txtTab.setText(tab);
        txtCount.setText(count);
        return tabView;
    }


    private void followerLoad() {
        new Thread() {
            public void run() {
                httpConnection.followerListApi(jwt, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int resultcode=0;
                        try {
                            String result;
                            result=response.body().string();
                            int index = result.indexOf("{");
                            result = result.substring(index);
                            JSONObject jsonObject= new JSONObject(result);
                            resultcode=jsonObject.getInt("code");
                            if(resultcode==100){
                                JSONArray followerArray = (JSONArray)jsonObject.get("result");
                                for(int i=0;i<followerArray.length();i++){
                                    JSONObject tmp = (JSONObject)followerArray.get(i);//인덱스 번호로 접근해서 가져온다.
                                    followerCount++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    private void followingLoad() {
        new Thread() {
            public void run() {
                httpConnection.followingListApi(jwt, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int resultcode=0;
                        try {
                            String result;
                            result=response.body().string();
                            int index = result.indexOf("{");
                            result = result.substring(index);
                            JSONObject jsonObject= new JSONObject(result);
                            resultcode=jsonObject.getInt("code");
                            if(resultcode==100){
                                JSONArray followerArray = (JSONArray)jsonObject.get("result");
                                for(int i=0;i<followerArray.length();i++){
                                    JSONObject tmp = (JSONObject)followerArray.get(i);//인덱스 번호로 접근해서 가져온다.
                                    followingCount++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

}
