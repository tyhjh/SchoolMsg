package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.tyhj.schoolmsg.Application;
import com.example.tyhj.schoolmsg.Home_;
import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tyhj.myfist_2016_6_29.MyTime;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import apis.conf.SMKProperties;
import apis.userAndRoom.ChatRoom;
import myViews.SharedData;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import publicinfo.Picture;

import static android.content.ContentValues.TAG;

public class ChatService extends Service {

    Vibrator vibrator;

    Runnable runnable;

    private static Handler handler = new Handler();

    public ChatService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String path = Environment.getExternalStorageDirectory() + "/ASchollMsg";
        File file=new File(path);
        if(!file.exists())
            file.mkdirs();


        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(getApplicationContext());
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        getPhotos();
        MyFunction.setConnect(true);
        if (MyFunction.getUser().getXmppConnection().getConnection()!=null&&MyFunction.getUser().getXmppConnection().getConnection().isConnected()) {
            MyFunction.getUser().getXmppConnection().getConnection().addConnectionListener(connectionListener);
        }


            MyFunction.getUser().getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        sendBroadCast(message,"@120.27.49.173");
                    }
                });
            }
        });


        MultiUserChat multiUserChat = new ChatRoom(MyFunction.getUser().getXmppConnection().getConnection()).joinMultiUserChat("tyhj2","111","111");
        MyFunction.setMultiUserChat(multiUserChat);
        multiUserChat.addMessageListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                Message message = (Message)packet;
                //接收来自聊天室的聊天信息
                System.out.println(message.getFrom()+":"+message.getBody());
                if(!message.getFrom().equals("111@conference.120.27.49.173/tyhj2"))
                sendBroadCast(message,"@conference.120.27.49.173");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //new Thread(runnable).start();
        return START_STICKY_COMPATIBILITY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    public static ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void reconnectionSuccessful() {
            MyFunction.setConnect(true);
            Log.i("connection", "reconnectionSuccessful");
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Log.i("connection", "reconnectionFailed");
            MyFunction.setConnect(false);
        }

        @Override
        public void reconnectingIn(int arg0) {
            MyFunction.setConnect(false);
            Log.i("connection", "reconnectingIn");
        }

        @Override
        public void connectionClosed() {
            MyFunction.setConnect(false);
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            MyFunction.setConnect(false);
            Log.i("connection", "connectionClosedOnError");

        }
    };

    private Runnable getRun(){
        runnable = new Runnable() {

            @Override
            public void run() {
                // 每一分钟发送一次包,确保连接
                Presence presence = new Presence(Presence.Type.available);
                if (MyFunction.isIntenet(getApplicationContext())) {
                    MyFunction.getUser().getXmppConnection().getConnection().sendPacket(presence);
                } else {
                    Log.d("TestApp", "已经发送包确认");

                    // 以指定的时间间隔执行
                    handler.postDelayed(this, 60 * 1000);
                    if (MyFunction.getUser().getXmppConnection().getConnection().isAuthenticated() && MyFunction.getUser().getXmppConnection().getConnection().isSendPresence()) {
                        Log.d("TestApp", "连接正常,发包正常!");
                    }
                    // 以指定的时间执行
                    //handler.postAtTime(r, uptimeMillis);
                    // 监听状态
                    MyFunction.getUser().getXmppConnection().getConnection().addConnectionListener(new ConnectionListener() {

                        @Override
                        public void reconnectionSuccessful() {
                            // TODO Auto-generated method stub
                            Log.d("TestApp", "重新登陆成功!");
                        }

                        @Override
                        public void reconnectionFailed(Exception arg0) {
                            // TODO Auto-generated method stub
                            Log.d("TestApp", "重新登陆失败!");
                        }

                        @Override
                        public void reconnectingIn(int arg0) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void connectionClosedOnError(Exception arg0) {
                            // TODO Auto-generated method stub
                            Log.d("TestApp", "连接已关闭!错误!");
                        }

                        @Override
                        public void connectionClosed() {
                            // TODO Auto-generated method stub
                            Log.d("TestApp", "连接已关闭!");
				/*	// 重新发送包!设置在线
					Presence presence = new Presence(Presence.Type.available);
					xmppConnection.sendPacket(presence);*/
                        }
                    });
                }
            }
        };
        return runnable;
    }

    private void getPhotos(){
        int count=50;
        String sdcardPath = Environment.getExternalStorageDirectory().toString();
        List<Picture> pictures=new ArrayList<Picture>();
        ContentResolver mContentResolver = getApplicationContext().getContentResolver();
        Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列

        while (mCursor.moveToNext()&&count>=0) {
            // 打印LOG查看照片ID的值
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.e("MediaStore.Images.Media_ID=", path + "");
            if(path.startsWith(sdcardPath + "/DCIM") || path.startsWith(sdcardPath + "/Pictures")
                    || path.startsWith(sdcardPath + "/tencent/QQfile_recv")|| path.startsWith(sdcardPath + "/tencent/QQ_Images")
                    || path.startsWith(sdcardPath + "/Download")) {
                pictures.add(new Picture(path));
                count--;
            }
        }
        mCursor.close();
        MyFunction.setPictureList(pictures);
    }

    public void sendBroadCast(Message message,String divi) {
        Application.setCount(Application.getCount()+1);
        int type=Integer.parseInt(message.getBody().substring(message.getBody().length()-1,message.getBody().length()));
        String messageBody = message.getBody().substring(0,message.getBody().length()-1);
        String messageFrom=message.getFrom().substring(0,message.getFrom().lastIndexOf(divi));
        Msg_chat msg_chat=new Msg_chat(2,type,0,messageBody,null,null,messageFrom, MyFunction.getTime());

        if(!Application.getIsLeave()){
            vibrator = (Vibrator)getSystemService(getApplicationContext().VIBRATOR_SERVICE);
            vibrator.vibrate(Long.parseLong("300"));
            notificationBar(msg_chat.getName(),msg_chat.getText(),Application.getCount());
        }


        if(MyFunction.getChatName()!=null&&MyFunction.getChatName().equals(messageFrom)){
            Intent intent=new Intent("boradcast.action.GETMESSAGE");
            Bundle bundle=new Bundle();
            bundle.putSerializable("newMsg",new Msg_chat(2,type,2,messageBody,null,null,messageFrom,MyFunction.getTime()));
            intent.putExtras(bundle);
            sendBroadcast(intent);
            return;
        }
        System.out.println(msg_chat.getName()+" xxxx"+msg_chat.getText()+"xxxx"+msg_chat.getType());
        List<Msg_chat> msg_chatList;
        msg_chatList=new SharedData(MyFunction.getContext()).getData(messageFrom);
        if(msg_chatList==null)
            msg_chatList=new ArrayList<Msg_chat>();
        msg_chatList.add(msg_chat);
        new SharedData(MyFunction.getContext()).saveData(msg_chatList,messageFrom);
        Intent intent=new Intent("boradcast.action.GETMESSAGE2");
        sendBroadcast(intent);
    }

    private void notificationBar(String name,String text,int count){
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.ic_camera_24dp);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notify);
        mRemoteViews.setImageViewResource(R.id.iv_headImage,R.mipmap.tyhj);
        mRemoteViews.setTextViewText(R.id.from,"From："+name);
        mRemoteViews.setTextViewText(R.id.text, text);
        mRemoteViews.setTextViewText(R.id.time, new MyTime().getHour()+":"+new MyTime().getSecond());
        mRemoteViews.setTextViewText(R.id.count, count+"");
     /*   mBuilder.setContentTitle("From："+name)//设置通知栏标题
                .setContentText(text) //设置通知栏显示内容
                //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                .setNumber(count) //设置通知集合的数量
                .setTicker("有新的消息") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_image);//设置通知小ICON*/
        Intent intent = new Intent(getApplicationContext(),Home_.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContent(mRemoteViews)
                .setTicker("有新的消息")
                .setPriority(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLargeIcon(bm)
                .setSmallIcon(R.drawable.ic_circle_24dp);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notification);
    }

}
