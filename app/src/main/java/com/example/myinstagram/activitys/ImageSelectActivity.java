package com.example.myinstagram.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.R;
import com.example.myinstagram.StaticMethod;
import com.example.myinstagram.data.TimeLine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.myinstagram.activitys.MainActivity.myName;
import static com.example.myinstagram.activitys.MainActivity.myProfileUrl;
import static com.example.myinstagram.activitys.MainActivity.timeLineAdapter;
import static com.example.myinstagram.activitys.MainActivity.timeline;

public class ImageSelectActivity extends AppCompatActivity {
    Context context;
    ImageView imgSelect;
    EditText editComment;
    TextView txtShare;
    TedBottomPicker bottomSheetDialogFragment;
    ArrayList<Uri> imageUri=new ArrayList<>();

    private HttpConnection httpConnection;
    private String jwt;

    String imageUrl;
    String content; //서버에 올리는 글

    String comment1; //로컬용 글 줄로 구분
    String comment2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        context = this;
        init();
        bottomSheetDialogFragment = new TedBottomPicker.Builder(ImageSelectActivity.this)
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        // here is selected uri list
                        imageUri = uriList;
                        Glide.with(context).load(uriList.get(0)).apply(new RequestOptions().centerCrop()).into(imgSelect); //이미지 불러오기
                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("선택")
                .setEmptySelectionText("No Select")
                .showGalleryTile(false)
                .setSelectMinCount(1)
                .setSelectMinCountErrorText("1장 이상 선택해주세요")
                .create();

        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

    private void init(){
        httpConnection = new HttpConnection();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        imgSelect=findViewById(R.id.imgSelect);
        editComment=findViewById(R.id.editComment);
        txtShare=findViewById(R.id.txtShare);
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogFragment.show(getSupportFragmentManager());
            }
        });
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = editComment.getText().toString();

                if(content.contains("\n")){
                    int index = editComment.getText().toString().indexOf("\n");
                    comment1 = editComment.getText().toString().substring(0, index);
                    comment2 = editComment.getText().toString().substring(index+1);
                }
                else{
                    comment1=editComment.getText().toString();
                    comment2="";
                }

                //////////서버에 새 글 올리기//////////////////////////////////////
                imageUpload(StaticMethod.getPath(context, imageUri.get(0)));  //일단 이미지는 한장만 업로드
                ///////////////////////////////////////////////////////////////////


            }
        });
    }


    private void postUpload() {
        new Thread() {
            public void run() {
                httpConnection.postApi(jwt, content, imageUrl, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        String data="";
                        Log.d("게시글업로드할 내용과 url", content +"  "+ imageUrl);
                        result=response.body().string();
                        Log.d("게시글업로드", result);
                        int index = result.indexOf("{");
                        result = result.substring(index);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("게시글업로드 시도", json.toString());
                            resultcode=json.getInt("code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(resultcode==100){
                            Log.d("게시글업로드 성공 ", data);
                            finish();
                            ///////게시글까지 다완료되면 테스트용
                            //TimeLine newTimeLine = new TimeLine(myProfileUrl,myName, "", comment1, comment2, new Date(), "0"); // 로컬테스트용
                            //newTimeLine.addImageUrl(imageUrl);// 로컬테스트용
                            //newTimeLine.addImageUrl("https://ppss.kr/wp-content/uploads/2016/11/album-540x540.jpg");// 로컬테스트용
                            //timeline.add(newTimeLine);// 로컬테스트용
                            //timeLineAdapter.notifyDataSetChanged();// 로컬테스트용
                        }
                    }
                });
            }
        }.start();
    }


    private void imageUpload(final String filePath) {
        new Thread() {
            public void run() {
                httpConnection.imageUpload(jwt, filePath, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        String url="";

                        result=response.body().string();
                        Log.d("이미지업로드실패 1" , result);
                        int index = result.indexOf("{");
                       // result = result.substring(index);
                       // JSONObject json = null;
                        try {
                            JSONObject json= new JSONObject(result);
                            Log.d("이미지업로드실패", json.toString());
                            resultcode=json.getInt("code");
                            url=json.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(resultcode==100){
                            Log.d("이미지업로드성공 ", url);
                            imageUrl=url; //이 액티비티용

                            postUpload(); //이미지 업로드 성공하면 글 업로드
                        }
                    }
                });
            }
        }.start();
    }
}
