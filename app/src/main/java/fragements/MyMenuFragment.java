package fragements;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.Login;
import com.example.tyhj.schoolmsg.Login_;
import com.example.tyhj.schoolmsg.R;
import com.mxn.soul.flowingdrawer_core.MenuFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import publicinfo.MyFunction;
import service.ChatService;


public class MyMenuFragment extends MenuFragment {
    private static String PATH="http://115.28.16.220:8080/Upload/uploadFile/p123.JPEG";
    NavigationView navigationView;
    View view;
    Button camoral, images;
    Uri imageUri;
    ImageView imageView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        navigationView= (NavigationView) view.findViewById(R.id.vNavigation);

        TextView textView= (TextView) view.findViewById(R.id.signature);
        imageView=(ImageView) view.findViewById(R.id.userheadImage);
        imageView.setOutlineProvider(MyFunction.getOutline(true,20,0));
        imageView.setClipToOutline(true);
        Picasso.with(getActivity()).load(R.mipmap.default_headimage).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        return  setupReveal(view) ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_feed:
                        break;
                    case R.id.menu_direct:
                        break;
                    case R.id.menu_news:
                        break;
                    case R.id.menu_photos_nearby:
                        break;
                    case R.id.menu_group_2:
                        break;
                    case R.id.menu_settings:
                        break;
                    //分享
                    case R.id.menu_share:
                      Toast.makeText(getActivity(),getString(R.string.share), Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.menu_logout:
                        if(MyFunction.getUser()!=null)
                        MyFunction.getUser().logout();
                        SharedPreferences shared=getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent=new Intent(getActivity(), ChatService.class);
                        getActivity().stopService(intent);
                        startActivity(new Intent(getActivity(), Login_.class));
                        getActivity().finish();
                        break;
                    case R.id.menu_about:
                        //删除数据
                        break;
                }
                return false;
            }
        });
    }
    // 上传用户头像
    private void dialog() {
        AlertDialog.Builder di;
        di = new AlertDialog.Builder(getActivity());
        di.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout = inflater.inflate(R.layout.headchoose, null);
        di.setView(layout);
        final Dialog dialog=di.show();
        camoral = (Button) layout.findViewById(R.id.camoral);
        images = (Button) layout.findViewById(R.id.images);
        // 相机
        camoral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                File f1 = new File(Environment.getExternalStorageDirectory()+"/LinkManPhone");
                if(!f1.exists()){
                    f1.mkdirs();
                }
                File outputImage = new File(Environment
                        .getExternalStorageDirectory()+"/LinkManPhone", "head.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                WHERE_PHOTO = 1;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        // 相册
        images.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
                File f1 = new File(Environment.getExternalStorageDirectory()+"/LinkManPhone");
                if(!f1.exists()){
                    f1.mkdirs();
                }
                File outputImage = new File(Environment
                        .getExternalStorageDirectory()+"/LinkManPhone", "head.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);

                WHERE_PHOTO = 2;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });
    }
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    int WHERE_PHOTO = 0;
    String date;
    public void onOpenMenu(){

    }
    public void onCloseMenu(){
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    };
}
