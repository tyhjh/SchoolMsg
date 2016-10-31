package com.example.tyhj.schoolmsg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;

import myViews.CircularAnim;
import publicinfo.Group;
import publicinfo.MyFunction;
import service.ChatService;

public class Application extends android.app.Application {
    private int activityCount;//activity的count数
    private static boolean isForeground=true;//是否在前台
    private static int count=0;
    private static Context context;
    private static Activity activity,activity2;
    private static Group group;
    @Override
    public void onCreate() {
        super.onCreate();
        CircularAnim.init(700, 500, R.color.color2from);
        //如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize(this, "Yi6HruJsj4h2bufroQKC9kJT-gzGzoHsz", "2nWoF8MhHN6kibFs72bVhLWV");
        this.context=getApplicationContext();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                Application.activity2=activity;
                Application.activity=activity;
                isForeground = true;
                if(ChatService.getNotifiManager()!=null)
                ChatService.getNotifiManager().cancelAll();
                count=0;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (0 == activityCount) {
                    isForeground = false;
                    count=0;
                }
                Application.activity=activity2;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public static boolean getIsLeave(){
        return isForeground;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Application.count = count;
    }

    public static Context getContext(){
        return context;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static Group getGroup() {
        return group;
    }

    public static void setGroup(Group group) {
        Application.group = group;
    }
}