package fragements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smack.RosterEntry;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import adpter.GroupAdapter;
import myViews.SharedData;
import myinterface.ShowMenu;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import publicinfo.Notice;
import publicinfo.UserInfo;
import service.ChatService;

@EFragment(R.layout.fragment_chat)
public class Chat extends Fragment {
    MsgBoradCastReceiver msgBoradCastReceiver;
    int rotate = 0;
    IntentFilter intentFilter;
    ShowMenu showMenu;
    Animation noserch;
    List<Group> groups, groups_land;
    GroupAdapter groupAdapter, groupAdapter_land;
    String ipAdress = "@120.27.49.173";
    SwipeMenuCreator swipeMenuCreator;
    OnSwipeMenuItemClickListener menuItemClickListener;
    Group addGroup;
    @Background
    public void addGroup(String from) {
        byte[] head= UserInfo.getUserImage(from);
        notify(from, head);
    }
    @UiThread
    public void notify(String from, byte[] head) {
        if(UserInfo.getMyGroups().contains(new Group(from,from,0,head)))
            groups.add(0, new Group(from,from,0,head));
        else
            groups.add(0, new Group(from,from,2,head));
        groupAdapter.notifyItemInserted(0);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noserch = AnimationUtils.loadAnimation(getActivity(), R.anim.noserch);
        noserch.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (rotate == 0)
                    iv_serchqun.setImageResource(R.drawable.ic_close_24dp);
                else
                    iv_serchqun.setImageResource(R.drawable.ic_search_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        swipMenuCreator();
    }

    @ViewById
    LinearLayout ll_serch,ll_view;

    @ViewById
    ImageView iv_showMenu, iv_serchqun;

    @ViewById
    EditText et_serchqun;

    @ViewById
    TextView tv_myqun;

    @ViewById
    RecyclerView  rcly_find_land;

    @ViewById
    SwipeMenuRecyclerView rcly_qun;

    @Click(R.id.iv_showMenu)
    void showMenu() {
        showMenu.showMenu();
    }

    @Click(R.id.iv_serchqun)
    void serchqun() {
        if (et_serchqun.getVisibility() == View.GONE) {
            rotate = 0;
            et_serchqun.setVisibility(View.VISIBLE);
            tv_myqun.setVisibility(View.GONE);
            iv_showMenu.setVisibility(View.GONE);
            iv_serchqun.startAnimation(noserch);
            rcly_qun.setVisibility(View.GONE);
            ll_serch.setVisibility(View.VISIBLE);
        } else {
            rotate = 1;
            et_serchqun.setVisibility(View.GONE);
            tv_myqun.setVisibility(View.VISIBLE);
            iv_showMenu.setVisibility(View.VISIBLE);
            iv_serchqun.startAnimation(noserch);
            rcly_qun.setVisibility(View.VISIBLE);
            ll_serch.setVisibility(View.GONE);
        }
    }

    @AfterTextChange(R.id.et_serchqun)
    void AfterTextChange(TextView editText) {
        String name = editText.getText().toString().trim();
        if(groups!=null)
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getGroupName().equals(name)) {
                if(!groups_land.contains(groups.get(i))) {
                    addGroup = groups.get(i);
                    addGroup.setIsgroup(0);
                    groupAdapter_land.addItem(addGroup);
                }
                return;
            }
        }

        if (name.length() == 3)
            findUser(name);
    }

    @Background
    void findUser(String name) {
        byte[] drawable = UserInfo.getUserImage(name);
        if (drawable != null) {
            Group group = new Group(null, name, 2, drawable);
            if (!groupAdapter_land.getGroups().contains(group)) {
                updateFindUser(groupAdapter_land, group);
            }
        }
    }

    @UiThread
    public void updateFindUser(GroupAdapter adapter, Group group) {
        adapter.addItem(group);
    }

    @AfterViews
    void afterViews() {
        initList();
        intentFilter = new IntentFilter();
        intentFilter.addAction("boradcast.action.FRIENDAPPLY");
        intentFilter.addAction("boradcast.action.GETMESSAGE2");
        msgBoradCastReceiver = new MsgBoradCastReceiver();
        getActivity().registerReceiver(msgBoradCastReceiver, intentFilter);
    }

    //初始化列表布局
    private void initList() {
        groups = new ArrayList<Group>();
        groups_land = new ArrayList<Group>();
        addLandGroup();
        groupAdapter_land = new GroupAdapter(getActivity(), groups_land);
        groupAdapter = new GroupAdapter(getActivity(), groups);
        rcly_find_land.setAdapter(groupAdapter_land);
        rcly_qun.setAdapter(groupAdapter);
        rcly_find_land.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcly_find_land.setItemAnimator(new DefaultItemAnimator());
        rcly_qun.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcly_qun.setItemAnimator(new DefaultItemAnimator());
        rcly_qun.setSwipeMenuCreator(swipeMenuCreator);
        rcly_qun.setSwipeMenuItemClickListener(menuItemClickListener);
        initGroups();
    }

    @Background
    void initGroups() {
        if (UserInfo.canDo()) {
            List<RosterEntry> list = UserInfo.getAllEntries();
            if (list != null) {
                List<Group> groupList=new ArrayList<Group>();
                for (int i = 0; i < list.size(); i++) {
                    String name = list.get(i).getUser();
                    name = name.substring(0, name.indexOf(ipAdress));
                    byte[] drawable = UserInfo.getUserImage(name);
                    Group group=new Group(null,name,0,drawable);
                    groupList.add(group);
                }
                UserInfo.setMyGroups(groupList);
                for(int i=0;i<groupList.size();i++){
                    if(!groups.contains(groupList.get(i)))
                        groups.add(groupList.get(i));
                    else{
                        groups.remove(groups.indexOf(groupList.get(i)));
                        groups.add(groupList.get(i));
                    }
                }
            }
            update();
        }
    }
    @UiThread
    void update() {
        groupAdapter.notifyDataSetChanged();
    }

    private void addLandGroup() {
        List<Group> groupList = new SharedData(getActivity()).getGroups();
        if (groupList != null) {
            for (int i = 0; i < groupList.size(); i++) {
                    groups.add(groupList.get(i));
            }
        }
    }

    public void setShowMenu(ShowMenu showMenu) {
        this.showMenu = showMenu;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (groupAdapter != null) {
            if (et_serchqun.getVisibility() == View.VISIBLE) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Group> groupList = new SharedData(getActivity()).getGroups();
                        if (groupList != null)
                            for (int i = 0; i < groupList.size(); i++) {
                                if(groups!=null)
                                if (!groups.contains(groupList.get(i))) {
                                    updateFindUser(groupAdapter, groupList.get(i));
                                }
                            }

                    }
                }).start();
            }
            groupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        ChatService.savaDate(groups);
        getActivity().unregisterReceiver(msgBoradCastReceiver);
        super.onDestroy();
    }
    //添加按钮
    public void swipMenuCreator() {
        swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                if(viewType==1) {
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
                }else if(viewType==0){
                    SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                            //.setImage(R.mipmap.ic_action_delete) // 图标。
                            .setBackgroundDrawable(R.drawable.bg_delete)
                            .setText("删除消息") // 文字。
                            .setTextColor(Color.WHITE) // 文字颜色。
                            .setTextSize(16) // 文字大小。
                            .setWidth(300)
                            .setHeight(height);
                    swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.
                }else if(viewType==2){
                    SwipeMenuItem addItem = new SwipeMenuItem(getActivity())
                            //.setImage(R.mipmap.ic_action_delete) // 图标。
                            .setBackgroundDrawable(R.drawable.bg_agree)
                            .setText("加为好友") // 文字。
                            .setTextColor(Color.WHITE) // 文字颜色。
                            .setTextSize(16) // 文字大小。
                            .setWidth(350)
                            .setHeight(height);
                    swipeRightMenu.addMenuItem(addItem);// 添加一个按钮到右侧侧菜单。.
                    SwipeMenuItem refuseItem = new SwipeMenuItem(getActivity())
                            //.setImage(R.mipmap.ic_action_delete) // 图标。
                            .setBackgroundDrawable(R.drawable.bg_disagree)
                            .setText("屏蔽") // 文字。
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
            }
        };

        menuItemClickListener = new OnSwipeMenuItemClickListener() {
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

                if(groupAdapter.getItemViewType(adapterPosition)==0){  //删除消息
                    if(menuPosition==0){
                        groups.remove(adapterPosition);
                        groupAdapter.notifyItemRemoved(adapterPosition);
                    }
                }else if(groupAdapter.getItemViewType(adapterPosition)==1){//好友申请
                    if(menuPosition==0){//同意
                        Snackbar.make(rcly_qun,"她（他）已成为你的好友",Snackbar.LENGTH_SHORT).show();
                        addGroup=new Group(groups.get(adapterPosition).getId(),
                                groups.get(adapterPosition).getGroupName(),0,groups.get(adapterPosition).getDrawable());
                        groups.remove(adapterPosition);
                        groupAdapter.notifyItemRemoved(adapterPosition);
                        addGroup.setIsgroup(0);
                        groups.add(0,addGroup);
                        groupAdapter.notifyItemInserted(0);
                        UserInfo.addGroup(addGroup);
                    }else if(menuPosition==1){//拒绝
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserInfo.removeUser(groups.get(adapterPosition).getId());
                            }
                        }).start();
                        Snackbar.make(rcly_qun,"已拒绝",Snackbar.LENGTH_SHORT).show();
                        groups.remove(adapterPosition);
                        groupAdapter.notifyItemRemoved(adapterPosition);
                    }
                }else if(groupAdapter.getItemViewType(adapterPosition)==2){//添加好友
                    if(menuPosition==0){//加好友
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserInfo.addUser(groups.get(adapterPosition).getGroupName());
                                groups.get(adapterPosition).setIsgroup(0);
                            }
                        }).start();
                        Snackbar.make(rcly_qun,"已发送好友请求",Snackbar.LENGTH_SHORT).show();
                        addGroup=groups.get(adapterPosition);
                        addGroup.setIsgroup(0);
                        UserInfo.addGroup(addGroup);
                    }else if(menuPosition==1){//屏蔽
                        Snackbar.make(rcly_qun,"已屏蔽",Snackbar.LENGTH_SHORT).show();
                        groups.remove(adapterPosition);
                        groupAdapter.notifyItemRemoved(adapterPosition);
                    }else if(menuPosition==2){//删除
                        groups.remove(adapterPosition);
                        groupAdapter.notifyItemRemoved(adapterPosition);
                    }
                }
            }
        };
    }


    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from;

            if((from=intent.getStringExtra("msgFrom"))!=null){
                groupAdapter.notifyDataSetChanged();
                int pos=groups.indexOf(new Group(from,from,0,null));
                if(pos==-1){
                    addGroup(from);
                }else {
                    Group date = groups.get(pos);
                    groups.remove(pos);
                    groups.add(0, date);
                    groupAdapter.notifyItemMoved(pos, 0);
                }
                return;
            }

            String id;
            if(intent.getStringExtra("friendApply")!=null){
                id=intent.getStringExtra("friendApply");
                addNotice(id);
            }
        }
    }

    @Background
    public void addNotice(String id) {
        Group group=new Group(id,id,1,UserInfo.getUserImage(id));
        addGroup=group;
        handler.sendEmptyMessage(1);
    }

    //更新list,多删少加
    public List<Group> updateList(List<Group> list, List<Group> list2, GroupAdapter adapter) {
        for (int i = 0; i < list2.size(); i++) {
            if (!list.contains(list2.get(i))) {
                list.add(list2.get(i));
                adapter.addItem(list2.get(i));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (!list2.contains(list.get(i))) {
                list.remove(list.get(i));
                adapter.deleteItem(list.get(i));
            }

        }
        return list;
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    groups.add(0,addGroup);
                    groupAdapter.notifyItemInserted(0);
                    break;
            }
        }
    };

}
