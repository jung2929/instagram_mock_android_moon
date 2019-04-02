package com.example.myinstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.data.Comment;
import com.example.myinstagram.ImageFragment;
import com.example.myinstagram.R;
import com.example.myinstagram.data.TimeLine;
import com.example.myinstagram.activitys.CommentActivity;
import com.pm10.library.CircleIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myinstagram.activitys.MainActivity.myProfileUrl;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private ArrayList<TimeLine> items;

    public static FragmentManager fragmentManager;

    public static Context context;

    HashMap<Integer, Integer> mViewPagerState = new HashMap<>();

    ArrayList<String> imageList = new ArrayList<>();

    String myProfileImageUrl;

    public FeedAdapter(FragmentManager fragmentManager, ArrayList<TimeLine> items, Context context, String myProfileImageUrl) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.context=context;
        this.myProfileImageUrl = myProfileImageUrl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_list, viewGroup, false);


        //txtName.setText(items.get(position));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final TimeLine item = items.get(position);

        viewHolder.txtName.setText(item.getPostName());
        //viewHolder.txtLocation.setText(item.getLocation());
        viewHolder.txtLike.setText(item.getLike()+"개");
        viewHolder.txtPostName.setText(item.getPostName());
        viewHolder.txtPostComment.setText(item.getPostComment());
        /////////댓글 있으면 피드에 댓글 2개까지 표시하기
        if(item.getCommentList().size()>0) {
            String lastComment = item.getPostComment2();
            for(int i=0; i<item.getCommentList().size(); i++){
                Comment comment = item.getCommentList().get(i);
                lastComment += "\n"+comment.getName()+" "+comment.getComment();
                if(i==1) break;
            }
            viewHolder.txtPostComment2.setText(lastComment);
        }
        else{
            viewHolder.txtPostComment2.setText(item.getPostComment2());
        }
        //viewHolder.txtTimeCheck.setText(item.getTimeCheck());

        Glide.with(context).load(item.getProfielUrl()).apply(new RequestOptions().centerCrop().circleCrop()).into(viewHolder.imgPostProfile); //본인 프로필 이미지 불러오기

        imageList = item.getImageUrl();

        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(fragmentManager);
        viewHolder.vp.setAdapter(bannerPagerAdapter);
        viewHolder.vp.setId(position+1);

        viewHolder.circleIndicator.setupWithViewPager(viewHolder.vp);

        if (mViewPagerState.containsKey(position)) {
            viewHolder.vp.setCurrentItem(mViewPagerState.get(position));
        }

        viewHolder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,CommentActivity.class);
                intent.putExtra("comment", item.getCommentList());
                intent.putExtra("index", item.getIndex());
                context.startActivity(intent);
            }
        });

        viewHolder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내 게시물인지 다른 사람 게시물인지 판단해야함//
                final ArrayList<String> ListItems = new ArrayList<>();
                ListItems.add("공유");
                ListItems.add("수정");
                ListItems.add("삭제");
                final CharSequence[] options =  ListItems.toArray(new String[ ListItems.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                //builder.setTitle("분류를 고르세요");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = options[pos].toString();

                        if(selectedText.equals("수정")){

                        }
                        else if(selectedText.equals("삭제")){
                            feedDelete(item.getIndex(), position);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        mViewPagerState.put(holder.getAdapterPosition(), holder.vp.getCurrentItem());
        super.onViewRecycled(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtLocation, txtLike, txtPostName, txtPostComment, txtPostComment2, txtTimeCheck;
        ImageView imgPostProfile, imgMore, imgComment;
        CircleIndicator circleIndicator;
        public ViewPager vp;

        public ViewHolder(View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtName);
            //txtLocation=itemView.findViewById(R.id.txtLocation);
            txtLike=itemView.findViewById(R.id.txtLike);
            txtPostName=itemView.findViewById(R.id.txtPostName);
            txtPostComment=itemView.findViewById(R.id.txtPostComment);
            txtPostComment2=itemView.findViewById(R.id.txtPostComment2);
            imgPostProfile=itemView.findViewById(R.id.imgPostProfile);
            imgMore=itemView.findViewById(R.id.imgMore);
            imgComment=itemView.findViewById(R.id.imgComment);
            //txtTimeCheck=itemView.findViewById(R.id.txtTimeCheck);

            vp = itemView.findViewById(R.id.viewPager);
            FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager);
            vp.setAdapter(fragmentAdapter);

            circleIndicator = itemView.findViewById(R.id.circle_indicator);
        }
    }

    public class BannerPagerAdapter extends FragmentPagerAdapter {
        public BannerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imgUrl", imageList.get(position));
            imageFragment.setArguments(bundle);

            return imageFragment;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }
    }

    private void feedDelete(final int postNum, final int listNum) {
        new Thread() {
            public void run() {
                HttpConnection httpConnection = new HttpConnection();
                SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                String jwt = pref.getString("jwt", "");
                httpConnection.feedDeleteApi(jwt, postNum, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        String data="";

                        result=response.body().string();
                        Log.d("삭제", result);
                        //int index = result.indexOf("{");
                        //result = result.substring(index);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("삭제 시도", json.toString());
                            resultcode = json.getInt("code");

                            if (resultcode == 100) {
                                Log.d("삭제 성공 ", data);
                                items.remove(listNum);
                                ((Activity)context).finish();
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