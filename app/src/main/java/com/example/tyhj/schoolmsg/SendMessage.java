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
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
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
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adpter.ChatAdpter;
import adpter.PictureAdpter;
import api.FormatTools;
import myViews.SharedData;
import myViews.StatusBarUtil;
import myinterface.sendPicture;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import publicinfo.Picture;
import publicinfo.UserInfo;
import service.ChatService;

import static android.content.Intent.ACTION_GET_CONTENT;

@EActivity(R.layout.activity_send_message)
public class SendMessage extends AppCompatActivity implements sendPicture {
    IntentFilter intentFilter;
    ChatManager chatmanager;
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
        signBroadCast();
        contentResolver = getContentResolver();
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
        MyFunction.initPictures();
    }

    //获取选中图片
    @Override
    public void sendPicture(List<Picture> pictures) {
        if(pictures.size()>0){
           btn_send_picture.setTextColor(Color.parseColor("#2ca6cb"));
            btn_send_picture.setClickable(true);
        }else {
            btn_send_picture.setTextColor(Color.GRAY);
            btn_send_picture.setClickable(false);
        }
        sendPicture=pictures;
    }

    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            chatAdpter.add((Msg_chat) intent.getSerializableExtra("newMsg"));
            chatAdpter.notifyDataSetChanged();
            Log.e("TAG", "onReceive: 已经收到广播了" );
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
        ChatService.savaDate(chatAdpter.getMsg_chats(),group.getGroupName());
        MyFunction.setChatName(null);
        unregisterReceiver(msgBoradCastReceiver);
        this.finish();
    }

    @ViewById
    LinearLayout ll_bg;

    @ViewById
    ListView lv_msg;

    @Click(R.id.btn_send_picture)
    void sendPhotos(){
        if(sendPicture==null||sendPicture.size()<=0)
            return;
        ll_add.setVisibility(View.GONE);
        iv_add.startAnimation(overadd);
        et_text_send.setInputType(InputType.TYPE_CLASS_TEXT);
        MyFunction.initPictures();
        initRecycle();
        pictureAdpter.notifyDataSetChanged();
        for(int i=0;i<sendPicture.size();i++){
            MyTime myTime = new MyTime();
            String time=myTime.getYear() + myTime.getMonth_() + myTime.getDays() +
                    myTime.getWeek_() + myTime.getHour() + myTime.getMinute() +
                    myTime.getSecond() +i+ group.getGroupName() + ".JPEG";
            if(isBigPicture){
                try {
                    //复制图片
                    MyFunction.copyFile(new File(sendPicture.get(i).getPath()),new File(path,time));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //压缩图片
                MyFunction.ImgCompress(sendPicture.get(i).getPath(),new File(path,time));
            }
            final String fileName=path+"/"+time;
            if (savaImageCloud(fileName,time,sendPicture.get(i).getPath())) return;
        }
        sendPicture.clear();
    }

    @Click(R.id.iv_sound)
    //发送文字或语音
    void send() {
        if(MyFunction.isIntenet(SendMessage.this)&&UserInfo.canDo()){
        String text=et_text_send.getText().toString();
        if(change)
            initSendMsg(text,0,text);
        }else if(MyFunction.isIntenet(SendMessage.this)){
            Toast.makeText(SendMessage.this,"重新连接中",Toast.LENGTH_SHORT).show();
        }
    }
    //显示发送的消息
    private void initSendMsg(String Url,int type,String path) {
        String time;
        if(msg_chatList.size()>0)
            time = MyFunction.getTime(chatAdpter.getMsg_chats().get(chatAdpter.getCount() - 1).getTime());
        else
        time="刚刚";
        if (time != null) {
            chatAdpter.add(new Msg_chat(0, 0, 0, time, null, null, null, MyFunction.getTime()));
        }
        chatAdpter.add(new Msg_chat(1, type, -1, Url, path, null, "tyhj", MyFunction.getTime()));
        et_text_send.setText("");
        if (group.getIsgroup() == 0) {
                chatmanager=UserInfo.getXmppConnection().getChatManager();
            Chat newChat = chatmanager.createChat(group.getGroupName() + "@120.27.49.173",null);
            sendText(Url, newChat,type);
        } else {
            try {
                MyFunction.getMultiUserChat().sendMessage(Url + type);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }
    //相册
    @Click(R.id.btn_album)
    void album(){
        getDate();
        File file=new File(path,date);
        Intent intent = new Intent(ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, PICK_PHOTO);
    }
    //相机
    @Click(R.id.btn_camera)
    void camera(){
        getDate();
        File file=new File(path,date);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, TAKE_PHOTO);
    }
    //发送消息
    @Background
    public void sendText(String text, Chat newChat,int type)  {
        try {
            newChat.sendMessage(text+type);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    void afterViews(){
        contentResolver = getContentResolver();
        initListView();
        setListener();
        initRecycle();
        btn_send_picture.setClickable(false);
    }

    //初始化消息列表
    private void initListView() {
        msg_chatList=new SharedData(SendMessage.this).getData(group.getGroupName());
        if(msg_chatList==null) {
            msg_chatList = new ArrayList<Msg_chat>();
        }
        setMsgStatus();
        chatAdpter=new ChatAdpter(SendMessage.this,0,msg_chatList);
        lv_msg.setAdapter(chatAdpter);
        tv_name.setText(group.getGroupName());
        iv_heagImage.setClipToOutline(true);
        iv_heagImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        if(group.getDrawable()==null)
            Picasso.with(this).load(R.mipmap.default_headimage).into(iv_heagImage);
        else
            iv_heagImage.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(group.getDrawable()));
        lv_msg.setSelection(chatAdpter.getCount()-1);
        //单人聊天的头像
        chatAdpter.setHeadImage(FormatTools.getInstance().Bytes2Drawable(group.getDrawable()));
        lv_msg.setOnScrollListener(new PauseOnScrollListener(chatAdpter.getImageLoader(),true,true));
    }

    //注册广播
    private void signBroadCast() {
        msgBoradCastReceiver=new MsgBoradCastReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("boradcast.action.GETMESSAGE");
        registerReceiver(msgBoradCastReceiver,intentFilter);
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
                myTime.getSecond() + group.getGroupName() + ".JPEG";
    }
    @Override
    public void onBackPressed() {
        if(ll_add.getVisibility()==View.VISIBLE){
            iv_add.startAnimation(overadd);
            et_text_send.setInputType(InputType.TYPE_CLASS_TEXT);
            ll_add.setVisibility(View.GONE);
            return;
        }

        ChatService.savaDate(chatAdpter.getMsg_chats(),group.getGroupName());
        MyFunction.setChatName(null);
        unregisterReceiver(msgBoradCastReceiver);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //这是从相机返回的数据
            case TAKE_PHOTO:
                if (resultCode == this.RESULT_OK) {

                    File file=new File(path,date);
                    File newFile = new File(path, date);
                    if(isBigPicture){
                        try {
                            //复制图片
                            MyFunction.copyFile(file,newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //压缩图片
                        MyFunction.ImgCompress(file.getAbsolutePath(), newFile);
                    }
                    cropPhoto(Uri.fromFile(newFile));
                }
                break;
            //这是从相册返回的数据
            case PICK_PHOTO:
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
                    try {
                        if (!MyFunction.isIntenet(SendMessage.this))
                            return;
                        AVObject avObject = new AVObject("Image");
                        final AVFile file = AVFile.withAbsoluteLocalPath("chat.JPEG", fileName);
                        avObject.put("image", file);
                        avObject.put("name", group.getGroupName()+ date);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    getImageUrl(fileName,date);
                                } else {
                                    Toast.makeText(SendMessage.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    //获取图片URl
    public void getImageUrl(final String path,String name) {
        AVQuery<AVObject> query = new AVQuery<>("Image");
        query.whereEqualTo("name", group.getGroupName() + name);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是符合条件的第一个 AVObject
                if(e!=null||avObject==null)
                    return;
                String chatImageUrl = avObject.getAVFile("image").getUrl();
                if (chatImageUrl != null)
                    initSendMsg(chatImageUrl,1,path);
            }

        });
    }
    //上传图片并发送
    public boolean savaImageCloud(final String fileName, final String name, final String PrelandPath) {
        try {
            if (!MyFunction.isIntenet(SendMessage.this))
                return true;
            AVObject avObject = new AVObject("Image");
            final AVFile file = AVFile.withAbsoluteLocalPath("chat.JPEG", fileName);
            avObject.put("image", file);
            avObject.put("name", group.getGroupName()+ name);
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        File file1=new File(fileName);
                        if(file1.exists())
                            file1.delete();
                        getImageUrl(PrelandPath,name);
                    } else {
                        Toast.makeText(SendMessage.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
