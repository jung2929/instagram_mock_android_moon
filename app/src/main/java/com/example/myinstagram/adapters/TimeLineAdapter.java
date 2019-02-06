package com.example.myinstagram.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.myinstagram.data.Comment;
import com.example.myinstagram.ImageFragment;
import com.example.myinstagram.R;
import com.example.myinstagram.data.TimeLine;
import com.example.myinstagram.activitys.CommentActivity;
import com.example.myinstagram.activitys.YourPageActivity;
import com.pm10.library.CircleIndicator;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private ArrayList<TimeLine> items;

    public static FragmentManager fragmentManager;

    public static Context context;

    HashMap<Integer, Integer> mViewPagerState = new HashMap<>();

    ArrayList<String> imageList[];
    ArrayList<Integer> imageCount = new ArrayList<>();

    public TimeLineAdapter(FragmentManager fragmentManager, ArrayList<TimeLine> items, Context context) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.context=context;

        imageList = new ArrayList[items.size()];
        for(int i=0; i<items.size(); i++){
            imageList[i] = new ArrayList<String>();
            imageCount.add(items.get(i).getImageUrl().size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timeline_list, viewGroup, false);


        //txtName.setText(items.get(position));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final TimeLine item = items.get(position);

        viewHolder.txtName.setText(item.getPostName());
//        viewHolder.txtLocation.setText(item.getLocation());
        viewHolder.txtLike.setText(item.getLike()+"개");
        viewHolder.txtPostName.setText(item.getPostName());
        viewHolder.txtPostComment.setText(item.getPostComment());
        //viewHolder.txtTimeCheck.setText(item.getTimeCheck());

        /////////댓글 있으면 피드에 댓글 2개까지 표시하기
        if(item.getCommentList().size()>0) {
            String lastComment = item.getPostComment2();
            for(int i=0; i<item.getCommentList().size(); i++){
                Comment comment = item.getCommentList().get(i);
                lastComment += "\n"+comment.getName()+" "+comment.getComment();
                if(i==1) break;
            }
            viewHolder.txtPostComment2.setText(lastComment);
            Log.d("lastComment", lastComment);
        }
        else{
            viewHolder.txtPostComment2.setText(item.getPostComment2());
        }
        Glide.with(context).load(item.getProfielUrl()).apply(new RequestOptions().centerCrop().circleCrop()).into(viewHolder.imgPostProfile); //이미지 불러오기

        //imageList[position] = item.getImageUrl();

        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(fragmentManager, position);
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
                intent.putExtra("index", position);
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
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                //builder.setTitle("분류를 고르세요");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos].toString();
                        //Toast.makeText(MainActivity.this, selectedText, Toast.LENGTH_SHORT).show();
                        if(selectedText.equals("수정")){

                        }
                        else if(selectedText.equals("삭제")){

                        }
                    }
                });
                builder.show();
            }
        });

        viewHolder.imgPostProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,YourPageActivity.class);
                //intent.putExtra("comment", item.getCommentList());
                //intent.putExtra("index", position);
                context.startActivity(intent);
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
        ImageView imgPostProfile, imgComment, imgMore;
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
            //txtTimeCheck=itemView.findViewById(R.id.txtTimeCheck);
            imgComment=itemView.findViewById(R.id.imgComment);
            imgMore=itemView.findViewById(R.id.imgMore);

            vp = itemView.findViewById(R.id.viewPager);
            FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager);
            vp.setAdapter(fragmentAdapter);

            circleIndicator = itemView.findViewById(R.id.circle_indicator);
        }
    }

    public class BannerPagerAdapter extends FragmentPagerAdapter {
        int index;
        public BannerPagerAdapter(FragmentManager fm, int index) {
            super(fm);
            this.index=index;
        }

        @Override
        public Fragment getItem(int position) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imgUrl", items.get(index).getImageUrl().get(position));
            imageFragment.setArguments(bundle);
            return imageFragment;
        }

        @Override
        public int getCount() {
            return items.get(index).getImageUrl().size();
            //return imageCount.get(index);
        }
    }

}