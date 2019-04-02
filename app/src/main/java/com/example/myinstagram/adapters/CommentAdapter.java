package com.example.myinstagram.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
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
import com.example.myinstagram.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Comment> items;

    public static FragmentManager fragmentManager;

    public static Context context;

    HashMap<Integer, Integer> mViewPagerState = new HashMap<>();

    ArrayList<String> imageList[];
    ArrayList<Integer> imageCount = new ArrayList<>();

    public CommentAdapter(FragmentManager fragmentManager, ArrayList<Comment> items, Context context) {
        this.items = items;
        this.fragmentManager = fragmentManager;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list, viewGroup, false);
        //txtName.setText(items.get(position));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Comment item = items.get(position);
        String comment = item.getName()+" "+item.getComment();
        viewHolder.txtComment.setText(comment);
        viewHolder.txtTime.setText(item.getTime());
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

        TextView txtComment, txtTime;
        ImageView imgProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            txtComment=itemView.findViewById(R.id.txtComment);
            txtTime=itemView.findViewById(R.id.txtTime);
            imgProfile=itemView.findViewById(R.id.imgProfile);
        }
    }

}