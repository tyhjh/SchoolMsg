package fragements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import adpter.NotificationAdpter;
import myViews.SharedData;
import publicinfo.Group;
import publicinfo.MyFunction;
import publicinfo.Notice;
import publicinfo.UserInfo;
import service.ChatService;

@EFragment(R.layout.fragment_msg)
public class Msg extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterView(){
    }

    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Background
    public void addNotice(String id) {
       /* Notice notice=new Notice("来自 "+id+" 的好友申请",id,id+" 想把你加为好友", MyFunction.getTime(), UserInfo.getUserImage(id),0,0);
        addnotice=notice;
        handler.sendEmptyMessage(1);*/
    }

    //注册广播
    private void signBroadCast() {
        /*msgBoradCastReceiver=new Msg.MsgBoradCastReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("boradcast.action.FRIENDAPPLY");
        getActivity().registerReceiver(msgBoradCastReceiver,intentFilter);*/
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    break;
            }
        }
    };


    private void addLandGroup() {
        List<Notice> noticesLand = new SharedData(getActivity()).getNotices();
        if (noticesLand != null) {

        }
    }

}
