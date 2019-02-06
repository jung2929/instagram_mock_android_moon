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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileEditActivity extends AppCompatActivity {

    EditText editName, editId, editIntro, editEmail, editPhone, editSex;
    TextView txtProfileChange, txtSave, txtCancle;
    ImageView imgMyProfile;
    private HttpConnection httpConnection;
    String jwt;
    String filePath;

    TedBottomPicker bottomSheetDialogFragment;
    ArrayList<Uri> imageUri=new ArrayList<>();
    Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        context=this;

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        jwt = pref.getString("jwt", "");
        filePath = pref.getString("myProfileImageUrl", "");

        init();
    }

    private void init(){
        httpConnection = new HttpConnection();

        editName=findViewById(R.id.editName);
        editId=findViewById(R.id.editId);
        editIntro=findViewById(R.id.editIntro);
        editEmail=findViewById(R.id.editEmail);
        editSex=findViewById(R.id.editEmail);
        editPhone=findViewById(R.id.editEmail);

        txtProfileChange=findViewById(R.id.txtProfileChange);
        txtSave=findViewById(R.id.txtSave);
        txtCancle=findViewById(R.id.txtCancle);

        imgMyProfile=findViewById(R.id.imgMyProfile);
        Glide.with(context).load(filePath).apply(new RequestOptions().centerCrop().circleCrop()).into(imgMyProfile); //기존 프로필 이미지 불러오기


        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버에 저장?//
                Log.d("이미지경로", filePath);
                imageUpload();
                //다른 프로필정보도 수정하기
                finish();
            }
        });

        txtProfileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogFragment = new TedBottomPicker.Builder(ProfileEditActivity.this)
                        .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                            @Override
                            public void onImagesSelected(ArrayList<Uri> uriList) {
                                // here is selected uri list
                                imageUri = uriList;
                                Glide.with(context).load(uriList.get(0)).apply(new RequestOptions().centerCrop().circleCrop()).into(imgMyProfile); //이미지 불러오기
                                filePath = StaticMethod.getPath(context, imageUri.get(0));

                            }
                        })
                        .setPeekHeight(1600)
                        .showTitle(false)
                        .setCompleteButtonText("선택")
                        .setEmptySelectionText("No Select")
                        .showGalleryTile(false)
                        .setSelectMinCount(1)
                        .setSelectMaxCount(1)
                        .setSelectMinCountErrorText("사진을 선택해주세요")
                        .create();

                bottomSheetDialogFragment.show(getSupportFragmentManager());
            }
        });



    }


    private void imageUpload() {
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
                        int index = result.indexOf("{");
                        result = result.substring(index);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("이미지업로드실패", json.toString());
                            resultcode=json.getInt("code");
                            url=json.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(resultcode==100){
                            Log.d("이미지업로드성공 ", url);
                            editor.putString("myProfileImageUrl", url);
                            editor.commit();
                        }
                    }
                });
            }
        }.start();
    }


}
