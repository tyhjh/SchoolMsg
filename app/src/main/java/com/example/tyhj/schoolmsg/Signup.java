package com.example.tyhj.schoolmsg;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import api.FormatTools;
import myViews.CircularAnim;
import publicinfo.MyFunction;
import publicinfo.UserInfo;
import service.ChatService;

@EActivity(R.layout.activity_signup)
public class Signup extends AppCompatActivity {
    private int count=30;
    private static  boolean CANGETAUTHCODE = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Client client=Client.create();
        WebResource webResource=client.resource("");
    }

    @ViewById
    EditText etRegisterNumber,etRegisterEmail,etRegisterPassword,etRegisterAuthCode,etRegisterName;
    @ViewById
    Button btGetAuthCode,btRegister;
    @ViewById
    ImageView ivRegisterBack;


    @Click(R.id.ivRegisterBack)
    void back(){
        CircularAnim.fullActivity(Signup.this, ivRegisterBack)
//                        .colorOrImageRes(R.color.colorPrimary)  //注释掉，因为该颜色已经在App.class 里配置为默认色
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        Signup.this.finish();
                    }
                });

    }

    //获取邮箱验证码
    @Click(R.id.btGetAuthCode)
    void getAuthCode(){
        if(CANGETAUTHCODE&& MyFunction.isIntenet(this)&&MyFunction.isEmail(etRegisterEmail.getText().toString().trim(),Signup.this)){
            CANGETAUTHCODE=false;
            setCount();
        }
    }
    //注册
    @Click(R.id.btRegister)
    void register(){
        if(!MyFunction.isIntenet(this))
            return;
        String number=etRegisterNumber.getText().toString();
        String password=etRegisterPassword.getText().toString();
        if(!number.equals("")&&!password.equals("")){
            regs(number,password);
        }

    }
    @Background
    void regs(String name,String pas) {
        if(UserInfo.Register(name,pas,Signup.this)) {
            Toast("注册成功");
            if(!etRegisterName.getText().toString().trim().equals(""))
                MyFunction.setRegisterName(etRegisterName.getText().toString().trim());
            else
                MyFunction.setRegisterName(name);
            MyFunction.setRegister(true);
            MyFunction.setRegisterId(name);
            this.finish();
        } else
          Snack("失败");
    }

    @UiThread
    void Toast(String str){
        Toast.makeText(Signup.this,str,Toast.LENGTH_SHORT).show();
    }
    @UiThread
    void Snack(String s){
        Snackbar.make(btRegister,s,Snackbar.LENGTH_SHORT).show();
    }

    //验证码计时中
    @UiThread
    void change(){
        btGetAuthCode.setText(count+"可后重新获取");
    }

    //可以获取验证码
    @UiThread
    void review(){
        btGetAuthCode.setText("获取验证码");
    }

    //获取验证码时间限制
    @Background
    void setCount(){
        while (!CANGETAUTHCODE){
            try {
                change();
                Thread.sleep(1000);
                count--;
                if(count==0){
                    CANGETAUTHCODE=true;
                    count=30;
                    review();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @AfterViews
    void afterViews(){
        ivRegisterBack.setClipToOutline(true);
        ivRegisterBack.setOutlineProvider(MyFunction.getOutline(false,20,10));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 添加返回过渡动画.
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
