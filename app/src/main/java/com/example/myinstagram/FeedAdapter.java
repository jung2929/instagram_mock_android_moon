package com.example.myinstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private ArrayList<TimeLine> items;

    public static FragmentManager fragmentManager;

    public static Context context;

    HashMap<Integer, Integer> mViewPagerState = new HashMap<>();

    ArrayList<String> imageList = new ArrayList<>();



    public FeedAdapter(FragmentManager fragmentManager, ArrayList<TimeLine> items, Context context) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_list, viewGroup, false);


        //txtName.setText(items.get(position));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        TimeLine item = items.get(position);

        viewHolder.txtName.setText(item.getPostName());
        viewHolder.txtLocation.setText(item.getLocation());
        viewHolder.txtLike.setText(item.getLike());
        viewHolder.txtPostName.setText(item.getPostName());
        viewHolder.txtPostComment.setText(item.getPostComment());
        viewHolder.txtPostComment2.setText(item.getPostComment2());
        //viewHolder.txtTimeCheck.setText(item.getTimeCheck());
        Glide.with(context).load(item.getProfielUrl()).apply(new RequestOptions().centerCrop().circleCrop()).into(viewHolder.imgPostProfile); //이미지 불러오기

        imageList = item.getImageUrl();

        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(fragmentManager);
        viewHolder.vp.setAdapter(bannerPagerAdapter);
        viewHolder.vp.setId(position+1);

        if (mViewPagerState.containsKey(position)) {
            viewHolder.vp.setCurrentItem(mViewPagerState.get(position));
        }

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
        ImageView imgPostProfile;
        public ViewPager vp;

        public ViewHolder(View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtName);
            txtLocation=itemView.findViewById(R.id.txtLocation);
            txtLike=itemView.findViewById(R.id.txtLike);
            txtPostName=itemView.findViewById(R.id.txtPostName);
            txtPostComment=itemView.findViewById(R.id.txtPostComment);
            txtPostComment2=itemView.findViewById(R.id.txtPostComment2);
            imgPostProfile=itemView.findViewById(R.id.imgPostProfile);
            //txtTimeCheck=itemView.findViewById(R.id.txtTimeCheck);

            vp = itemView.findViewById(R.id.viewPager);
            FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager);
            vp.setAdapter(fragmentAdapter);
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

}