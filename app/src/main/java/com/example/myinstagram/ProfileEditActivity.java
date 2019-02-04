package com.example.myinstagram;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.activitys.ImageSelectActivity;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

public class ProfileEditActivity extends AppCompatActivity {

    EditText editName, editId, editIntro, editEmail, editPhone, editSex;
    TextView txtProfileChange, txtSave, txtCancle;
    ImageView imgMyProfile;

    TedBottomPicker bottomSheetDialogFragment;
    ArrayList<Uri> imageUri=new ArrayList<>();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        context=this;
        init();
    }

    private void init(){
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
                                //서버에 저장?//
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
}
