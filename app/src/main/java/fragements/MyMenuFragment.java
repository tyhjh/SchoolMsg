package fragements;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.Login_;
import com.example.tyhj.schoolmsg.R;
import com.mxn.soul.flowingdrawer_core.MenuFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.squareup.picasso.Picasso;
import com.tyhj.myfist_2016_6_29.MyTime;

import java.io.File;

import api.FormatTools;
import publicinfo.MyFunction;
import publicinfo.UserInfo;

import static android.content.Intent.ACTION_GET_CONTENT;


public class MyMenuFragment extends MenuFragment {
    private static String PATH="http://115.28.16.220:8080/Upload/uploadFile/p123.JPEG";
    String headImage;
    NavigationView navigationView;
    View view;
    Button camoral, images;
    Uri imageUri;
    ImageView imageView;
    Drawable drawable;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        navigationView= (NavigationView) view.findViewById(R.id.vNavigation);
        contentResolver=getActivity().getContentResolver();
        TextView textView= (TextView) view.findViewById(R.id.signature);
        imageView=(ImageView) view.findViewById(R.id.userheadImage);
        imageView.setOutlineProvider(MyFunction.getOutline(true,20,0));
        imageView.setClipToOutline(true);
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
        com.ant.liao.GifView gifView= (com.ant.liao.GifView) view.findViewById(R.id.gif);
        gifView.setGifImage(R.mipmap.gif3);
        gifView.setGifImageType(com.ant.liao.GifView.GifImageType.COVER);
        gifView.setShowDimension(900, 820);
        navigtion();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(MyFunction.isRegister()&&UserInfo.getId().equals(MyFunction.getRegisterId())) {
                    handler.sendEmptyMessage(2);
                    MyFunction.setRegister(false);
                }else
                    drawable = FormatTools.getInstance().Bytes2Drawable(UserInfo.getUserImage(UserInfo.getId()));
                if (drawable != null)
                    handler.sendEmptyMessage(1);
            }
        }).start();
    }
    //侧栏菜单
    public void navigtion() {
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
                        UserInfo.logout(getActivity());
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

    public void onOpenMenu(){

    }
    public void onCloseMenu(){
    }

    String path = Environment.getExternalStorageDirectory() + "/ASchollMsg";
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int PICK_PHOTO=0;
    String date;
    ContentResolver contentResolver;

    //选择图片
    private void dialog() {
        AlertDialog.Builder di;
        di = new AlertDialog.Builder(getActivity());
        di.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout = inflater.inflate(R.layout.headchoose, null);
        di.setView(layout);
        final Dialog dialog = di.show();
        camoral = (Button) layout.findViewById(R.id.camoral);
        images = (Button) layout.findViewById(R.id.images);
        // 相机
        camoral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        // 相册
        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, PICK_PHOTO);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //这是从相机返回的数据
            case TAKE_PHOTO:
                getDate();
                if (resultCode == getActivity().RESULT_OK) {
                    if (data != null) {
                        imageUri = data.getData();
                    }
                    String path_pre = MyFunction.getFilePathFromContentUri(imageUri, contentResolver);
                    File newFile = new File(path, date);
                        //压缩图片
                    MyFunction.ImgCompress(path_pre, newFile);
                    cropPhoto(Uri.fromFile(newFile));
                }
                break;
            //这是从相册返回的数据
            case PICK_PHOTO:
                getDate();
                if (resultCode == getActivity().RESULT_OK) {
                    if (data != null) {
                        imageUri = data.getData();
                    }
                    String path_pre = MyFunction.getFilePathFromContentUri(imageUri, contentResolver);
                    File newFile = new File(path, date);
                        //压缩图片
                        MyFunction.ImgCompress(path_pre, newFile);
                        cropPhoto(Uri.fromFile(newFile));
                }
                break;
            //剪裁图片返回数据,就是原来的文件
            case CROP_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    final String fileName = path + "/" + date;
                    File newFile = new File(path, date);
                    headImage=fileName;
                    MyFunction.ImgCompress(fileName, newFile,100,100,50);
                    //获取到的就是new File或fileName
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(UserInfo.changeImage(new File(path, date)))
                                handler.sendEmptyMessage(3);
                        }
                    }).start();

                }
                break;
            default:
                break;
        }
    }

    //随机获取文件名字
    public void getDate() {
        MyTime myTime = new MyTime();
        date = myTime.getYear() + myTime.getMonth_() + myTime.getDays() +
                myTime.getWeek_() + myTime.getHour() + myTime.getMinute() +
                myTime.getSecond() + UserInfo.getId()+ ".JPEG";
    }

    //剪裁图片
    public void cropPhoto(Uri imageUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:imageView.setImageDrawable(drawable);
                    break;
                case 2:
                    imageView.setImageResource(R.mipmap.default_headimage);
                    break;
                case 3:
                    ImageLoader imageLoader=ImageLoader.getInstance();
                    String imageUrl = ImageDownloader.Scheme.FILE.wrap(headImage);
                    imageLoader.displayImage(imageUrl,imageView, MyFunction.getOption());
                    break;
            }
        }
    };
}
