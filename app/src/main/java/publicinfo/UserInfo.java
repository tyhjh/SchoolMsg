package publicinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tyhj.myfist_2016_6_29.MyTime;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import api.File2Bytes;
import api.FormatTools;
import service.ChatService;

/**
 * Created by Tyhj on 2016/10/16.
 */

public class UserInfo {
    private static int SERVER_PORT=5222;
    private static String SERVER_HOST="120.27.49.173";
    private static String SERVER_NAME="120.27.49.173";


    public static boolean canDo(){
        if(xmppConnection!=null&&xmppConnection.isConnected()&&xmppConnection.isAuthenticated())
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
            if(!MyFunction.isIntenet(context,null))
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
        shared.edit().clear().commit();

        shared = context.getSharedPreferences("chat_date", Context.MODE_PRIVATE);
        shared.edit().clear().commit();

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

    //修改密码
    public static boolean changePassword(Context context,String pwd,String prepas,boolean b) {
        SharedPreferences shared=context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String pas=shared.getString("pas",null);
        if (!canDo())
            return false;
        if(b&&pas!=null){
            try {
                xmppConnection.getAccountManager().changePassword(pwd);
                return true;
            } catch (XMPPException e) {
                return false;
            }
        }else if(pas!=null){
            if(pas.equals(prepas)){
                try {
                    xmppConnection.getAccountManager().changePassword(pwd);
                    return true;
                } catch (XMPPException e) {
                    return false;
                }
            }else
                return false;
        }
     return false;
    }

    //查询用户
    public static List<HashMap<String, String>> searchUsers(String userName) {
        if (!canDo())
            return null;
        HashMap<String, String> user = null;
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        try {
            new ServiceDiscoveryManager(xmppConnection);
            UserSearchManager usm = new UserSearchManager(xmppConnection);
            Form searchForm = usm.getSearchForm(xmppConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("userAccount", true);
            answerForm.setAnswer("userPhote", userName);
            ReportedData data = usm.getSearchResults(answerForm, xmppConnection.getServiceName());

            Iterator<ReportedData.Row> it = data.getRows();
            ReportedData.Row row = null;
            while (it.hasNext()) {
                user = new HashMap<String, String>();
                row = it.next();
                user.put("userAccount", row.getValues("userAccount").next()
                        .toString());
                Log.e("获取的东西",row.getValues("userAccount").next().toString());
                user.put("userPhote", row.getValues("userPhote").next().toString());
                Log.e("获取的东西",row.getValues("userPhote").next().toString());
                results.add(user);
                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
            }
            Log.e("my=没有","哈哈哈哈哈哈哈");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return results;
    }

    //获取用户信息
    public VCard getUserVCard(String user) {
        if (!canDo())
            return null;
        VCard vcard = new VCard();
        try {
            vcard.load(xmppConnection, user);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        Log.e("获取的东西",vcard.getNickName());
        return vcard;
    }

    //添加好友
    public static void addUser(String userName) {
        if (!canDo())
            return;
        Presence subscription=new Presence(Presence.Type.subscribe);
        subscription.setTo(userName+"@"+xmppConnection.getServiceName());
        xmppConnection.sendPacket(subscription);
    }

    //同意添加好友并添加对方为好友
    public static void agreeAdd(String from){
        if(!canDo())
            return;
        Presence reSubscription=new Presence(Presence.Type.subscribe);
        reSubscription.setTo(from);
       xmppConnection.sendPacket(reSubscription);
    }

    //获取好友状态
    public int IsUserOnLine(String user) {
        String url = "http://"+SERVER_HOST+":9090/plugins/presence/status?" +
                "jid="+ user +"@"+ SERVER_NAME +"&type=xml";
//        System.out.println(url);
        int shOnLineState = 0; // 不存在
        try {
            URL oUrl = new URL(url);
            URLConnection oConn = oUrl.openConnection();
            if (oConn != null) {
                BufferedReader oIn = new BufferedReader(new InputStreamReader(
                        oConn.getInputStream()));
                if (null != oIn) {
                    String strFlag = oIn.readLine();
                    oIn.close();
                    System.out.println("strFlag"+strFlag);
                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
                        shOnLineState = 2;
                    }
                    if (strFlag.indexOf("type=\"error\"") >= 0) {
                        shOnLineState = 0;
                    } else if (strFlag.indexOf("priority") >= 0
                            || strFlag.indexOf("id=\"") >= 0) {
                        shOnLineState = 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shOnLineState;
    }

    //设置昵称
    public static void setNickName(String nickName){
        VCard vCard1 = new VCard();
        vCard1.setNickName(nickName);
        try {
            vCard1.save(xmppConnection);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    //获取昵称
    public static String getNickName(String name){
        if(!canDo())
            return null;
        VCard vCard = new VCard();
        try {
            vCard.load(xmppConnection,name+SERVER_HOST);
        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        }
        return vCard.getNickName();
    }

    //获取用户头像
    public static byte[] getUserImage(String user) {
        if(!canDo())
            return null;
        ByteArrayInputStream bais = null;
        try {
            VCard vcard = new VCard();
            // 加入这句代码，解决No VCard for
            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
                    new org.jivesoftware.smackx.provider.VCardProvider());
            vcard.load(xmppConnection, user+"@"+xmppConnection.getServiceName());
            if (vcard == null || vcard.getAvatar() == null)
                return null;
            bais = new ByteArrayInputStream(vcard.getAvatar());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bais == null)
            return null;
        Drawable drawable=FormatTools.getInstance().InputStream2Drawable(bais);
        return getPicture(drawable);
    }

    //修改用户头像
    public static boolean changeImage(File file) {
        if (!canDo())
            return false;
        try {
            VCard vcard = new VCard();
            vcard.load(xmppConnection);
            byte[] bytes;
            bytes = File2Bytes.getFileBytes(file);
            String encodedImage = StringUtils.encodeBase64(bytes);
            vcard.setAvatar(bytes);
            vcard.setEncodedImage(encodedImage);
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
                    + encodedImage + "</BINVAL>", true);
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    vcard.getAvatar());
            vcard.save(xmppConnection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //修改用户头像
    public static boolean changeImage(byte[] bytes) {
        if (!canDo())
            return false;
        try {
            VCard vcard = new VCard();
            vcard.load(xmppConnection);
            String encodedImage = StringUtils.encodeBase64(bytes);
            vcard.setAvatar(bytes);
            vcard.setEncodedImage(encodedImage);
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
                    + encodedImage + "</BINVAL>", true);
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    vcard.getAvatar());
            vcard.save(xmppConnection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //删除好友
    public static boolean removeUser(String userName) {
        try {
            if (userName.contains("@")) {
                userName = userName.split("@")[0];
            }
            Roster roster=xmppConnection.getRoster();
            RosterEntry entry = roster.getEntry(userName);
            System.out.println("删除好友：" + userName);
            System.out.println("User." + roster.getEntry(userName) == null);
            roster.removeEntry(entry);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取好友列表
    public static List<RosterEntry> getAllEntries() {
        if(!canDo())
            return null;
        Roster roster=xmppConnection.getRoster();
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()) {
            Entrieslist.add(i.next());
        }
        return Entrieslist;
    }


    private static List<Group> groups;

    public static List<Group> getMyGroups() {
        return groups;
    }

    public static void setMyGroups(List<Group> groups) {
        UserInfo.groups = groups;
    }

    public static void addGroup(Group group){
        groups.add(group);
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

    //将drawable转换成可以用来存储的byte[]类型
    public static byte[] getPicture(Drawable drawable) {
             if(drawable == null) {
                        return null;
                     }
               BitmapDrawable bd = (BitmapDrawable) drawable;
             Bitmap bitmap = bd.getBitmap();
             ByteArrayOutputStream os = new ByteArrayOutputStream();
                 bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                 return os.toByteArray();
            }

}
