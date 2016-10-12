package com.example.tyhj.schoolmsg;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import apis.connection.XmppConnection;
import apis.userAndRoom.User;
import publicinfo.MyFunction;

@EActivity(R.layout.activity_signup)
public class Signup extends AppCompatActivity {
    private int count=30;
    private static  boolean CANGETAUTHCODE = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @ViewById
    EditText etRegisterNumber,etRegisterEmail,etRegisterPassword,etRegisterAuthCode,etRegisterName;
    @ViewById
    Button btGetAuthCode,btRegister;
    @ViewById
    ImageView ivRegisterBack;


    @Click(R.id.ivRegisterBack)
    void back(){
        this.finish();
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
        User user = new User(XmppConnection.getInstance().getConnection());
        if(user.regist(name, pas).equals("1"))
            Toast("注册成功");
        else
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

}
