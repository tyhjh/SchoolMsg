package myViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import publicinfo.GetChatMsg;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.UserInfo;

public  class SharedData {
  
    private Context context;
  
    private SharedPreferences shared;

    private SharedPreferences groupShared;
  
    public SharedData(Context context) {
        this.context = context;
        shared = context.getSharedPreferences("chat_date", Context.MODE_PRIVATE);
        groupShared=context.getSharedPreferences("group_date",Context.MODE_PRIVATE);
    }  
  
    public void saveData(List<Msg_chat> msg_chats,String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流  
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流  
            oos.writeObject(msg_chats);
            // 将字节流编码成base64的字符串  
            String oAuth_Base64 = new String(Base64.encodeBase64(baos
                    .toByteArray()));  
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(name, oAuth_Base64);
            editor.commit();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public List<Msg_chat> getData(String name) {
        List<Msg_chat> chats = null;
        String productBase64 = shared.getString(name, null);
        if(productBase64==null) {
            return null;
        }
        // 读取字节  
        byte[] base64 = Base64.decodeBase64(productBase64.getBytes());
        // 封装到字节流  
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {  
            // 再次封装  
            ObjectInputStream bis = new ObjectInputStream(bais);
            // 读取对象  
            chats = (List<Msg_chat>) bis.readObject();
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
        Log.e("xxxxxxx","数据库获取成功"+chats.size());
        return chats;
    }

    public void savaGrops(List<Group> groups){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(groups);
            // 将字节流编码成base64的字符串
            String oAuth_Base64 = new String(Base64.encodeBase64(baos
                    .toByteArray()));
            SharedPreferences.Editor editor = groupShared.edit();
            editor.putString(UserInfo.getId(), oAuth_Base64);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addGrops(Group group){
        List<Group> groups=getGroups();
        if(groups==null)
            groups=new ArrayList<Group>();
        if(!groups.contains(group))
            groups.add(group);
        savaGrops(groups);
    }

    public List<Group> getGroups() {
        List<Group> chats = null;
        String productBase64 = groupShared.getString(UserInfo.getId(), null);
        if(productBase64==null) {
            return null;
        }
        // 读取字节
        byte[] base64 = Base64.decodeBase64(productBase64.getBytes());
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            // 读取对象
            chats = (List<Group>) bis.readObject();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return chats;
    }
}  