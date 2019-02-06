package com.example.myinstagram.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.R;
import com.example.myinstagram.data.TimeLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.myinstagram.activitys.MainActivity.myId;
import static com.example.myinstagram.activitys.MainActivity.myIntro;
import static com.example.myinstagram.activitys.MainActivity.myName;

public class MyPageActivity extends AppCompatActivity {

    GridView gridview;
    GridAdapter adapter;
    Context context;
    ImageView imgHome, imgSearch, imgPost, imgHeart, imgMyProfile;
    TextView txtPostNum, txtFollower, txtFolloing, txtEditProfile, txtId, txtName, txtIntro;

    private String jwt;
    private String myProfileImageUrl;

    private HttpConnection httpConnection;
    //서버에서 본인정보, 팔로워, 팔로잉, 게시글수 등 가져와서 화면 초기화해줌
    //프로필사진, 아이디, 팔로워, 팔로잉, 개시글, 팔로우버튼(이 사람을 팔로우했는지 여부 판단)
    //이름, 소개글
    //피드 이미지리스트, 각 피드정보
    String profileUrl, id, name, intro;
    int postNum;
    static int followerCount;
    static int followingCount;

    ArrayList<String> feedImageUrlList = new ArrayList<>();
    ArrayList<TimeLine> myFeed = new ArrayList<>(); //서버를통해서 받아와야함 -> feedImageList에 이미지 url전달

    int gridviewH; // 그리드뷰안에 이미지의 높이를 1/3로 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        context=this;

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        serverLoad();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 300:
                    finish();
                    break;
                case 200:
                    finish();
                    break;
            }
        }
    }

    private void init(){
        httpConnection = new HttpConnection();

        adapter = new GridAdapter (getApplicationContext(), R.layout.grid_view, feedImageUrlList);
        gridview = (GridView)findViewById(R.id.gridview1);
        gridview.setAdapter(adapter);
        gridviewH = gridview.getHeight() / 3;

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭 이벤트 설정 -> 각 피드 상세보기
                    Toast.makeText(MyPageActivity.this,"클릭한 포지션: " + position +"총 크기: " + feedImageUrlList.size() , Toast.LENGTH_SHORT).show();
                //게시글 상세보기 액티비티 띄우기
                TimeLine clickFeed = myFeed.get(position);
                Intent intent= new Intent(MyPageActivity.this,FeedActivty.class);
                intent.putExtra("profile", clickFeed.getProfielUrl());
                intent.putExtra("imageList", clickFeed.getImageUrl());
                intent.putExtra("name", clickFeed.getPostName());
                intent.putExtra("location", clickFeed.getLocation());
                intent.putExtra("like", clickFeed.getLike());
                intent.putExtra("comment", clickFeed.getPostComment());
                intent.putExtra("comment2", clickFeed.getPostComment2());

                startActivityForResult(intent, 300);
            }
        });

        imgHome = findViewById(R.id.imgHome);
        imgSearch = findViewById(R.id.imgSearch);
        imgPost = findViewById(R.id.imgPost);
        imgHeart = findViewById(R.id.imgHeart);
        imgMyProfile = findViewById(R.id.imgMyProfile);
        txtPostNum = findViewById(R.id.txtPostNum);
        txtFollower=findViewById(R.id.txtFollower);
        txtFolloing =findViewById(R.id.txtFolloing);
        txtEditProfile=findViewById(R.id.txtEditProfile);

        txtId=findViewById(R.id.txtId);
        txtName=findViewById(R.id.txtName);
        txtIntro=findViewById(R.id.txtIntro);

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyPageActivity.this,ImageSelectActivity.class);
                startActivity(intent);
            }
        });

        txtFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyPageActivity.this,FollowerActivity.class);
                startActivityForResult(intent, 200);
            }
        });
        txtFolloing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyPageActivity.this,FollowerActivity.class);
                startActivityForResult(intent, 200);
            }
        });
        txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MyPageActivity.this,ProfileEditActivity.class);
                startActivity(intent);
            }
        });

        txtId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> ListItems = new ArrayList<>();
                ListItems.add("로그아웃");
                ListItems.add("취소");
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                //builder.setTitle("분류를 고르세요");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos].toString();

                        if(selectedText.equals("로그아웃")){
                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("jwt");
                            editor.commit();
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK,resultIntent);
                            finish();
                        }
                        else if(selectedText.equals("취소")){

                        }
                    }
                });
                builder.show();
            }
        });
    }

    void serverLoad(){
        //프로필사진, 아이디, 팔로워, 팔로잉, 개시글, 팔로우버튼(이 사람을 팔로우했는지 여부 판단)
        //이름, 소개글
        //피드 이미지리스트, 각 피드정보

        profileUrl = "https://media.treepla.net:447/project/7ac115de-ec05-4ca3-8ea7-f3b70a22a1dc.png";
        id="myj0113";
        name="문영진";
        intro="안녕";

        /////////////////////////팔로워, 팔로잉 숫자 계산/////////////////////
        followerCount=1;
        followingCount=1;
        followerLoad();
        followingLoad();
        ////////////////////////본인 타임라인 불러오기////////////////////////
        TimeLine temp = new TimeLine(profileUrl,"dudwls", "??????", "#스타벅스 #아메리카노", "줄띄위고 입력", new Date(), "15");
        temp.addImageUrl(profileUrl);
        myFeed.add(temp);
        feedImageUrlList.add(temp.getImageUrl().get(0));
        adapter.notifyDataSetChanged();//테스트용으로 계속추가해줌
        /////////////////////////게시글 숫자 계산////////////////////////
        postNum=feedImageUrlList.size();
        txtPostNum.setText(String.valueOf(postNum));
        //////////////////프로필 이미지 불러오기/////////////////////////
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        myProfileImageUrl = pref.getString("myProfileImageUrl", "");
        Glide.with(context).load(myProfileImageUrl).apply(new RequestOptions().centerCrop().circleCrop()).into(imgMyProfile);

        /////////////////아이디, 이름, 소개 불러오기//////////////////////////
        userInfoLoad();
        txtId.setText(myId);
        txtName.setText(myName);
        txtIntro.setText(myIntro);
        ////////////////////////////////////////////////////////////////////


        //int gridviewHeight=(postNum/3)*1/3;
        //gridview.getLayoutParams().height = gridview.getHeight()*gridviewHeight;
        //gridview.requestLayout();
    }

    class GridAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<String> feedImageUrlList;
        LayoutInflater inf;

        public GridAdapter(Context context, int layout, ArrayList<String> feedImageUrlList) {
            this.context = context;
            this.layout = layout;
            this.feedImageUrlList = feedImageUrlList;
            inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return feedImageUrlList.size();
        }

        @Override
        public Object getItem(int position) {
            return feedImageUrlList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inf.inflate(layout, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.feedImage);
            //iv.setImageResource(img[position]);
            int gridviewH = gridview.getHeight() / 3; // 그리드뷰안에 이미지의 높이를 1/3로 설정

            Glide.with(context).load(feedImageUrlList.get(position)).apply(new RequestOptions().override(gridviewH,gridviewH).centerCrop()).into(iv); //이미지 불러오기

             return convertView;
        }
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
                            Log.d("팔로워목록", result);
                            if(resultcode==100){
                                JSONArray followerArray = (JSONArray)jsonObject.get("result");
                                for(int i=0;i<followerArray.length();i++){
                                    JSONObject tmp = (JSONObject)followerArray.get(i);//인덱스 번호로 접근해서 가져온다.
                                    followerCount++;
                                }
                                runOnUiThread(new Runnable() { public void run() {
                                    // UI변경하는 쓰레드
                                    txtFollower.setText(String.valueOf(followerCount));
                                }
                                });

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
                                runOnUiThread(new Runnable() { public void run() {
                                    // UI변경하는 쓰레드
                                    txtFolloing.setText(String.valueOf(followingCount));
                                }
                                });
                            }
                            else if(resultcode==400){
                                runOnUiThread(new Runnable() { public void run() {
                                    // UI변경하는 쓰레드
                                    txtFolloing.setText(String.valueOf(1));
                                }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    private void userInfoLoad() {
        new Thread() {
            public void run() {
                httpConnection.userInfo("dudwls0113", new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        String data="";

                        result=response.body().string();
                        Log.d("유저정보조회", result);
                        //int index = result.indexOf("{");
                        //result = result.substring(index);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("유저정보조회시도", json.toString());
                            resultcode = json.getInt("code");

                            data = json.getJSONArray("data").toString();
                            Log.d("유저정보data", data);

                            if (resultcode == 100) {
                                Log.d("유저정보조회 성공 ", data);
                                //txtId.setText(json.getJSONArray("data").getJSONObject(0).getString("user_id"));
                                //txtName.setText(json.getJSONArray("data").getJSONObject(0).getString("name"));
                                //txtIntro.setText(json.getJSONArray("data").getJSONObject(0).getString("introduction"));
                                //Glide.with(context).load(myProfileImageUrl).apply(new RequestOptions().centerCrop().circleCrop()).into(imgMyProfile);
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

}
