package publicinfo;

import android.content.Context;
import android.content.SharedPreferences;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import apis.conf.SMKProperties;
import service.ChatService;

/**
 * Created by Tyhj on 2016/10/16.
 */

public class UserInfo {
    private int SERVER_PORT=SMKProperties.PORT;
    private static String SERVER_HOST=SMKProperties.HOST;
    private String SERVER_NAME=SMKProperties.HOSTNAME;

    static{
        try{
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //名字
    String name;
    //id
    String id;
    //连接
    static XMPPConnection xmppConnection;
    // 加入组的名字
    String groupName;
    //



    //登陆
    public static synchronized boolean Login(String name,String pas,Context context){
        try {
            if(!MyFunction.isIntenet(context))
                return false;
            XMPPConnection conn = new XMPPConnection(SERVER_HOST);
            conn.connect();
            conn.login(name+"@"+SERVER_HOST, pas);
            Presence presence = new Presence(Presence.Type.available);
            conn.sendPacket(presence);
            xmppConnection=conn;
            SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("name",name);
            editor.putString("pas",pas);
            editor.commit();
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }

    //重新登陆
    public static synchronized boolean reLogin(Context context){
        try {
            if(!MyFunction.isIntenet())
                return false;

            if(MyFunction.isServiceRun(context,"service.ChatService")){
                xmppConnection= ChatService.Getconnection();
            }

            XMPPConnection conn = new XMPPConnection(SERVER_HOST);
            conn.connect();
            SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
            conn.login(shared .getString("name",null)+"@"+SERVER_HOST, shared.getString("pas",null));
            Presence presence = new Presence(Presence.Type.available);
            conn.sendPacket(presence);



            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //




}
