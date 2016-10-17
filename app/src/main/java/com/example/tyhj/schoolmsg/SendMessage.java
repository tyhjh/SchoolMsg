package com.example.tyhj.schoolmsg;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tyhj.myfist_2016_6_29.MyTime;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adpter.ChatAdpter;
import adpter.PictureAdpter;
import myViews.SharedData;
import myViews.StatusBarUtil;
import myinterface.sendPicture;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import publicinfo.Picture;

import static android.content.Intent.ACTION_GET_CONTENT;

@EActivity(R.layout.activity_send_message)
public class SendMessage extends AppCompatActivity implements sendPicture {
    IntentFilter intentFilter;
    MsgBoradCastReceiver msgBoradCastReceiver;
    List<Msg_chat> msg_chatList;
    ChatAdpter chatAdpter;
    Group group=null;
    boolean change;
    Animation big,small,addpicture,overadd;
    InputMethodManager imm;
    PictureAdpter pictureAdpter;
    List<Picture> pictures;
    List<Picture> sendPicture;
    Uri imageUri;
    ContentResolver contentResolver;
    String date;
    boolean isBigPicture;
    String path = Environment.getExternalStorageDirectory() + "/ASchollMsg";
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int PICK_PHOTO=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.mipmap.chat_bg);
        group= (Group) this.getIntent().getSerializableExtra("group");
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"));
        addpicture=AnimationUtils.loadAnimation(this,R.anim.addpicture);
        overadd=AnimationUtils.loadAnimation(this,R.anim.addpictureover);
        big= AnimationUtils.loadAnimation(this,R.anim.change_big);
        small=AnimationUtils.loadAnimation(this,R.anim.change_small);
        small.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(ll_add.getVisibility()==View.VISIBLE)
                    return;
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
        MyFunction.setChatName(group.getGroupName());
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void sendPicture(List<Picture> pictures) {
        if(pictures.size()>0){
            btn_send_picture.setTextColor(getColor(R.color.blue));
        }else {
            btn_send_picture.setTextColor(getColor(R.color.ichome));
        }
        sendPicture=pictures;
    }


    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            chatAdpter.add((Msg_chat) intent.getSerializableExtra("newMsg"));
            chatAdpter.notifyDataSetChanged();
        }
    }

    @ViewById
    LinearLayout ll_add;

    @ViewById
    Button btn_album,btn_camera,btn_send_picture;

    @ViewById
    RecyclerView rcly_picture;

    @ViewById
    CheckBox ckb_big;

    @ViewById
    ImageView iv_back,iv_heagImage,iv_add;

    @ViewById
    TextView tv_name;

    @ViewById
    ImageView iv_sound;

    @ViewById
    EditText et_text_send;

    @ViewById
    CardView cd_send;

    @Click(R.id.iv_add)
    void add(){
        imm.hideSoftInputFromWindow(et_text_send.getWindowToken(), 0);
        //isOpen若返回true，则表示输入法打开
        if(ll_add.getVisibility()!=View.VISIBLE){
            ll_add.setVisibility(View.VISIBLE);
            iv_add.startAnimation(addpicture);
            et_text_send.setInputType(InputType.TYPE_NULL);
        }else {
            ll_add.setVisibility(View.GONE);
            iv_add.startAnimation(overadd);
            et_text_send.setInputType(InputType.TYPE_CLASS_TEXT);
        }

    }

    @TextChange(R.id.et_text_send)
    void AfterTextChanged(TextView hello) {
        if(!hello.getText().toString().trim().equals("")&&!change){
            iv_sound.startAnimation(small);
        }else if(hello.getText().toString().trim().equals("")&&change) {
            iv_sound.startAnimation(small);
        }
    }

    @Click(R.id.iv_back)
    void back(){
        new SharedData(SendMessage.this).saveData(chatAdpter.getMsg_chats(),group.getGroupName());
        MyFunction.setChatName(null);
        this.finish();
    }

    @ViewById
    LinearLayout ll_bg;

    @ViewById
    ListView lv_msg;

    @Click(R.id.iv_sound)
    //发送文字或语音
    void send() {
        if(MyFunction.isIntenet(SendMessage.this)&&MyFunction.isConnect()){
        String text=et_text_send.getText().toString();
        if(change) {
            String time = MyFunction.getTime(chatAdpter.getMsg_chats().get(chatAdpter.getCount() - 1).getTime());
            if (time != null) {
                chatAdpter.add(new Msg_chat(0, 0, 0, time, null, null, null, MyFunction.getTime()));
            }
            chatAdpter.add(new Msg_chat(1, 0, 1, text, null, null, "tyhj", MyFunction.getTime()));
            et_text_send.setText("");
            ChatManager chatmanager = MyFunction.getUser().getChatManager();

            if (group.getIsgroup() == 0) {
                Chat newChat = chatmanager.createChat(group.getGroupName() + "@120.27.49.173", new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        Log.e("send", "成功接受" + message.getBody());
                        chatAdpter.update();
                    }
                });
                sendText(text, newChat);
            } else {
                try {
                    MyFunction.getMultiUserChat().sendMessage(text + "0");
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }
        }
    }

    @Click(R.id.btn_album)
    void album(){
        Intent intent = new Intent(ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICK_PHOTO);
    }

    @Click(R.id.btn_camera)
    void camera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Background
    public void sendText(String text, Chat newChat)  {
        try {
            newChat.sendMessage(text+"0");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    void afterViews(){
        initListView();
        signBroadCast();
        setListener();
        initRecycle();
    }

    //初始化消息列表
    private void initListView() {
        msg_chatList=new SharedData(SendMessage.this).getData(group.getGroupName());
        if(msg_chatList==null) {
            msg_chatList = new ArrayList<Msg_chat>();
            initMsg();
        }
        setMsgStatus();
        chatAdpter=new ChatAdpter(SendMessage.this,0,msg_chatList);
        lv_msg.setAdapter(chatAdpter);
        tv_name.setText(group.getGroupName());
        iv_heagImage.setClipToOutline(true);
        iv_heagImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        Picasso.with(this).load(group.getGroupImageUrl()).into(iv_heagImage);
        lv_msg.setSelection(chatAdpter.getCount()-1);
        //单人聊天的头像
        chatAdpter.setHeadImage(group.getGroupImageUrl());
    }

    //注册广播
    private void signBroadCast() {
        msgBoradCastReceiver=new MsgBoradCastReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("boradcast.action.GETMESSAGE");
        registerReceiver(msgBoradCastReceiver,intentFilter);
        contentResolver = getContentResolver();
    }

    //设置消息为已读
    private void setMsgStatus() {
        for(int i=msg_chatList.size()-1;i>=0;i--){
            if(msg_chatList.get(i).getWho()==2&&msg_chatList.get(i).getStatus()!=2){
                msg_chatList.get(i).setStatus(2);
            }else {
                break;
            }
        }
    }

    //添加的监听
    private void setListener() {
        ckb_big.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    isBigPicture=true;
                else
                    isBigPicture=false;
            }
        });
    }

    //初始化本地图片的列表
    private void initRecycle() {
        pictures=MyFunction.getPictureList();
        pictureAdpter=new PictureAdpter(this,pictures);
        rcly_picture.setAdapter(pictureAdpter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rcly_picture.setLayoutManager(linearLayoutManager);
        rcly_picture.setItemAnimator(new DefaultItemAnimator());
        pictureAdpter.setInterface(this);
    }

    //初始化测试数据
    private void initMsg() {
        Msg_chat msg_chat0=new Msg_chat(0,0,0,"昨天•22:45",null,null,null,10131210);
        Msg_chat msg_chat=new Msg_chat(1,0,0,"你好啊",null,null,"tyhj",10131210);
        Msg_chat msg_chat1=new Msg_chat(2,0,0,"你好,哈哈",null,getString(R.string.textUrl),"tyhj",10131210);
        Msg_chat msg_chat2=new Msg_chat(1,1,1,"你好啊",getString(R.string.textUrl),null,"tyhj",10131210);
        Msg_chat msg_chat3=new Msg_chat(0,0,0,"12:05",null,null,null,10141210);

        Msg_chat msg_chat4=new Msg_chat(1,0,1,"你好啊",null,null,"tyhj",10141210);
        Msg_chat msg_chat5=new Msg_chat(2,0,0,"你好,哈哈",null,getString(R.string.textUrl1),"tyhj",10131210);
        Msg_chat msg_chat6=new Msg_chat(1,1,1,"你好啊",getString(R.string.textUrl),null,"tyhj",10131210);
        Msg_chat msg_chat8=new Msg_chat(1,1,1,"你好啊",getString(R.string.textUrl3),null,"tyhj",10131210);
        msg_chatList.add(msg_chat0);
        msg_chatList.add(msg_chat);
        msg_chatList.add(msg_chat1);
        msg_chatList.add(msg_chat2);
        msg_chatList.add(msg_chat3);
        msg_chatList.add(msg_chat4);
        msg_chatList.add(msg_chat5);
        msg_chatList.add(msg_chat6);
        msg_chatList.add(msg_chat8);
    }

    //剪裁图片
    public void cropPhoto(Uri imageUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);
    }
    //随机获取文件名字
    public void getDate() {
        MyTime myTime = new MyTime();
        date = myTime.getYear() + myTime.getMonth_() + myTime.getDays() +
                myTime.getWeek_() + myTime.getHour() + myTime.getMinute() +
                myTime.getSecond() + MyFunction.getUserInfo().getName() + ".JPEG";
    }
    @Override
    public void onBackPressed() {
        new SharedData(SendMessage.this).saveData(chatAdpter.getMsg_chats(),group.getGroupName());
        MyFunction.setChatName(null);
        unregisterReceiver(msgBoradCastReceiver);

        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //这是从相机返回的数据
            case TAKE_PHOTO:
                getDate();
                if (resultCode == this.RESULT_OK) {
                    if (data != null) {
                        imageUri = data.getData();
                    }

                    String path_pre = MyFunction.getFilePathFromContentUri(imageUri, contentResolver);
                    File newFile = new File(path, date);
                    if(isBigPicture){
                        try {
                            //复制图片
                            MyFunction.copyFile(new File(path_pre),newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //压缩图片
                        MyFunction.ImgCompress(path_pre, newFile);
                    }
                    cropPhoto(Uri.fromFile(newFile));
                }
                break;
            //这是从相册返回的数据
            case PICK_PHOTO:
                getDate();
                if (resultCode == this.RESULT_OK) {
                    if (data != null) {
                        imageUri = data.getData();
                    }
                    String path_pre = MyFunction.getFilePathFromContentUri(imageUri, contentResolver);
                    File newFile = new File(path, date);
                    if(isBigPicture){
                        try {
                            //复制图片
                            MyFunction.copyFile(new File(path_pre),newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //压缩图片
                        MyFunction.ImgCompress(path_pre, newFile);
                    }
                    cropPhoto(Uri.fromFile(newFile));
                }
                break;
            //剪裁图片返回数据,就是原来的文件
            case CROP_PHOTO:
                if (resultCode == this.RESULT_OK) {
                    final String fileName = path + "/" + date;
                    File newFile = new File(path, date);
                    if(!isBigPicture){
                        MyFunction.ImgCompress(fileName, newFile);
                    }
                   //获取到的就是new File或fileName

                }
                break;
            default:
                break;
        }
    }

}
