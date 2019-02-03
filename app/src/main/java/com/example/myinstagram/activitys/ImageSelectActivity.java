package com.example.myinstagram.activitys;

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
import com.example.myinstagram.R;
import com.example.myinstagram.TimeLine;

import java.util.ArrayList;
import java.util.Date;

import gun0912.tedbottompicker.TedBottomPicker;

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
                int index = editComment.getText().toString().indexOf("\n");
                String comment1 = editComment.getText().toString().substring(0, index);
                String comment2 = editComment.getText().toString().substring(index+1);
                TimeLine newTimeLine = new TimeLine(myProfileUrl,myName, "", comment1, comment2, new Date(), "0");
                //for(int i=0; i<imageUri.size(); i++){
                //    newTimeLine.addIimageUri(imageUri.get(i));
               // }
                newTimeLine.addImageUrl("http://www.topstarnews.net/news/photo/201805/416010_62936_104.jpg");
                newTimeLine.addImageUrl("https://ppss.kr/wp-content/uploads/2016/11/album-540x540.jpg");
                timeline.add(newTimeLine);
                timeLineAdapter.notifyDataSetChanged();
                finish();
            }
        });
    }
}
