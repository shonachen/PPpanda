package com.pppanda.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.pppanda.R;

/**
 * Created by Administrator on 2017/5/3.
 */

public class ForgetPasswordActivity extends Activity {
    boolean eyeOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        final ImageView ivBack = (ImageView)findViewById(R.id.forgetPassword_back);
        final ImageView ivEye = (ImageView)findViewById(R.id.forgetPassword_eye);
        final EditText etLoginPassword = (EditText)findViewById(R.id.forgetPassword_et_password);

        //返回
        ivBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //密码明文密文切换
        ivEye.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (eyeOpen){
                    etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.eye2);
                    eyeOpen = false;
                }else{
                    etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.eye);
                    eyeOpen = true;
                }
            }
        });
    }
}
