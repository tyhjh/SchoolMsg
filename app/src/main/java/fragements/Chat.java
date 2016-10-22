package fragements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from;
            groupAdapter.notifyDataSetChanged();
            if((from=intent.getStringExtra("msgFrom"))!=null){
                int pos=groups.indexOf(new Group(null,from,0,null));
                Group date=groups.get(pos);
                groups.remove(pos);
                groups.add(0,date);
                groupAdapter.notifyItemMoved(pos,0);
            }
        }
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
    RecyclerView rcly_qun, rcly_find_land;

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
                groupAdapter_land.addItem(groups.get(i));
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
            Group group = new Group(null, name, 0, drawable);
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
        intentFilter.addAction("boradcast.action.GETMESSAGE2");
        msgBoradCastReceiver = new MsgBoradCastReceiver();
        getActivity().registerReceiver(msgBoradCastReceiver, intentFilter);
        groupAdapter.setView(ll_view);
        groupAdapter_land.setView(ll_view);
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




}
