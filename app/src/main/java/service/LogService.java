package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;

import apis.connection.XmppConnection;
import apis.userAndRoom.User;
import publicinfo.MyFunction;

import static android.content.ContentValues.TAG;

/**
 * Created by Tyhj on 2016/10/18.
 */

public class LogService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences shared=getSharedPreferences("login", Context.MODE_PRIVATE);
        final String name=shared.getString("name",null);
        final String pas=shared.getString("pas",null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = new User(XmppConnection.getInstance());
                MyFunction.setUser(user);
                if(MyFunction.getUser()!=null&&MyFunction.getUser().login(name, pas))
                    MyFunction.setCanServer(true);
                else {
                    //handler.sendEmptyMessage(1);
                    while (MyFunction.getUser()==null||!MyFunction.getUser().login(name, pas)){
                        user = new User(XmppConnection.getInstance());
                        MyFunction.setUser(user);
                    }
                    MyFunction.setCanServer(true);
                }
            }
        }).start();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(),getText(R.string.nointernet),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
