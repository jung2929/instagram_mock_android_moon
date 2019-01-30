package com.example.myinstagram;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myinstagram.MainActivity.myProfileUrl;

public class CommentActivity extends AppCompatActivity {

    ArrayList<Comment> commentList;
    RecyclerView commentListView;
    LinearLayoutManager mLayoutManager;
    CommentAdapter commentAdapter;

    TextView txtPost;
    EditText editComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentList = (ArrayList<Comment>)getIntent().getSerializableExtra("comment");
        init();
    }

    private void init() {
        commentListView = (RecyclerView) findViewById(R.id.commentList);
        mLayoutManager = new LinearLayoutManager(this);
        commentListView.setLayoutManager(mLayoutManager);
        commentAdapter = new CommentAdapter(getSupportFragmentManager(), commentList, this);
        commentListView.setAdapter(commentAdapter);

        editComment = findViewById(R.id.editComment);
        txtPost = findViewById(R.id.txtPost);
        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String profileUrl = "https://media.treepla.net:447/project/7ac115de-ec05-4ca3-8ea7-f3b70a22a1dc.png";
                Comment newComment = new Comment("myNickName", editComment.getText().toString(), new Date(), myProfileUrl);
                commentList.add(newComment);
                commentAdapter.notifyDataSetChanged();
                editComment.setText("");
                hideKeyBoard();


            }
        });
    }

    public void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);
    }


}
