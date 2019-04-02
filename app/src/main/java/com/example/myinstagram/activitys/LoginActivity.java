package com.example.myinstagram.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import static com.example.myinstagram.activitys.MainActivity.myId;

public class LoginActivity extends AppCompatActivity{

    private HttpConnection httpConn;
    private HttpConnection httpConn2;

    EditText editId, editPassword;
    TextView txtLogin, txtAccount;
    String id, password;
    public static String jwtToken;


    private String jwt;
    Boolean autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        jwt = pref.getString("jwt", "");
        init();

        if(!jwt.equals("")){
            //autoLoginCheck();
            myIdLoad();
        }
        else{

        }
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

        editPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){ // 뷰의 id를 식별, 키보드의 완료 키 입력 검출
                    //이 부분에 원하는 이벤트를 작성합니다
                    if(isFormatOK()){
                        //Toast.makeText(LoginActivity.this,"완료이벤트" , Toast.LENGTH_SHORT).show();
                        sendData();
                    }
                    return  true;
                }
                return false;
            }
        });
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
                            Log.d("로그인결과", "서버에서 응답한 Body:"+jsonObject.toString());
                            //resultcode=jsonObject.getInt("code");
                            //if(resultcode==100){
                                jwt=jsonObject.getString("result");
                                jwtToken = jsonObject.getString("result");
                                Log.d("로그인토큰", jwt);
                            Log.d("로그인토큰", jwtToken);
                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("jwt", jwt);
                                editor.commit();
                                myIdLoad();
                                //Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                //startActivity(intent);
                                //finish();
                            //}
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
                                myIdLoad();
                                //Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                //startActivity(intent);
                                //finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    private void myIdLoad() {
        new Thread() {
            public void run() {
                HttpConnection httpConnection = new HttpConnection();
                httpConnection.myIdApi(jwt, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result;
                        int resultcode=0;
                        String data="";

                        result=response.body().string();
                        Log.d("아이디조회", result);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(result);
                            Log.d("아이디조회시도", json.toString());
                            resultcode = json.getInt("code");

                            if (resultcode == 100) {
                                Log.d("아이디조회 성공 ", json.toString());
                                myId = json.getString("id");
                                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

}
