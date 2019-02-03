package com.example.myinstagram.activitys;

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

public class AccountActivity extends AppCompatActivity {

    private HttpConnection httpConn;
    EditText editId, editPassword, editPasswordCheck, editName, editEmalil, editIntro;
    TextView txtAccount;
    String id, password, passwordCheck, name, email, intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        init();
    }

    void init(){
        httpConn = new HttpConnection();
        editId = findViewById(R.id.editId);
        editPassword = findViewById(R.id.editPassword);
        editPasswordCheck = findViewById(R.id.editPasswordCheck);
        editName = findViewById(R.id.editName);
        editEmalil = findViewById(R.id.editEmalil);
        editIntro = findViewById(R.id.editIntro);
        txtAccount = findViewById(R.id.txtAccount);
        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormatOK()){
                    sendData();
                }
            }
        });
    }

    private void sendData() {
        // 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
                // 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.accountApi(id, password, name, email, intro, new Callback(){
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.d(TAG, "콜백오류:"+e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        int resultcode=0;
                        try {
                            JSONObject jsonObject= new JSONObject(response.body().string());

                            Log.d("가입결과", "서버에서 응답한 Body:"+jsonObject);
                            Log.d("가입", id+password+name+email+intro);

                            resultcode=jsonObject.getInt("code");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(resultcode==100){
                            //Toast.makeText(AccountActivity.this,"회원가입이 완료되었습니다" , Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }.start();;
    }

    private Boolean isFormatOK(){
        id=editId.getText().toString();
        password=editPassword.getText().toString();
        passwordCheck=editPasswordCheck.getText().toString();
        name=editName.getText().toString();
        email=editEmalil.getText().toString();
        intro=editIntro.getText().toString();

        if(id.length()==0 || password.length()==0 || passwordCheck.length()==0 || name.length()==0 || email.length()==0 || intro.length()==0){
            Toast.makeText(AccountActivity.this,"입력하지 않은 항목이 있습니다" , Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!password.equals(passwordCheck)){
            Toast.makeText(AccountActivity.this,"비밀번호가 확인과 다릅니다" , Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

}
