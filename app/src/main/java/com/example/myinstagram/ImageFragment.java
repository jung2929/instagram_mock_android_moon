package com.example.myinstagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        if (getArguments() != null) {
            Bundle args = getArguments();
            // MainActivity에서 받아온 Resource를 ImageView에 셋팅
            final String imageUrl = args.getString("imgUrl");
            //Log.d("유알엘", imageUrl);
            //Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.image).into(imageView);
            Glide.with(this).load(imageUrl).into(imageView);
            }
        return view;
    }

    public static ImageFragment newInstance(String url) {

        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }



    // TODO: Rename and change types of parameters
    private int mParam1;

    public ImageFragment() {}

}