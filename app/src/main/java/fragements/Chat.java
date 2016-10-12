package fragements;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

@EFragment(R.layout.fragment_chat)
public class Chat extends Fragment {
    int rotate=0;
    ShowMenu showMenu;
    Animation noserch;
    List<Group> groups;
    GroupAdapter groupAdapter;
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
        Group group=new Group(getString(R.string.textUrl),"4班","刚刚","哈哈哈哈",0,0,-1,3);
        Group group1=new Group(getString(R.string.textUrl1),"Google Assistant","23:05","你好啊",1,1,1,3);
        groups.add(group);
        groups.add(group1);
        groupAdapter=new GroupAdapter(getActivity(),groups);
        rcly_qun.setAdapter(groupAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rcly_qun.setLayoutManager(linearLayoutManager);
        rcly_qun.setItemAnimator(new DefaultItemAnimator());
    }


    public void setShowMenu(ShowMenu showMenu){
        this.showMenu=showMenu;
    }

}
