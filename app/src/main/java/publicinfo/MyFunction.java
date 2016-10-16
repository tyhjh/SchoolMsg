package publicinfo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage;
import com.tyhj.myfist_2016_6_29.MyTime;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apis.userAndRoom.User;
import myViews.SharedData;

/**
 * Created by Tyhj on 2016/10/9.
 */

public class MyFunction {
    public static MultiUserChat multiUserChat;

    public static MultiUserChat getMultiUserChat() {
        return multiUserChat;
    }

    public static void setMultiUserChat(MultiUserChat multiUserChat) {
        MyFunction.multiUserChat = multiUserChat;
    }

    private static String chatName;

    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MyFunction.user = user;
    }

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static String getChatName() {
        return chatName;
    }

    public static void setChatName(String chatName) {
        MyFunction.chatName = chatName;
    }
    public static void setContext(Context context) {
        MyFunction.context = context;
    }

    //轮廓
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ViewOutlineProvider getOutline(boolean b, final int x, final int y){
        if(b) {
            return  new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int margin = Math.min(view.getWidth(), view.getHeight()) / x;
                    outline.setOval(margin, margin, view.getWidth() - margin, view.getHeight() - margin);
                }
            };
        }else {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int margin = Math.min(view.getWidth(), view.getHeight()) / x;
                    outline.setRoundRect(margin, margin, view.getWidth() - margin, view.getHeight() - margin, y);
                    //outline.setOval(margin, margin, view.getWidth() - margin, view.getHeight() - margin);
                }
            };
        }

    }


    //是否有网络
    public static boolean isIntenet(Context context){
        ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi||internet){
            return true;
        }else {
            Toast.makeText(context, context.getString(R.string.nointernet),Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //手机号是否正确
    public static boolean isMobileNO(String mobiles,Context context) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        boolean is=m.matches();
        if(!is)
            Toast.makeText(context,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
        return is;
    }
    //邮箱是否正确
    public static boolean isEmail(String email,Context context) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        boolean is=m.matches();
        if(!is)
            Toast.makeText(context,"请输入正确的Email地址",Toast.LENGTH_SHORT).show();
        return is;
    }
    //获取时间
    public static String getTime(int time){
        int ca=getTime()-time;
        if(ca<10){
            return null;
        }else if(ca>10){
            return "刚刚";
        }
        return null;
    }

    public static String getTime2(int time){
        int ca=getTime()-time;
        int ca2=(getTime()/10000)-(time/10000);
        String str=time+"";
        if(ca2==1){
            return ("昨天 • "+str.substring(4,6)+":"+str.substring(6,8));
        }else if(ca2>1){
            return (str.substring(0,2)+"月"+str.substring(2,4)+"日 "+str.substring(4,6)+":"+str.substring(6,8));
        }else if(ca<=10){
            return ("刚刚");
        }else if(ca<500){
            return (str.substring(4,6)+":"+str.substring(6,8));
        }else {
            return ("今天 • "+str.substring(4,6)+":"+str.substring(6,8));
        }
    }

    public static int getTime(){
        MyTime myTime=new MyTime();
        return Integer.parseInt(myTime.getMonth()+myTime.getDays()+myTime.getHour()+myTime.getMinute());
    }
}
