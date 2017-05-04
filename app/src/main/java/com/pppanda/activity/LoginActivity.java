package com.pppanda.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pppanda.response.BaseResponse;
import com.pppanda.request.LoginRequest;
import com.pppanda.response.LoginResponse;
import com.pppanda.Md5Util;
import com.pppanda.R;
import com.pppanda.request.UserIdRequest;
import com.pppanda.response.UserIdResponse;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/28.
 */

public class LoginActivity extends Activity {
    private static final int MSG_GET_USER_ID_SUCCEED = 0X13;
    private static final int MSG_GET_USER_ID_FAILED = 0X14;
    private static final int MSG_LOGIN_SUCCEED = 0X15;
    private static final int MSG_LOGIN_FAILED = 0X16;

    public ImageView imageView;
    public EditText editText;
    boolean eyeOpen = false;
    String phone,password;
    EditText etPhone,etPassword;
    ImageView ivEye;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_USER_ID_SUCCEED:
                    int userID = msg.arg1;
                    Log.e("TAG", "userID = "+userID);

                    login(userID);
                    break;
                case MSG_GET_USER_ID_FAILED:
                    String msgObj = (String)msg.obj;
                    Toast.makeText(LoginActivity.this, msgObj, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_LOGIN_SUCCEED:
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    break;
                case MSG_LOGIN_FAILED:
                    String msgObj1 = (String)msg.obj;
                    Toast.makeText(LoginActivity.this,msgObj1,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imageView = (ImageView)findViewById(R.id.login_eye);
        editText = (EditText)findViewById(R.id.login_et_password);
        ivEye = (ImageView)findViewById(R.id.login_eye);
        etPassword = (EditText)findViewById(R.id.login_et_password);
        etPhone = (EditText)findViewById(R.id.login_et_phone);
        Button btnLogin = (Button)findViewById(R.id.login_btn_login);
        Button btnRegister = (Button)findViewById(R.id.login_btn_register);
        TextView tvForgetPassword = (TextView)findViewById(R.id.login_tv_forget_password) ;

        //密码明文密文切换
        ivEye.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (eyeOpen){
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.eye2);
                    eyeOpen = false;
                }else{
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.eye);
                    eyeOpen = true;
                }
            }
        });

        //登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserID();
            }
        });

        //点击注册按钮进入注册页面
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //点击忘记密码进入密码找回页面
        tvForgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        tvForgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//添加下划线
        tvForgetPassword.getPaint().setAntiAlias(true);//抗锯齿
    }

    private void getUserID(){
        final Thread mUserId = new Thread(){
            @Override
            public void run(){
                String client_id = "zx2016";
                int req_time = (int)(System.currentTimeMillis()/1000);
                String sign_key = Md5Util.md5(req_time + "binwen");
                String mob_phone = etPhone.getText().toString();
                String card_id = "";
                UserIdRequest mUserIdRequest = new UserIdRequest(client_id,req_time,sign_key,mob_phone,card_id);
                Gson mGson = new Gson();
                String json = mGson.toJson(mUserIdRequest);
                Log.e("TAG", json);

                String url = "http://api.pp-panda.cc:8080/v1/user/usergetid";

                //OkHttp
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                //发送者
                OkHttpClient mOkHttpClient = new OkHttpClient();
                //请求
                Request mRequest = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(JSON, json))
                        .build();
                //请求过后的响应
                Response mUserIdResponse = null;
                try {
                    mUserIdResponse = mOkHttpClient.newCall(mRequest).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String mUserIdResult = null;
                try {
                    mUserIdResult = mUserIdResponse.body().string();
                    Log.e("TAG", "mResponse.body().string() = \n" + mUserIdResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BaseResponse mBaseResponse = mGson.fromJson(mUserIdResult, BaseResponse.class);
                int code1 = mBaseResponse.getCode();
                String codeMsg1= mBaseResponse.getCode_msg();
                if(code1 == 0){
                    UserIdResponse mUserIdResponseString = mGson.fromJson(mUserIdResult,UserIdResponse.class);
                    int userID = mUserIdResponseString.getBody().getUser_id();

                    Log.e("TAG", "code_msg = " + codeMsg1);
                    Log.e("TAG", "code = " + code1);
                    Log.e("TAG", "user_id = " + userID);

//                        Message msg = new Message();
//                        msg.what = MSG_GET_USER_ID_SUCCEED;
//                        msg.arg1 = 0;
//                        msg.arg2 = 2;
//                        msg.obj = "Hello";
//                        mHandler.sendMessage(msg);
                    Message msg = new Message();
                    msg.what = MSG_GET_USER_ID_SUCCEED;
                    msg.arg1 = userID;
                    mHandler.sendMessage(msg);
                }else {
//                    mHandler.sendEmptyMessage(MSG_GET_USER_ID_FAILED);
                    Message msg = new Message();
                    msg.what = MSG_GET_USER_ID_FAILED;
                    msg.obj = codeMsg1;
                    mHandler.sendMessage(msg);
                }
            }
        };

        mUserId.start();
    }


    private void login(final int userID){

        Thread mlogin = new Thread(){
            @Override
            public void run(){
                String client_id = "zx2016";
                int req_time = (int)(System.currentTimeMillis()/1000);
                String sign_key = Md5Util.md5(req_time + "binwen");
//                        int user_id = 5570;
//                        String password = "Aa123456";
                password = etPassword.getText().toString();
                password = Md5Util.md5(password);
                int user_id = userID;
                String finger = "";
                int login_type = 1;
                String login_deviceid = "Android" + "," + "JPUSH" + "," + 5570;
                String login_lang = "zh_CN";
                LoginRequest mLoginRequest = new LoginRequest(client_id,req_time,sign_key,user_id,password,finger,login_type,login_deviceid,login_lang);
                Gson mGson = new Gson();
                String json = mGson.toJson(mLoginRequest);
                Log.e("TAG", json);

                String url = "http://api.pp-panda.cc:8080/v1/user/userlogin";

                //OkHttp
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                //发送者
                OkHttpClient mOkHttpClient = new OkHttpClient();
                //请求
                Request mRequest = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(JSON, json))
                        .build();
                //请求过后的响应
                Response mResponse = null;
                try {
                    mResponse = mOkHttpClient.newCall(mRequest).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String mResult = null;
                try {
                    mResult = mResponse.body().string();
                    Log.e("TAG", "mResponse.body().string() = \n" + mResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BaseResponse mBaseResponse = mGson.fromJson(mResult,LoginResponse.class);
                int code2 = mBaseResponse.getCode();
                String codeMsg2 = mBaseResponse.getCode_msg();
                if (code2 == 0){
                    LoginResponse mLoginResponse = mGson.fromJson(mResult,LoginResponse.class);
                    String body = mLoginResponse.getBody().toString();
                    Log.e("TAG", "code = " + code2);
                    Log.e("TAG", "code_msg = " + codeMsg2);
                    Log.e("TAG", "body = " + body);

                    Message msg = new Message();
                    msg.what = MSG_LOGIN_SUCCEED;
                    mHandler.sendMessage(msg);
                }else {
                    Message msg = new Message();
                    msg.what = MSG_LOGIN_FAILED;
                    msg.obj = codeMsg2;
                    mHandler.sendMessage(msg);
                }

            }
        };
        mlogin.start();

    }
}
