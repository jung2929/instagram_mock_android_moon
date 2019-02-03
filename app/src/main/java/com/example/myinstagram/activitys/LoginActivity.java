package com.example.myinstagram.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myinstagram.HttpConnection;
import com.example.myinstagram.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity{

    private HttpConnection httpConn;
    private HttpConnection httpConn2;

    EditText editId, editPassword;
    TextView txtLogin, txtAccount;
    String id, password;

    static public  String jwt;
    Boolean autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        //follow();
        autoLoginCheck();
    }


    void init() {
        httpConn = new HttpConnection();
        httpConn2 = new HttpConnection();
        editId=findViewById(R.id.editId);
        editPassword=findViewById(R.id.editPassword);
        txtLogin=findViewById(R.id.txtLogin);
        txtAccount=findViewById(R.id.txtAccount);

        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormatOK()){
                    sendData();
                }
            }
        });

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        //SharedPreferences.Editor editor = pref.edit();
        //editor.remove("jwt");
        //editor.commit();

        jwt = pref.getString("jwt", "");
    }


    private Boolean isFormatOK(){
        id=editId.getText().toString();
        password=editPassword.getText().toString();

        if(id.length()==0 || password.length()==0 ){
            Toast.makeText(LoginActivity.this,"아이디와 비밀번호를 모두 입력해주세요" , Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    private void sendData() {
        new Thread() {
            public void run() {
                httpConn.loginApi(id, password, new Callback(){
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
                            JSONObject jsonObject= new JSONObject(result);
                            Log.d("로그인결과", "서버에서 응답한 Body:"+result);
                            resultcode=jsonObject.getInt("code");
                            if(resultcode==100){
                                jwt=jsonObject.getString("result");
                                Log.d("로그인토큰", jwt);
                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("jwt", jwt);
                                editor.commit();
                                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }.start();
    }


    private void autoLoginCheck() {
        new Thread() {
            public void run() {
                httpConn2.followerListApi(jwt, new Callback(){
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
                            int index = result.indexOf("{");
                            result = result.substring(index);
                            //Log.d("오토로그인", "서버에서 응답한 Body:"+result);
                            JSONObject jsonObject= new JSONObject(result);
                            resultcode=jsonObject.getInt("code");
                            Log.d("오토로그인", "서버에서 응답한 Body:"+jsonObject);
                            Log.d("오토로그인", "code값:"+resultcode);
                            if(resultcode==100){
                                autoLogin=true;
                                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }


    private void follow() {
        new Thread() {
            public void run() {
                httpConn.followApi(jwt, "dudwls0113", new Callback(){
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
                                Log.d("팔로우", "팔로오 성공");
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
