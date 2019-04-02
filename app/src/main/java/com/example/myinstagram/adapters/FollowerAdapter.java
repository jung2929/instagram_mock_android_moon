package com.example.myinstagram.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.myinstagram.activitys.YourPageActivity;
import com.example.myinstagram.data.Follower;
import com.example.myinstagram.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    private ArrayList<Follower> items;

    public static FragmentManager fragmentManager;

    public static Context context;

    HttpConnection httpConnection;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Follower item = items.get(position);
        viewHolder.txtName.setText(item.getName());
        //viewHolder.txtTime.setText(item.getTime());팔로우를 한지 안한지 판단후 버튼 설정하기
        Glide.with(context).load(item.getProfileUrl()).apply(new RequestOptions().error(R.drawable.camera).centerCrop().circleCrop()).into(viewHolder.imgProfile); //이미지 불러오기
        if(item.getYouFollowMe()){
            viewHolder.txtButton.setText("팔로잉");
            viewHolder.txtButton.setBackgroundResource(R.drawable.teduri);
            viewHolder.txtButton.setTextColor(R.color.black);
        }
        else{
            //viewHolder.txtButton.setText("팔로잉");
        }

        viewHolder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,YourPageActivity.class);
                intent.putExtra("id", item.getName());
                //intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });

        viewHolder.txtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.txtButton.getText().toString().equals("팔로우")){
                    httpConnection = new HttpConnection();
                    follow(item.getName());
                    viewHolder.txtButton.setText("팔로잉");
                    viewHolder.txtButton.setBackgroundResource(R.drawable.teduri);
                    viewHolder.txtButton.setTextColor(R.color.black);
                }
            }
        });
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

    private void follow(final String id) {
        new Thread() {
            public void run() {
                SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
                String jwt = pref.getString("jwt", "");
                httpConnection.followApi(jwt, id, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d(TAG, "콜백오류:"+e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int resultcode=0;
                        try {
                            String result;
                            result=response.body().string();
                            Log.d("팔로우", "서버에서 응답한 Body:"+result);
                            JSONObject jsonObject= new JSONObject(result);
                            resultcode=jsonObject.getInt("code");
                            //Log.d("오토로그인", "서버에서 응답한 Body:"+jsonObject);
                            if(resultcode==100){
                                Log.d("팔로우", "팔로우 성공");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

}