package com.example.tyhj.schoolmsg;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import myViews.AndroidBug5497Workaround;
import myViews.MyViewPager;
import myViews.StatusBarUtil;
import publicinfo.Group;
import publicinfo.MyFunction;

@EActivity(R.layout.activity_send_message)
public class SendMessage extends AppCompatActivity {
    Group group=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.mipmap.chat_bg);
        group= (Group) this.getIntent().getSerializableExtra("group");
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"));
    }



    @ViewById
    ImageView iv_back,iv_heagImage;

    @ViewById
    TextView tv_name;

    @ViewById
    ScrollView scrollView;


    @Click(R.id.iv_back)
    void back(){
        this.finish();
    }

    @ViewById
    LinearLayout ll_bg;


    @AfterViews
    void afterViews(){
        tv_name.setText(group.getGroupName());
        iv_heagImage.setClipToOutline(true);
        iv_heagImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        Picasso.with(this).load(group.getGroupImageUrl()).into(iv_heagImage);
    }
}
