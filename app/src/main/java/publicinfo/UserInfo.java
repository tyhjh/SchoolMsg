package publicinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.tyhj.myfist_2016_6_29.MyTime;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import service.ChatService;

/**
 * Created by Tyhj on 2016/10/16.
 */

public class UserInfo {
    private static int SERVER_PORT=5222;
    private static String SERVER_HOST="120.27.49.173";
    private static String SERVER_NAME="120.27.49.173";
    public static boolean canDo(){
        if(xmppConnection!=null&&xmppConnection.isConnected())
            return true;
        else
            return false;
    }

    static{
        try{
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //名字
    static String name;
    //id
    static String id;
    //连接
    static XMPPConnection xmppConnection;
    // 加入组的名字
    static String groupName;
    //
    private static void setXmppConnection(){
        try{
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        }catch(Exception e){
            e.printStackTrace();
        }
        XMPPConnection conn;
        ConnectionConfiguration config = new ConnectionConfiguration(
                SERVER_HOST, SERVER_PORT,SERVER_NAME);
        config.setReconnectionAllowed(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setSASLAuthenticationEnabled(false);
        conn = new XMPPConnection(config);
        try {
            conn.connect();
            xmppConnection=conn;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public static void setXmppConnection(XMPPConnection xmppConnection) {
        UserInfo.xmppConnection = xmppConnection;
    }

    //登陆
    public static synchronized boolean Login(String name,String pas,Context context){
        try {
            if(!MyFunction.isIntenet(context))
                return false;

            if(!canDo())
                setXmppConnection();
            xmppConnection.login(name, pas);
            Presence presence = new Presence(Presence.Type.available);
            xmppConnection.sendPacket(presence);
            SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("name",name);
            editor.putString("pas",pas);
            editor.commit();
            UserInfo.id=name;
            UserInfo.groupName=name+MyFunction.getTime();
            return true;

        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }

    //重新连接
    public static synchronized boolean reLogin(Context context){
        try {
            if(!MyFunction.isIntenet(context,null))
                return false;

            if(MyFunction.isServiceRun(context,"service.ChatService")){
                xmppConnection= ChatService.getConnection();
                return true;
            }

            SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
            UserInfo.id=shared.getString("name",null);
            UserInfo.groupName=id+MyFunction.getTime();


            if(!canDo())
                setXmppConnection();

            xmppConnection.login(id, shared.getString("pas",null));
            Presence presence = new Presence(Presence.Type.available);
            xmppConnection.sendPacket(presence);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //注册
    public static boolean Register(String account,String password,Context context){
        if(!MyFunction.isIntenet(context,null))
            return false;
        setXmppConnection();
        if(xmppConnection==null)
            return false;
        AccountManager manager = new AccountManager(xmppConnection);
        try {
            manager.createAccount(account, password);
        } catch (XMPPException e) {
            return false;
        }
        return true;
    }

    //退出登录
    public static void logout(Context context){
        SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.commit();
        shared = context.getSharedPreferences("chat_date", Context.MODE_PRIVATE);
         editor = shared.edit();
        editor.clear();
        editor.commit();
        if(UserInfo.canDo())
            xmppConnection.disconnect();
        Intent intent=new Intent(context, ChatService.class);
        context.stopService(intent);
    }

    //强制下线
    public static void logoutFore(Context context){
        SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.commit();
        if(xmppConnection.isConnected())
            xmppConnection.disconnect();
        Intent intent=new Intent(context, ChatService.class);
        context.stopService(intent);
    }

    public static String getName() {
        return name;
    }

    public static String getId() {
        return id;
    }

    public static XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    public static String getGroupName() {
        return groupName;
    }

}
