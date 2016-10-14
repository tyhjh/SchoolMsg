package com.example.tyhj.schoolmsg;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tyhj.myfist_2016_6_29.MyTime;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import adpter.ChatAdpter;
import myViews.AndroidBug5497Workaround;
import myViews.MyViewPager;
import myViews.SharedData;
import myViews.StatusBarUtil;
import publicinfo.GetChatMsg;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;

@EActivity(R.layout.activity_send_message)
public class SendMessage extends AppCompatActivity {
    List<Msg_chat> msg_chatList;
    ChatAdpter chatAdpter;
    Group group=null;
    boolean change;
    Animation big,small;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.mipmap.chat_bg);
        group= (Group) this.getIntent().getSerializableExtra("group");
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"));
        big= AnimationUtils.loadAnimation(this,R.anim.change_big);
        small=AnimationUtils.loadAnimation(this,R.anim.change_small);
        small.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(change){
                    iv_sound.setImageResource(R.drawable.ic_mic_gray_24dp);
                }else {
                    iv_sound.setImageResource(R.drawable.ic_send_24dp);
                }
                change=!change;
                iv_sound.startAnimation(big);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @ViewById
    ImageView iv_back,iv_heagImage;

    @ViewById
    TextView tv_name;


    @ViewById
    ImageView iv_sound;

    @ViewById
    EditText et_text_send;

    @TextChange(R.id.et_text_send)
    void AfterTextChanged(TextView hello) {
        if(!hello.getText().toString().equals("")&&!change){
            iv_sound.startAnimation(small);
        }else if(hello.getText().toString().equals("")) {
            iv_sound.startAnimation(small);
        }
    }

    @Click(R.id.iv_back)
    void back(){
        this.finish();
    }

    @ViewById
    LinearLayout ll_bg;

    @ViewById
    ListView lv_msg;

    @Click(R.id.iv_sound)
    //发送文字或语音
    void send(){
        if(change){
            String time=MyFunction.getTime(chatAdpter.getMsg_chats().get(chatAdpter.getCount()-1).getTime());
            if(time!=null){
                chatAdpter.add(new Msg_chat(0,0,0,time,null,null,null,MyFunction.getTime()));
            }
            chatAdpter.add(new Msg_chat(1,0,1,et_text_send.getText().toString(),null,null,"tyhj",MyFunction.getTime()));
            et_text_send.setText("");
        }
    }

    @AfterViews
    void afterViews(){
        msg_chatList=new SharedData(SendMessage.this).getData(group.getGroupName());
        if(msg_chatList==null) {
            msg_chatList = new ArrayList<Msg_chat>();
            initMsg();
        }
        if(MyFunction.isIntenet(SendMessage.this)) {
            new SharedData(SendMessage.this).saveData(msg_chatList,group.getGroupName());
        }
        chatAdpter=new ChatAdpter(SendMessage.this,0,msg_chatList);
        lv_msg.setAdapter(chatAdpter);
        tv_name.setText(group.getGroupName());
        iv_heagImage.setClipToOutline(true);
        iv_heagImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        Picasso.with(this).load(group.getGroupImageUrl()).into(iv_heagImage);
        lv_msg.setSelection(chatAdpter.getCount()-1);
    }

    private void initMsg() {
        Msg_chat msg_chat0=new Msg_chat(0,0,0,"昨天•22:45",null,null,null,10131210);
        Msg_chat msg_chat=new Msg_chat(1,0,0,"你好啊",null,null,"tyhj",10131210);
        Msg_chat msg_chat1=new Msg_chat(2,0,0,"你好,哈哈",null,getString(R.string.textUrl),"tyhj",10131210);
        Msg_chat msg_chat2=new Msg_chat(1,1,0,"你好啊",getString(R.string.textUrl),null,"tyhj",10131210);
        Msg_chat msg_chat3=new Msg_chat(0,0,0,"12:05",null,null,null,10141210);

        Msg_chat msg_chat4=new Msg_chat(1,0,1,"你好啊",null,null,"tyhj",10141210);
        Msg_chat msg_chat5=new Msg_chat(2,0,0,"你好,哈哈",null,getString(R.string.textUrl1),"tyhj",10131210);
        Msg_chat msg_chat6=new Msg_chat(1,1,1,"你好啊",getString(R.string.textUrl),null,"tyhj",10131210);
        Msg_chat msg_chat7=new Msg_chat(2,1,0,"",getString(R.string.textUrl2),getString(R.string.textUrl1),"tyhj",10131210);
        Msg_chat msg_chat8=new Msg_chat(1,1,1,"你好啊",getString(R.string.textUrl3),null,"tyhj",10131210);
        msg_chatList.add(msg_chat0);
        msg_chatList.add(msg_chat);
        msg_chatList.add(msg_chat1);
        msg_chatList.add(msg_chat2);
        msg_chatList.add(msg_chat3);
        msg_chatList.add(msg_chat4);
        msg_chatList.add(msg_chat5);
        msg_chatList.add(msg_chat6);
        msg_chatList.add(msg_chat7);
        msg_chatList.add(msg_chat8);
    }

    @Override
    public void onBackPressed() {
        new SharedData(SendMessage.this).saveData(chatAdpter.getMsg_chats(),group.getGroupName());
        super.onBackPressed();
    }

}
