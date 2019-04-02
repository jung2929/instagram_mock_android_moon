package com.example.myinstagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myinstagram.adapters.FollowerAdapter;
import com.example.myinstagram.data.Follower;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myinstagram.activitys.MainActivity.myId;


public class FollowingFragment extends Fragment {

    ArrayList<Follower> commentList;
    RecyclerView followerRecyclerView;
    LinearLayoutManager mLayoutManager;
    FollowerAdapter followerAdapter;

    ArrayList<Follower> followingrList;
    ArrayList<String> followerList;
    ArrayList<String> followingProfileList;
    ArrayList<String> followingIdList;

    Context context;
    private HttpConnection httpConnection;


    int followerCount=1;
    int followingCount=1;

    String jwt;

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);
        context = getActivity();
        httpConnection = new HttpConnection();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        followingrList=new ArrayList<>();
        followerList=new ArrayList<>();
        followingProfileList=new ArrayList<>();
        followingIdList = new ArrayList<>();

        //서버에서 팔로워 요청해서 리스트에 담기
        //followingrList.add(new Follower("", "왜안대", true));
        followerLoad();


        followerRecyclerView = (RecyclerView)view.findViewById(R.id.followerList);
        mLayoutManager = new LinearLayoutManager(context);
        followerRecyclerView.setLayoutManager(mLayoutManager);
        followerAdapter = new FollowerAdapter(followingrList, context);
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
                                    followerList.add(id);
                                    followerCount++;
                                }
                                followingLoad();
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
                                Log.d("팔로잉목록 코드100", result);

                                for(int i=0;i<followerArray.length();i++){
                                    JSONObject tmp = (JSONObject)followerArray.get(i);//인덱스 번호로 접근해서 가져온다.
                                    String id = (String)tmp.get("following");
                                    if(id.equals(myId)){
                                        continue;
                                    }
                                    followingIdList.add(index,id);
                                    //팔로워들 프로필 이미지 서버에서 로드
                                    userInfoLoad(id, i);
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


    private void userInfoLoad(final String id, final int index) {
        new Thread() {
            public void run() {
                httpConnection.userInfo(id, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        JSONArray data;

                        result=response.body().string();
                        Log.d("유저정보조회", result);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            resultcode = json.getInt("code");
                            data = json.getJSONArray("data");
                            Log.d("유저정보data", data.toString());

                            if (resultcode == 100) {
                                Log.d("유저정보조회 성공 ", data.toString());
                                String url = data.getJSONObject(0).getString("profileImage");
                                String id = data.getJSONObject(0).getString("user_id");
                                //followingProfileList.add(index,url);
                                if(isYouFollowMe(id)){
                                    Follower following = new Follower(url, id, true);
                                    followingrList.add(following);
                                }
                                else{
                                    Follower following = new Follower(url, id, true);
                                    followingrList.add(following);
                                }
                                followerCount++;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 이 곳에 UI작업을 한다
                                        followerAdapter.notifyDataSetChanged();
                                    }
                                });
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


    private Boolean isYouFollowMe(String id){
            boolean followMe;
            if(followerList.contains(id)){
                return true;
            }
            else{
                return false;
            //Follower following = new Follower(url, id, followMe);
            //followingrList.add(following);

        }
    }

}
