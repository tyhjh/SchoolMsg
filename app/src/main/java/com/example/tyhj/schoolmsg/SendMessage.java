package com.example.tyhj.schoolmsg;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import api.AudioRecoderUtils;
import api.FormatTools;
import api.PopupWindowFactory;
import myViews.SharedData;
import myViews.StatusBarUtil;
import myinterface.ExpendImage;
import myinterface.sendPicture;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import publicinfo.MyHttp;
import publicinfo.Picture;
import publicinfo.UserInfo;
import service.ChatService;

import static android.content.Intent.ACTION_GET_CONTENT;

@EActivity(R.layout.activity_send_message)
public class SendMessage extends AppCompatActivity implements sendPicture, ExpendImage {
    IntentFilter intentFilter;
    ChatManager chatmanager;
    MsgBoradCastReceiver msgBoradCastReceiver;
    List<Msg_chat> msg_chatList;
    ChatAdpter chatAdpter;
    Group group=null;
    boolean change;
    Animation big,small,addpicture,overadd,expend,noexpend,saveup,savedown;
    InputMethodManager imm;
    PictureAdpter pictureAdpter;
    List<Picture> pictures;
    List<Picture> sendPicture;
    Uri imageUri;
    ContentResolver contentResolver;
    String date;
    boolean isBigPicture=false;
    AudioRecoderUtils mAudioRecoderUtils;
    ImageView mImageView;
    TextView mTextView;
    PopupWindowFactory mPop;
    float y=0;
    String path = Environment.getExternalStorageDirectory() + "/ASchollMsg";
    String RECORD_NAME;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int PICK_PHOTO=0;
    long time=0;
    String time_Long;
    private ImageView ivEssayExpend;
    private Button saveImage;
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
        expend=AnimationUtils.loadAnimation(this,R.anim.expend);
        noexpend=AnimationUtils.loadAnimation(this,R.anim.noexpend);
        saveup=AnimationUtils.loadAnimation(this,R.anim.saveimage_up);
        savedown=AnimationUtils.loadAnimation(this,R.anim.saveimage_down);
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
        MyFunction.setChatName(group.getId());
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        MyFunction.initPictures();
        saveup.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                saveImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        savedown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                saveImage.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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

    //查看，保存聊天图片
    @Override
    public void callBack(final Msg_chat msg_chat) {
        Dialog dialog=new Dialog(this,R.style.Dialog_Fullscreen);
        dialog.setCancelable(true);
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.dialogessayimage,null);
        dialog.setContentView(view);
        dialog.create();
        ivEssayExpend= (ImageView) view.findViewById(R.id.ivEssayExpend);
        saveImage= (Button) view.findViewById(R.id.btSavaEssayIv);
        saveImage.setVisibility(View.INVISIBLE);
        Picasso.with(this).load(msg_chat.getText()).into(ivEssayExpend);
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        dialog.getWindow().setAttributes(p);     //设置生效
        dialog.show();

        ivEssayExpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveImage.getVisibility()==View.GONE||saveImage.getVisibility()==View.INVISIBLE)
                    saveImage.startAnimation(saveup);
                else
                    saveImage.startAnimation(savedown);
            }
        });
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyFunction.isIntenet(SendMessage.this))
                    return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyFunction.savaFile(msg_chat.getText(),group.getId()+MyFunction.getTime()+getString(R.string.imageFormat),handler,SendMessage.this);
                    }
                }).start();
            }
        });
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
        ChatService.savaDate(chatAdpter.getMsg_chats(),group.getId());
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
                    myTime.getSecond() +i+ group.getId() + ".JPEG";
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

    //显示发送的消息
    private void initSendMsg(final String Url, final int type, String path) {
        if(Url==null||Url.equals(""))
            return;
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
        if (!group.getId().equals(UserInfo.getGroupId())) {
                chatmanager=UserInfo.getXmppConnection().getChatManager();
            Chat newChat = chatmanager.createChat(group.getId() + "@120.27.49.173",null);
            sendText(Url, newChat,type);
        } else {
            try {
                Log.e("已发送","xxxxxx");
                //MyFunction.getMultiUserChat().sendMessage(Url + type);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MyHttp.SendMessage(UserInfo.getId(),UserInfo.getGroupId(),Url+type+"size"+time_Long);
                    }
                }).start();
            } catch (Exception e) {
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
            newChat.sendMessage(text+type+"size"+time_Long);

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
        final View view = View.inflate(this, R.layout.dialog_recordbutton_alert_dialog, null);
        mPop = new PopupWindowFactory(this,view);
        mImageView = (ImageView) view.findViewById(R.id.zeffect_recordbutton_dialog_imageview);
        mTextView = (TextView) view.findViewById(R.id.zeffect_recordbutton_dialog_time_tv);
        mAudioRecoderUtils = new AudioRecoderUtils();
        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                //根据分贝值来设置录音时话筒图标的上下波动，下面有讲解
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(MyFunction.getDate(time));
                time_Long=(int)(time/1000)+"";
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                //发送语音
                sendVoice(filePath);
                mTextView.setText("00:00");
            }
        });
        chatAdpter.setExpendImage(this);
    }

    //初始化消息列表
    private void initListView() {
        msg_chatList=new SharedData(SendMessage.this).getData(group.getId());
        if(msg_chatList==null) {
            msg_chatList = new ArrayList<Msg_chat>();
        }
        setMsgStatus();
        chatAdpter=new ChatAdpter(SendMessage.this,0,msg_chatList);
        lv_msg.setAdapter(chatAdpter);
        tv_name.setText(group.getId());
        iv_heagImage.setClipToOutline(true);
        iv_heagImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        if(group.getDrawable()==null)
            Picasso.with(this).load(R.mipmap.default_headimage).into(iv_heagImage);
        else
            iv_heagImage.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(group.getDrawable()));
        lv_msg.setSelection(chatAdpter.getCount()-1);
        //单人聊天的头像
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
        //发送
        iv_sound.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String path=null;
                if(MyFunction.isIntenet(SendMessage.this)&&UserInfo.canDo()){
                    String text=et_text_send.getText().toString();
                    if(change){
                        initSendMsg(text,0,text);
                        return false;
                    } else {//录音
                        MyFunction.muteAudioFocus(SendMessage.this,true);
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                y=event.getRawY();
                                time = System.currentTimeMillis();
                                mPop.showAtLocation(ll_bg, Gravity.CENTER,0,0);
                                try {
                                    mAudioRecoderUtils.startRecord();
                                }catch (RuntimeException e){
                                    e.printStackTrace();
                                    Snackbar.make(iv_sound,"先允许调用系统录音权限",Snackbar.LENGTH_SHORT).show();
                                }
                                iv_sound.startAnimation(expend);
                                break;
                            case MotionEvent.ACTION_UP:
                                if(System.currentTimeMillis()-time<1000){
                                    Snackbar.make(iv_sound,"录音时间过短，请重试",Snackbar.LENGTH_SHORT).show();
                                    mAudioRecoderUtils.cancelRecord();
                                    mPop.dismiss();
                                    iv_sound.startAnimation(noexpend);
                                    break;
                                }else if(y-event.getRawY()>300){
                                    Snackbar.make(iv_sound,"已取消发送语音",Snackbar.LENGTH_SHORT).show();
                                    mAudioRecoderUtils.cancelRecord();
                                    mPop.dismiss();
                                    iv_sound.startAnimation(noexpend);
                                    break;
                                }else {
                                    try{
                                        mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Snackbar.make(iv_sound,"先允许调用系统录音权限",Snackbar.LENGTH_SHORT).show();
                                    }
                                    mPop.dismiss();
                                    iv_sound.startAnimation(noexpend);
                                    break;
                                }
                            case MotionEvent.ACTION_CANCEL:
                                mAudioRecoderUtils.cancelRecord(); //取消录音（不保存录音文件）
                                mPop.dismiss();
                                iv_sound.startAnimation(noexpend);
                                break;
                        }

                    }
                }else if(MyFunction.isIntenet(SendMessage.this)){
                    Toast.makeText(SendMessage.this,"重新连接中",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


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
    //上传发送语音
    private void sendVoice(final String path) {
        try {
            RECORD_NAME=MyFunction.getTimeName();
            AVFile avFile=AVFile.withAbsoluteLocalPath("record.amr",path);
            AVObject avObject = new AVObject("Record");
            avObject.put("record", avFile);
            avObject.put("user",UserInfo.getId());
            avObject.put("name",RECORD_NAME);
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e==null){
                        File file1=new File(path);
                        if(file1.exists())
                            file1.delete();
                        getRecordUrl(time_Long,RECORD_NAME);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                myTime.getSecond() + group.getId() + ".JPEG";
    }
    @Override
    public void onBackPressed() {
        if(ll_add.getVisibility()==View.VISIBLE){
            iv_add.startAnimation(overadd);
            et_text_send.setInputType(InputType.TYPE_CLASS_TEXT);
            ll_add.setVisibility(View.GONE);
            return;
        }

        ChatService.savaDate(chatAdpter.getMsg_chats(),group.getId());
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
                        avObject.put("name", group.getId()+ date);
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
        query.whereEqualTo("name", group.getId() + name);
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

    private void getRecordUrl(final String path, String name) {
        AVQuery<AVObject> query = new AVQuery<>("Record");
        query.whereEqualTo("name", name);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是符合条件的第一个 AVObject
                if(e!=null||avObject==null)
                    return;
                String RecordUrl = avObject.getAVFile("record").getUrl();
                if (RecordUrl != null)
                    initSendMsg(RecordUrl,2,path);
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
            avObject.put("user",UserInfo.getId());
            avObject.put("name", group.getId()+ name);
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        File file1=new File(fileName);
                        /*if(file1.exists())
                            file1.delete();*/
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

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(SendMessage.this,"保存成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
