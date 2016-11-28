package com.example.tyhj.schoolmsg;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.simplearcloader.SimpleArcLoader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import myViews.CircularAnim;
import myViews.SharedData;
import publicinfo.Group;
import publicinfo.MyFunction;
import publicinfo.UserInfo;
import service.ChatService;

@EActivity(R.layout.activity_login)
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shared=getSharedPreferences("login", Context.MODE_PRIVATE);
        if(shared!=null&&shared.getString("name",null)!=null){
            starActivity();
            this.finish();
        }
    }

    @ViewById
    TextView tvsign,tvforgetpas;
    @ViewById
    EditText etUserNumber,etUserPassord;
    @ViewById
    Button btLogin;
    @ViewById
    ImageView UserHeadImaegLg;
    @ViewById
    ImageView iv_user,iv_pas;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    @AfterViews
    void afterViews(){
        UserHeadImaegLg.setOutlineProvider(MyFunction.getOutline(true,20,0));
        UserHeadImaegLg.setClipToOutline(true);
        Picasso.with(Login.this).load(R.mipmap.default_headimage).into(UserHeadImaegLg);
        Picasso.with(Login.this).load(R.drawable.im_user).resize(100, 100).centerCrop().into(iv_user);
        Picasso.with(Login.this).load(R.drawable.im_pas).resize(100, 100).centerCrop().into(iv_pas);
    }

    @Click(R.id.tvsign)
    void setTvsign(){
        CircularAnim.fullActivity(Login.this, tvsign)
//                        .colorOrImageRes(R.color.colorPrimary)  //注释掉，因为该颜色已经在App.class 里配置为默认色
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(Login.this,Signup_.class));
                    }
                });
    }

    @Click(R.id.tvforgetpas)
    void setTvforgetpas(){
        CircularAnim.fullActivity(Login.this, tvforgetpas)
//                        .colorOrImageRes(R.color.colorPrimary)  //注释掉，因为该颜色已经在App.class 里配置为默认色
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(Login.this,ChanegPassword_.class));
                    }
                });

    }

    @Click(R.id.btLogin)
    void login(){
        List<Group> groups=new ArrayList<Group>();
        new SharedData(this).savaGrops(groups);
        String name,pas;
        name=etUserNumber.getText().toString();
        pas=etUserPassord.getText().toString();
        log(name,pas);
    }

    @Background
    void log(String name,String pas){
        if(UserInfo.Login(name,pas,Login.this)){
            Intent intent=new Intent(this, ChatService.class);
            startService(intent);
            starActivity();
        }else {
            Snack("失败");
        }
    }

    @UiThread
    void Toast(String str){
        Toast.makeText(Login.this,str,Toast.LENGTH_SHORT).show();
    }

    @UiThread
    void starActivity(){
        CircularAnim.fullActivity(Login.this, btLogin)
//                        .colorOrImageRes(R.color.colorPrimary)  //注释掉，因为该颜色已经在App.class 里配置为默认色
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(Login.this,Home_.class));
                        Login.this.finish();
                    }
                });
    }

    @UiThread
    void Snack(String s){
        Snackbar.make(btLogin,s,Snackbar.LENGTH_SHORT).show();
    }
}
