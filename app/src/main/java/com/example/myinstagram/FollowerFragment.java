package com.example.myinstagram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myinstagram.activitys.LoginActivity;
import com.example.myinstagram.activitys.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.myinstagram.activitys.LoginActivity.jwt;

public class FollowerFragment extends Fragment {

    ArrayList<Follower> commentList;
    RecyclerView followerRecyclerView;
    LinearLayoutManager mLayoutManager;
    FollowerAdapter followerAdapter;
    ArrayList<Follower> followerList;

    Context context;
    private HttpConnection httpConnection;

    final int FOLLOWER_MODE=1;
    final int FOLLOING_MODE=2;

    int followerCount=1;
    int followingCount=1;
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);
        context = getActivity();
        httpConnection = new HttpConnection();

        followerList=new ArrayList<>();
        String url = "https://scontent-hkg3-1.xx.fbcdn.net/v/t1.0-9/49609871_2368471986765652_3343732018184716288_n.jpg?_nc_cat=100&_nc_ht=scontent-hkg3-1.xx&oh=c2dce3e0e0466ecfc10d35b823407fc8&oe=5CF62F61";
        followerList.add(new Follower(url,"gsssni", true));

        Bundle args = getArguments();
        final int mode = args.getInt("mode");
        //서버에서 팔로워 요청해서 리스트에 담기

        if(mode == FOLLOWER_MODE) {
            followerLoad();
        }
        else if(mode == FOLLOING_MODE) {
            followingLoad();
        }

        followerRecyclerView = (RecyclerView)view.findViewById(R.id.followerList);
        mLayoutManager = new LinearLayoutManager(context);
        followerRecyclerView.setLayoutManager(mLayoutManager);
        followerAdapter = new FollowerAdapter(followerList, context);
        followerRecyclerView.setAdapter(followerAdapter);

        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
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
                                    String id = (String)tmp.get("my_id");
                                    //임시 이미지 url
                                    String url = "https://scontent-hkg3-1.xx.fbcdn.net/v/t1.0-9/49609871_2368471986765652_3343732018184716288_n.jpg?_nc_cat=100&_nc_ht=scontent-hkg3-1.xx&oh=c2dce3e0e0466ecfc10d35b823407fc8&oe=5CF62F61";
                                    Follower follower = new Follower(url, id, true);
                                    followerList.add(follower);
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
                            Log.d("팔로잉목록", result);
                            if(resultcode==100){
                                JSONArray followerArray = (JSONArray)jsonObject.get("result");
                                for(int i=0;i<followerArray.length();i++){
                                    JSONObject tmp = (JSONObject)followerArray.get(i);//인덱스 번호로 접근해서 가져온다.
                                    String id = (String)tmp.get("my_id");
                                    //임시 이미지 url
                                    String url = "https://scontent-hkg3-1.xx.fbcdn.net/v/t1.0-9/49609871_2368471986765652_3343732018184716288_n.jpg?_nc_cat=100&_nc_ht=scontent-hkg3-1.xx&oh=c2dce3e0e0466ecfc10d35b823407fc8&oe=5CF62F61";
                                    Follower follower = new Follower(url, id, true);
                                    followerList.add(follower);
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
