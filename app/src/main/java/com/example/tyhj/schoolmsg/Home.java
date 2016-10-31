package com.example.tyhj.schoolmsg;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smackx.muc.MultiUserChat;

import api.FormatTools;
import fragements.Chat;
import fragements.Chat_;
import fragements.Msg;
import fragements.Msg_;
import fragements.MyMenuFragment;
import fragements.MyTools;
import fragements.Pager2;
import myViews.MyViewPager;
import myViews.waveNavigation.FlowingView;
import myViews.waveNavigation.LeftDrawerLayout;
import myinterface.ShowMenu;
import publicinfo.MyFunction;
import publicinfo.UserInfo;
import service.ChatService;

@EActivity(R.layout.activity_home)
public class Home extends AppCompatActivity implements ShowMenu{
    private int TAB_COUNT=4;
    private long exitTime = 0;
    Chat chat;
    private LeftDrawerLayout mLeftDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyFunction.setContext(this);
        if(UserInfo.getXmppConnection()==null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!MyFunction.isIntenet(Home.this,null)){

                    }
                    if(UserInfo.reLogin(Home.this)){
                        Intent intent=new Intent(Home.this,ChatService.class);
                        startService(intent);
                    }
                }
            }).start();
        if(MyFunction.isRegister()&&UserInfo.getId().equals(MyFunction.getRegisterId())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserInfo.changeImage( FormatTools.getInstance().Drawable2Bytes(getDrawable(R.mipmap.tyhj)));
                }
            }).start();
        }
    }

    @ViewById
    ImageView btnHome1,btnHome2,btnHome3,btnHome4;

    @ViewById
    MyViewPager vpHome;

    @AfterViews
    void afterViews(){
        chat=new Chat_();
        chat.setShowMenu(Home.this);
        setInitColor();
        initdrawerLayout();
        vpHome.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return chat;
                    case 1:
                        return new Pager2();
                    case 2:
                        return new Msg_() ;
                    case 3:
                        return new MyTools();
                }
                return null;
            }

            @Override
            public int getCount() {
                return TAB_COUNT;
            }
        });
        vpHome.setOffscreenPageLimit(4);
        btnHome1.setImageResource(R.drawable.ic_group_chose_24dp);
        vpHome.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setInitColor();
                switch (position){
                    case 0:
                        btnHome1.setImageResource(R.drawable.ic_group_chose_24dp);
                        break;
                    case 1:
                        btnHome2.setImageResource(R.drawable.ic_tags_chose);
                        break;
                    case 2:
                        btnHome3.setImageResource(R.drawable.ic_chat_chose_24dp);
                        break;
                    case 3:
                        btnHome4.setImageResource(R.drawable.ic_hdr_weak_chose_24dp);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Click(R.id.btnHome1)
    void home1(){
        setInitColor();
        btnHome1.setImageResource(R.drawable.ic_group_chose_24dp);
        vpHome.setCurrentItem(0);
    }

    @Click(R.id.btnHome2)
    void home2(){
        setInitColor();
        btnHome2.setImageResource(R.drawable.ic_tags_chose);
        vpHome.setCurrentItem(1);
    }

    @Click(R.id.btnHome3)
    void home3(){
        setInitColor();
        btnHome3.setImageResource(R.drawable.ic_chat_chose_24dp);
        vpHome.setCurrentItem(2);
    }

    @Click(R.id.btnHome4)
    void home4(){
        setInitColor();
        btnHome4.setImageResource(R.drawable.ic_hdr_weak_chose_24dp);
        vpHome.setCurrentItem(3);
    }

    @UiThread
    public void startService() {
        Intent intent=new Intent(this, ChatService.class);
        startService(intent);
    }
    void setInitColor(){
        btnHome1.setImageResource(R.drawable.ic_group_24dp);
        btnHome2.setImageResource(R.drawable.ic_tags);
        btnHome3.setImageResource(R.drawable.ic_chat_24dp);
        btnHome4.setImageResource(R.drawable.ic_hdr_weak_24dp);
    }

    private void initdrawerLayout() {
        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        FragmentManager fm = getSupportFragmentManager();
        MyMenuFragment mMenuFragment = (MyMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new MyMenuFragment()).commit();
        }
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
    }

    @Override
    public void showMenu() {
        mLeftDrawerLayout.openDrawer();
    }

    @Override
    public void onBackPressed() {
        if (mLeftDrawerLayout.isShownMenu()) {
            mLeftDrawerLayout.closeDrawer();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
               this.finish();
            }
        }
    }



}
