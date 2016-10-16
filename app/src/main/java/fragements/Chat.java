package fragements;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import com.example.tyhj.schoolmsg.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import adpter.GroupAdapter;
import myinterface.ShowMenu;
import publicinfo.Group;
import publicinfo.Msg_chat;

@EFragment(R.layout.fragment_chat)
public class Chat extends Fragment {
    MsgBoradCastReceiver msgBoradCastReceiver;
    int rotate=0;
    IntentFilter intentFilter;
    ShowMenu showMenu;
    Animation noserch;
    List<Group> groups;
    GroupAdapter groupAdapter;


    class MsgBoradCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            groupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noserch=AnimationUtils.loadAnimation(getActivity(),R.anim.noserch);
        noserch.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(rotate==0)
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
    ImageView iv_showMenu,iv_serchqun;

    @ViewById
    EditText et_serchqun;

    @ViewById
    TextView tv_myqun;

    @ViewById
    RecyclerView rcly_qun;

    @Click(R.id.iv_showMenu)
    void showMenu(){
        showMenu.showMenu();
    }

    @Click(R.id.iv_serchqun)
    void serchqun(){
        if(et_serchqun.getVisibility()==View.GONE) {
            rotate=0;
            et_serchqun.setVisibility(View.VISIBLE);
            tv_myqun.setVisibility(View.GONE);
            iv_showMenu.setVisibility(View.GONE);
            iv_serchqun.startAnimation(noserch);
        }
        else {
            rotate=1;
            et_serchqun.setVisibility(View.GONE);
            tv_myqun.setVisibility(View.VISIBLE);
            iv_showMenu.setVisibility(View.VISIBLE);
            iv_serchqun.startAnimation(noserch);
        }
    }

    @AfterViews
    void afterViews(){
        groups=new ArrayList<Group>();
        Group group=new Group(getString(R.string.textUrl),"110",0);
        Group group0=new Group(getString(R.string.textUrl),"120",0);
        Group group1=new Group(getString(R.string.textUrl1),"Google Assistant",0);
        Group group2=new Group(getString(R.string.textUrl4),"111",1);
        groups.add(group);
        groups.add(group1);
        groups.add(group2);
        groups.add(group0);
        groupAdapter=new GroupAdapter(getActivity(),groups);
        rcly_qun.setAdapter(groupAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rcly_qun.setLayoutManager(linearLayoutManager);
        rcly_qun.setItemAnimator(new DefaultItemAnimator());
        intentFilter=new IntentFilter();
        intentFilter.addAction("boradcast.action.GETMESSAGE2");
        msgBoradCastReceiver=new MsgBoradCastReceiver();
        getActivity().registerReceiver(msgBoradCastReceiver,intentFilter);
    }

    public void setShowMenu(ShowMenu showMenu){
        this.showMenu=showMenu;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(groupAdapter!=null){
            groupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(msgBoradCastReceiver);
        super.onDestroy();
    }
}
