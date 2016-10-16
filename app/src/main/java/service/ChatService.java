package service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.example.tyhj.schoolmsg.SendMessage;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;

import apis.userAndRoom.ChatRoom;
import myViews.SharedData;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;

public class ChatService extends Service {
    Vibrator vibrator;
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
