package service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;

import com.example.tyhj.schoolmsg.SendMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

public class ChatService extends Service {

    Vibrator vibrator;

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

    public void sendBroadCast(Message message,String divi) {
        int type=Integer.parseInt(message.getBody().substring(message.getBody().length()-1,message.getBody().length()));
        String messageBody = message.getBody().substring(0,message.getBody().length()-1);
        String messageFrom=message.getFrom().substring(0,message.getFrom().lastIndexOf(divi));

        Msg_chat msg_chat=new Msg_chat(2,type,0,messageBody,null,null,messageFrom, MyFunction.getTime());
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
        vibrator = (Vibrator)getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        vibrator.vibrate(Long.parseLong("300"));
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(runnable).start();
        return super.onStartCommand(intent, flags, startId);
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



    private Runnable runnable = new Runnable() {

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

}
