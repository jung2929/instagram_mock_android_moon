package com.example.myinstagram.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.data.Comment;
import com.example.myinstagram.R;
import com.example.myinstagram.data.TimeLine;
import com.example.myinstagram.adapters.TimeLineAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.myinstagram.activitys.LoginActivity.jwtToken;

public class MainActivity extends AppCompatActivity {

    static ArrayList<TimeLine> timeline = new ArrayList<>();
    public static String myProfileUrl;
    public static String myName;
    public static String myId;
    public static String myIntro;

    private HttpConnection httpConnection;
    private String jwt;

    int updateCount=1;
    int feedCount=0;
    Boolean load=true;

    ImageView imgHome, imgSearch, imgPost, imgHeart, imgProfile;
    RecyclerView feedList;
    LinearLayoutManager mLayoutManager;
    static TimeLineAdapter timeLineAdapter;
    PullRefreshLayout pullRefreshLayout;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        jwt = pref.getString("jwt", "");
        Log.d("로그인토큰 이어받음", jwt);
        init();
        myInfoLoad();
        follow("sqrd1234");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                //mFirebaseDatabase.getReference("users").child(userData.userEmailID).setValue(userData);
                Log.e("토큰",newToken);
            }
        });

        final String url = "https://fcm.googleapis.com/fcm/send";
        final String parameters = "{\"data\" : {\"message\" : {\"title\":\"test\",\"content\":\"test content\",\"imgUrl\":\"\",\"link\":\"\"}},\"to\" : \"\n" +
                "fUJIsG0I6GQ:APA91bEKaOBiQWmeCV9S-oPTHha0vVLeFrYe3Z8Nka6aVCLpiHNbHwsUhcCWTSjpQ3AGWuKULbr4fUSxe5nKqpso828QW00c2shy5DNWWjPGdDoHSToipkVJWSPRvXxZH-eyFTPdqVih\"}";


        //fmcPushApi2("", "제목", "내용");

    }

    @Override
    protected void onStart() {
        super.onStart();
        timeline.clear();
        feedLoad(0);

    }

    public void updateFeed(int num){
        //서버를통해 업데이트
        //서버에서 10개정도만 불러와서 리스트에 이어붙임
        addInstagramFeed(num*10);
        //timeLineAdapter.notifyDataSetChanged();
    }

    private void init(){
        httpConnection = new HttpConnection();

        imgHome = findViewById(R.id.imgHome);
        imgSearch = findViewById(R.id.imgSearch);
        imgPost = findViewById(R.id.imgPost);
        imgHeart = findViewById(R.id.imgHeart);
        imgProfile = findViewById(R.id.imgProfile);

        feedList = (RecyclerView) findViewById(R.id.TimeLine);
        mLayoutManager = new LinearLayoutManager(this);
        feedList.setLayoutManager(mLayoutManager);
        timeLineAdapter = new TimeLineAdapter(getSupportFragmentManager(), timeline, this);
        feedList.setAdapter(timeLineAdapter);

        feedList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!feedList.canScrollVertically(-1)) {
                    //최상단
                } else if (!feedList.canScrollVertically(1)) {
                    //최하단
                    if(load) {
                        Log.d("최하단 도달", timeline.size() + "");
                        feedLoad(timeline.size());
                        //addInstagramFeed(timeline.size());
                        load=false;
                    }
                } else {

                }
            }
        });


        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        // listen refresh event
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //로딩작업수행
                //저장된 피드 모두지우고 다시 서버에 재요청
                timeline.clear();
                feedLoad(0);
                //InstagramFeed(0);
                //pullRefreshLayout.setRefreshing(false);//로딩 멈추기
            }

        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,MyPageActivity.class);
                startActivityForResult(intent, 150);
            }
        });
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,ImageSelectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 150:
                    Intent intent= new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();  //로그아웃했을때
                    break;
            }
        }
    }

    private void myInfoLoad() {
        new Thread() {
            public void run() {
                httpConnection.userInfo(myId, new Callback(){
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
                                myId=(json.getJSONArray("data").getJSONObject(0).getString("user_id"));
                                myName=(json.getJSONArray("data").getJSONObject(0).getString("name"));
                                myIntro=(json.getJSONArray("data").getJSONObject(0).getString("introduction"));
                                myProfileUrl=(json.getJSONArray("data").getJSONObject(0).getString("profileImage"));
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        // UI변경하는 쓰레드
                                        Glide.with(context).load(myProfileUrl).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.default_image)).into(imgProfile);
                                    }
                                });
                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                jwt = pref.getString("jwt", "");
                                feedLoad(0);
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

    private void InstagramFeed(final int postNum) {
        timeline.clear();
        feedCount=0;
        Thread feedThread =
                new Thread() {
                    public void run() {
                        httpConnection.instagramFeed(jwt, postNum, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result;
                                int resultcode = 0;
                                String data = "";
                                result = response.body().string();
                                Log.d("피드조회 웨안대 ", result);
                                //int index = result.indexOf("{");
                                //result = result.substring(index);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(result);
                                    // Log.d("피드조회시도", json.toString());
                                    resultcode = json.getInt("code");

                                    data = json.getJSONArray("data").toString();
                                    // Log.d("피드data", data);

                                    if (resultcode == 100) {
                                        Log.d("피드조회 성공 ", data);
                                        for (int i = 0; i < json.getJSONArray("data").length(); i++) {
                                            final JSONObject feed = json.getJSONArray("data").getJSONObject(i);
                                            final String writer = feed.getString("writer");
                                            String content = feed.getString("content");
                                            String content2;
                                            final String stringDate = feed.getString("date");
                                            final String picture = feed.getString("picture");
                                            final int like = feed.getInt("likes");
                                            final int feedNum = feed.getInt("postNumber");//////////////피드번호 조희
                                            if (content.contains("\n") && content.indexOf("\n") < content.length()) {
                                                int index = content.indexOf("\n");
                                                content2 = content.substring(index + 1);
                                                content = content.substring(0, index);
                                            } else {
                                                content = content;
                                                content2 = "";
                                            }
                                            final String c1 = content;
                                            final String c2 = content2;
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
                                            final Date date = dateFormat.parse(stringDate);

                                            ///글쓴이의 프로필url 불러오기
                                            Thread profileUrlThread = new Thread() {
                                                public void run() {
                                                    httpConnection.userInfo(writer, new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            String result;
                                                            int resultcode = 0;
                                                            String data = "";

                                                            result = response.body().string();
                                                            Log.d("피드프로필 조회", result);
                                                            //int index = result.indexOf("{");
                                                            //result = result.substring(index);
                                                            JSONObject json = null;
                                                            try {
                                                                json = new JSONObject(result);
                                                                //Log.d("피드프로필 조회 시도", json.toString());
                                                                resultcode = json.getInt("code");

                                                                data = json.getJSONArray("data").toString();
                                                                Log.d("피드프로필 조회 시도", data);

                                                                if (resultcode == 100) {
                                                                    Log.d("피드프로필 조회 성공 ", data);
                                                                    String url = (json.getJSONArray("data").getJSONObject(0).getString("profileImage"));
                                                                    TimeLine temp = new TimeLine(feedNum, url, writer, "??????", c1, c2, date, String.valueOf(like));
                                                                    temp.addImageUrl(picture);
                                                                    Log.d("의문 타임라인 사진"+feedNum, picture + " "+ feed.getString("picture"));
                                                                    timeline.add(temp);
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            };
                                            profileUrlThread.start();
                                            //profileUrlThread.join();
                                            profileUrlThread.sleep(100);

                                            //TimeLine temp = new TimeLine(profileUrl,writer, "??????", content, content2, date, String.valueOf(like));
                                            //temp.addImageUrl(picture);
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                // UI변경하는 쓰레드
                                                timeLineAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                };
        feedThread.start();
        try {
            feedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timeLineAdapter.notifyDataSetChanged();
        pullRefreshLayout.setRefreshing(false);//로딩 멈추기
    }


    public void addInstagramFeed(final int postNum) {
        Thread feedThread =
                new Thread() {
                    public void run() {
                        httpConnection.instagramFeed(jwt, postNum, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result;
                                int resultcode = 0;
                                String data = "";
                                result = response.body().string();
                                Log.d("피드조회 웨안대 ", result);
                                //int index = result.indexOf("{");
                                //result = result.substring(index);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(result);
                                    // Log.d("피드조회시도", json.toString());
                                    resultcode = json.getInt("code");

                                    data = json.getJSONArray("data").toString();
                                    // Log.d("피드data", data);

                                    if (resultcode == 100) {
                                        Log.d("피드조회 성공 ", data);
                                        for (int i = 0; i < json.getJSONArray("data").length(); i++) {
                                            final JSONObject feed = json.getJSONArray("data").getJSONObject(i);
                                            final String writer = feed.getString("writer");
                                            String content = feed.getString("content");
                                            String content2;
                                            final String stringDate = feed.getString("date");
                                            final String picture = feed.getString("picture");
                                            final int like = feed.getInt("likes");
                                            final int feedNum = feed.getInt("postNumber");//////////////피드번호 조희
                                            if (content.contains("\n") && content.indexOf("\n") < content.length()) {
                                                int index = content.indexOf("\n");
                                                content2 = content.substring(index + 1);
                                                content = content.substring(0, index);
                                            } else {
                                                content = content;
                                                content2 = "";
                                            }
                                            final String c1 = content;
                                            final String c2 = content2;
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
                                            final Date date = dateFormat.parse(stringDate);

                                            ///글쓴이의 프로필url 불러오기
                                            Thread profileUrlThread = new Thread() {
                                                public void run() {
                                                    httpConnection.userInfo(writer, new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            String result;
                                                            int resultcode = 0;
                                                            String data = "";

                                                            result = response.body().string();
                                                            Log.d("피드프로필 조회", result);
                                                            //int index = result.indexOf("{");
                                                            //result = result.substring(index);
                                                            JSONObject json = null;
                                                            try {
                                                                json = new JSONObject(result);
                                                                //Log.d("피드프로필 조회 시도", json.toString());
                                                                resultcode = json.getInt("code");

                                                                data = json.getJSONArray("data").toString();
                                                                Log.d("피드프로필 조회 시도", data);

                                                                if (resultcode == 100) {
                                                                    Log.d("피드프로필 조회 성공 ", data);
                                                                    String url = (json.getJSONArray("data").getJSONObject(0).getString("profileImage"));

                                                                    TimeLine temp = new TimeLine(feedNum, url, writer, "??????", c1, c2, date, String.valueOf(like));
                                                                    Log.d("의문 타임라인 사진", picture + " "+ feed.getString("picture"));
                                                                    temp.addImageUrl(picture);
                                                                    timeline.add(temp);
                                                                    feedCount++;
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            };
                                            profileUrlThread.start();
                                            //profileUrlThread.join();
                                            profileUrlThread.sleep(100);

                                            //TimeLine temp = new TimeLine(profileUrl,writer, "??????", content, content2, date, String.valueOf(like));
                                            //temp.addImageUrl(picture);
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                // UI변경하는 쓰레드
                                                timeLineAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                };
        feedThread.start();
        try {
            feedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //pullRefreshLayout.setRefreshing(false);//로딩 멈추기
        timeLineAdapter.notifyDataSetChanged();
    }


    public void feedLoad(final int postNum) {
        Thread feedThread =
                new Thread() {
                    public void run() {
                        httpConnection.instagramFeed(jwt, postNum, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String result;
                                int resultcode = 0;
                                String data = "";
                                result = response.body().string();
                                Log.d("피드조회 웨안대 ", result);
                                //int index = result.indexOf("{");
                                //result = result.substring(index);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(result);
                                    // Log.d("피드조회시도", json.toString());
                                    resultcode = json.getInt("code");

                                    data = json.getJSONArray("data").toString();
                                    // Log.d("피드data", data);

                                    if (resultcode == 100) {
                                        Log.d("피드조회 성공 ", data);
                                        for (int i = 0; i < json.getJSONArray("data").length(); i++) {
                                            if(json.getJSONArray("data").getJSONObject(i).getString("writer").equals("null")){
                                                Log.d("포스트넘 마지막 null들어옴", i+"번째");
                                                break;
                                            }
                                            final JSONObject feed = json.getJSONArray("data").getJSONObject(i);
                                            final String picture = feed.getString("picture");
                                            final String writer = feed.getString("writer");
                                            String content = feed.getString("content");
                                            String content2;
                                            final String stringDate = feed.getString("date");
                                            final int like = feed.getInt("likes");
                                            final int feedNum = feed.getInt("postNumber");//////////////피드번호 조희
                                            if (content.contains("\n") && content.indexOf("\n") < content.length()) {
                                                int index = content.indexOf("\n");
                                                content2 = content.substring(index + 1);
                                                content = content.substring(0, index);
                                            } else {
                                                content = content;
                                                content2 = "";
                                            }
                                            final String c1 = content;
                                            final String c2 = content2;
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
                                            final Date date = dateFormat.parse(stringDate);
                                            TimeLine temp = new TimeLine(picture, feedNum,"",writer, "??????", content, content2, date, String.valueOf(like));
                                            Log.d("picture에 들어가는 값", i + "번째 " + picture);
                                            temp.addImageUrl("http://thegear.mygoodnews.com/imgdata/thegear_co_kr/201809/201809050119966.png");
                                            //Log.d("picture에 들어가는 값(TimeLint객체값)", i + "번째 " + temp.toString());
                                            //temp.addImageUrl(picture);
                                            timeline.add(temp);
                                            Log.d("포스트넘 timeLine add하고 사이즈", timeline.size()+"");
                                        }
                                        for(int i=postNum; i<timeline.size(); i++) {
                                            Log.d("포스트넘", i+" "+timeline.size());
                                            profileImageLoad(timeline.get(i).getPostName(), i);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.d("포스트넘 에러캐치", e.toString());
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                };
        feedThread.start();
        try {
            feedThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void profileImageLoad(final String id, final int index){
        Thread profileUrlThread = new Thread() {
            public void run() {
                httpConnection.userInfo(id, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode = 0;
                        String data = "";

                        result = response.body().string();
                        Log.d("피드프로필 조회", result);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            resultcode = json.getInt("code");
                            data = json.getJSONArray("data").toString();
                            Log.d("피드프로필 조회 시도", data);

                            if (resultcode == 100) {
                                Log.d("피드프로필 조회 성공 ", data);
                                String url = (json.getJSONArray("data").getJSONObject(0).getString("profileImage"));
                                timeline.get(index).setProfielUrl(url);
                                feedCount++;
                                Log.d("마지막 피드까지 불러오는지 판단", index + " " +timeline.size());
                                if(index == timeline.size()-1){
                                    //마지막 피드까지 프로필 불러오면
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            // UI변경하는 쓰레드
                                            Log.d("마지막 피드까지 불러옴", "로딩멈춤, notify");
                                            load = true;
                                            pullRefreshLayout.setRefreshing(false);//로딩 멈추기
                                            timeLineAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        profileUrlThread.start();
    }

    public void fmcPushApi2(final String token, final String title, final String content){
        Thread fmcPushApi2 = new Thread() {
            public void run() {
                httpConnection.fmcPushApi2(token, title, content, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("fmc푸시 결과", response.toString());
                    }

                });
            }
        };
        fmcPushApi2.start();
    }

    private void follow(final String id) {
        new Thread() {
            public void run() {
                httpConnection.followApi(jwt, id, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d(TAG, "콜백오류:"+e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int resultcode=0;
                        try {
                            String result;
                            result=response.body().string();
                            Log.d("팔로우", "서버에서 응답한 Body:"+result);
                            JSONObject jsonObject= new JSONObject(result);
                            resultcode=jsonObject.getInt("code");
                            //Log.d("오토로그인", "서버에서 응답한 Body:"+jsonObject);
                            if(resultcode==100){
                                Log.d("팔로우", "팔로우 성공");
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
