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


public class FollowerFragment extends Fragment {

    ArrayList<Follower> commentList;
    RecyclerView followerRecyclerView;
    LinearLayoutManager mLayoutManager;
    FollowerAdapter followerAdapter;

    ArrayList<Follower> followerList;
    ArrayList<String> followingList;
    ArrayList<String> followerProfileList;
    ArrayList<String> followerIdList;

    Context context;
    private HttpConnection httpConnection;

    final int FOLLOWER_MODE=1;
    final int FOLLOING_MODE=2;

    int followerCount=1;
    int followingCount=1;

    int modeVal;

    String jwt;
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);
        context = getActivity();
        httpConnection = new HttpConnection();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        followerList=new ArrayList<>();
        followingList= new ArrayList<>();
        followerProfileList=new ArrayList<>();
        followerIdList = new ArrayList<>();


        //서버에서 팔로워 요청해서 리스트에 담기
        followingLoad();
        followerLoad();
        modeVal = FOLLOWER_MODE;

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
                                    if(id.equals(myId)){
                                        continue;
                                    }
                                    followerIdList.add(id);
                                    //팔로워들 프로필 이미지 서버에서 로드
                                    userInfoLoad(id, i);
                                    //String url = "https://scontent-hkg3-1.xx.fbcdn.net/v/t1.0-9/49609871_2368471986765652_3343732018184716288_n.jpg?_nc_cat=100&_nc_ht=scontent-hkg3-1.xx&oh=c2dce3e0e0466ecfc10d35b823407fc8&oe=5CF62F61";
                                    //Follower follower = new Follower(url, id, true);
                                    //followerList.add(follower);
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
                            Log.d("왜 안대는 팔로잉목록", result);
                            if(resultcode==100){
                                JSONArray followerArray = (JSONArray)jsonObject.get("result");
                                for(int i=0;i<followerArray.length();i++){
                                    JSONObject tmp = (JSONObject)followerArray.get(i);//인덱스 번호로 접근해서 가져온다.
                                    String id = (String)tmp.get("following");
                                    followingList.add(id);
                                    Log.d("왜 안대는 팔로잉 아이디 목록에 추가: ", id);

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
                        //int index = result.indexOf("{");
                        //result = result.substring(index);
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

                                //followerProfileList.add(index,url);
                                if(amIFollowYou(id)){
                                    Follower following = new Follower(url, id, true);
                                    followerList.add(following);
                                }
                                else{
                                    Follower following = new Follower(url, id, false);
                                    followerList.add(following);
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

    private Boolean amIFollowYou(String id){
        //String id = followerIdList.get(index);
        boolean followYou;
        Log.d("팔로잉 아이디 목록: ", followingList.toString());
        if(followingList.contains(id)){
            Log.d("팔로잉 하고있음: ", id);
            return true;
        }
        else{
            return false;
            //Follower following = new Follower(url, id, followMe);
            //followingrList.add(following);

        }
    }

}
