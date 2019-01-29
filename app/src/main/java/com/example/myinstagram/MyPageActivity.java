package com.example.myinstagram;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.sql.Time;
import java.util.ArrayList;

public class MyPageActivity extends AppCompatActivity {

    GridView gridview;
    Context context;

    ArrayList<TimeLine> myFeed = new ArrayList<>(); //서버를통해서 받아와야함 -> feedImageList에 이미지 url전달

    //서버에서 본인정보, 팔로워, 팔로잉, 게시글수 등 가져와서 화면 초기화해줌

    //int feedImageList[] = {R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image, R.drawable.image,
    //        R.drawable.image, R.drawable.image, R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image

    ArrayList<String> feedImageUrlList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        context=this;

        GridAdapter adapter = new GridAdapter (getApplicationContext(), R.layout.grid_view, feedImageUrlList);

        gridview = (GridView)findViewById(R.id.gridview1);
        gridview.setAdapter(adapter);

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

                startActivity(intent);
            }
        });

        String tempProfileUrl = "http://stackhouse.s3.amazonaws.com/b2ae375b60d494290d29b56dd1325135_image.png";
    TimeLine temp = new TimeLine(tempProfileUrl,"dudwls", "??????", "#스타벅스 #아메리카노", "줄띄위고 입력", "1분전", "15");
        temp.addImageUrl(tempProfileUrl);
        myFeed.add(temp);
        feedImageUrlList.add(temp.getImageUrl().get(0));
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

}
