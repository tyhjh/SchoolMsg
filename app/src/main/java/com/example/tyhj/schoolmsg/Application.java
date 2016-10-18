package com.example.tyhj.schoolmsg;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import publicinfo.MyFunction;

public class Application extends android.app.Application {
    private int activityCount;//activity的count数
    private static boolean isForeground=true;//是否在前台

    private static int count=0;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                isForeground = true;
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
}