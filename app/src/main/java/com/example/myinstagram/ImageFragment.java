package com.example.myinstagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class ImageFragment extends Fragment {


    private static final String ARG_PARAM1 = "imgRes";
    Bitmap imageResource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long btnPressTime = 0;
                if (System.currentTimeMillis() > btnPressTime + 1000) {
                    btnPressTime = System.currentTimeMillis();
                    //  Toast.makeText(getApplicationContext(), "한번 더 터치하면 실행됩니다.",
                    //         Toast.LENGTH_SHORT).show();
                    return;
                }
                if (System.currentTimeMillis() <= btnPressTime + 500) {
                    Toast.makeText(getActivity(), "더블클릭", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //if (getArguments() != null) {
            Bundle args = getArguments();
            final String imageUrl = args.getString("imgUrl");
        Glide.with(this).load(imageUrl).apply(new RequestOptions().centerCrop()).into(imageView);
        Log.d("picture에 들어가서 표시되는 이미지",  ""+ imageUrl);

        //Uri uriData = Uri.parse(args.getString("imgUri"));
            //Glide.with(this).load(uriData).into(imageView);
            //Log.d("유알엘", imageUrl);
            //Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.image).into(imageView);
            //Glide.with(this).load(imageUrl).into(imageView);
          //  }
        return view;
    }

    public static ImageFragment newInstance(String url) {

        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }

    private int mParam1;

    public ImageFragment() {}

}