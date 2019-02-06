package com.example.myinstagram.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myinstagram.data.Follower;
import com.example.myinstagram.R;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    private ArrayList<Follower> items;

    public static FragmentManager fragmentManager;

    public static Context context;


    public FollowerAdapter(ArrayList<Follower> items, Context context) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.follower_list, viewGroup, false);
        //txtName.setText(items.get(position));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Follower item = items.get(position);
        viewHolder.txtName.setText(item.getName());
        //viewHolder.txtTime.setText(item.getTime());팔로우를 한지 안한지 판단후 버튼 설정하기
        Glide.with(context).load(item.getProfileUrl()).apply(new RequestOptions().centerCrop().circleCrop()).into(viewHolder.imgProfile); //이미지 불러오기
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        //mViewPagerState.put(holder.getAdapterPosition(), holder.vp.getCurrentItem());
        super.onViewRecycled(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtButton;
        ImageView imgProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtName);
            txtButton=itemView.findViewById(R.id.txtButton);
            imgProfile=itemView.findViewById(R.id.imgProfile);
        }
    }

}