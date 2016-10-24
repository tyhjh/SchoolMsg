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
    View view;
    NotificationAdpter notification;
    List<Notice> notices;
    IntentFilter intentFilter;
    Msg.MsgBoradCastReceiver msgBoradCastReceiver;
    Notice addnotice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signBroadCast();
        notices=new ArrayList<Notice>();
    }

    @ViewById
    SwipeMenuRecyclerView rcly_notice;


    @AfterViews
    void afterView(){
        addLandGroup();
        rcly_notice.setSwipeMenuCreator(swipeMenuCreator);
        notification=new NotificationAdpter(getActivity(),notices);
        rcly_notice.setAdapter(notification);
        rcly_notice.setLayoutManager(new LinearLayoutManager(getActivity()));// 布局管理器。
        rcly_notice.setSwipeMenuItemClickListener(menuItemClickListener);
    }

    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String id=intent.getStringExtra("friendApply");
            addNotice(id);
        }
    }

    @Background
    public void addNotice(String id) {
        Notice notice=new Notice("来自 "+id+" 的好友申请",id,id+" 想把你加为好友", MyFunction.getTime(), UserInfo.getUserImage(id),0,0);
        addnotice=notice;
        handler.sendEmptyMessage(1);
    }

    //注册广播
    private void signBroadCast() {
        msgBoradCastReceiver=new Msg.MsgBoradCastReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("boradcast.action.FRIENDAPPLY");
        getActivity().registerReceiver(msgBoradCastReceiver,intentFilter);
    }

    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem addItem = new SwipeMenuItem(getActivity())
                    //.setImage(R.mipmap.ic_action_delete) // 图标。
                    .setBackgroundDrawable(R.drawable.bg_agree)
                    .setText("同意并添加") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(350)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(addItem);// 添加一个按钮到右侧侧菜单。.
            SwipeMenuItem refuseItem = new SwipeMenuItem(getActivity())
                    //.setImage(R.mipmap.ic_action_delete) // 图标。
                    .setBackgroundDrawable(R.drawable.bg_disagree)
                    .setText("拒绝") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(200)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(refuseItem);// 添加一个按钮到右侧侧菜单。
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                    //.setImage(R.mipmap.ic_action_delete) // 图标。
                    .setBackgroundDrawable(R.drawable.bg_delete)
                    .setText("删除消息") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(300)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.
        }
    };
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if(menuPosition==0){
                //同意
                notices.get(adapterPosition).setStatus(2);
                notification.notifyDataSetChanged();
                Intent intent=new Intent("boradcast.action.UPDATELIST");
                intent.putExtra("updateList",1);
                getActivity().sendBroadcast(intent);

            }else if(menuPosition==1){
                //拒绝
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo.removeUser(notices.get(adapterPosition).getId());
                    }
                }).start();
                notices.get(adapterPosition).setStatus(1);
                notification.notifyDataSetChanged();

            }else if (menuPosition == 2) {// 删除按钮被点击
                //拒绝
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       // UserInfo.removeUser(notices.get(adapterPosition).getId());
                    }
                }).start();
                notices.remove(adapterPosition);
                notification.notifyItemRemoved(adapterPosition);
            }


        }
    };

    @Override
    public void onPause() {
        super.onPause();
        ChatService.savaNoticeDate(notices);
        Log.e("xxxx","执行了"+notices.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(msgBoradCastReceiver!=null)
        getActivity().unregisterReceiver(msgBoradCastReceiver);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    notices.add(0,addnotice);
                    notification.notifyItemInserted(0);
                    break;
            }
        }
    };


    private void addLandGroup() {
        List<Notice> noticesLand = new SharedData(getActivity()).getNotices();
        if (noticesLand != null) {
            for (int i = 0; i < noticesLand.size(); i++) {
                notices.add(noticesLand.get(i));
            }
        }
    }

}
