package com.example.myinstagram.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myinstagram.Comment;
import com.example.myinstagram.CommentAdapter;
import com.example.myinstagram.R;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myinstagram.activitys.MainActivity.myProfileUrl;
import static com.example.myinstagram.activitys.MainActivity.timeline;

public abstract class BaseActivity extends AppCompatActivity {

    abstract void init();

}
