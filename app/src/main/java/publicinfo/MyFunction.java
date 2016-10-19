package publicinfo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tyhj.myfist_2016_6_29.MyTime;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apis.userAndRoom.User;
import myViews.SharedData;

/**
 * Created by Tyhj on 2016/10/9.
 */

public class MyFunction {

    private  static boolean canServer;

    public static boolean isCanServer() {
        return canServer;
    }

    public static void setCanServer(boolean canServer) {
        MyFunction.canServer = canServer;
    }

    private static boolean ifstart=true;

    private static int IMAGE_SIZE=300;

    private static boolean lost;

    private static boolean reConnect;

    private static boolean isConnect;

    private static UserInfo userInfo;

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        MyFunction.userInfo = userInfo;
    }

    public static MultiUserChat multiUserChat;

    public static MultiUserChat getMultiUserChat() {
        return multiUserChat;
    }

    public static void setMultiUserChat(MultiUserChat multiUserChat) {
        MyFunction.multiUserChat = multiUserChat;
    }

    private static String chatName;

    private static User user;

    private static List<Picture> pictureList;

    public static List<Picture> getPictureList() {
        return pictureList;
    }

    public static void setPictureList(List<Picture> pictureList) {
        MyFunction.pictureList = pictureList;
    }

    public static boolean isReConnect() {
        return reConnect;
    }

    public static void setReConnect(boolean reConnect) {
        MyFunction.reConnect = reConnect;
    }

    public static void setIsConnect(boolean isConnect) {
        MyFunction.isConnect = isConnect;
    }

    public static boolean isConnect() {
        return isConnect;
    }

    public static void setConnect(boolean connect) {
        isConnect = connect;
    }

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

    //展示图片的设置
    public static DisplayImageOptions getOption(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_image)
                .showImageOnFail(R.mipmap.nomsg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
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

    //是否有网络
    public static boolean isIntenet(){
        ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if(wifi||internet){
            return true;
        }else {
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
    //从uri得到path
    public static String getFilePathFromContentUri(Uri uri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    //图片压缩
    public static void ImgCompress(String filePath,File newFile) {
        int imageMg=100;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //规定要压缩图片的分辨率
        options.inSampleSize = calculateInSampleSize(options,720,1280);
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageMg, baos);
        //如果文件大于100KB就进行质量压缩，每次压缩比例增加百分之五
        while (baos.toByteArray().length / 1024 > IMAGE_SIZE&&imageMg>50){
            baos.reset();
            imageMg-=5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageMg, baos);
        }
        //然后输出到指定的文件中
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    //文件复制
    public static void copyFile(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
//判断 服务 是否运行
    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }
}
