package adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import myinterface.sendPicture;
import publicinfo.Group;
import publicinfo.Picture;

/**
 * Created by Tyhj on 2016/10/17.
 */

public class PictureAdpter extends RecyclerView.Adapter<PictureAdpter.holder>{

    List<Picture> pictures;
    Context context;
    sendPicture sendPicture;
    private LayoutInflater mInflater;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    int count=0;
    List<Picture> list;
    public PictureAdpter(Context context, List<Picture> pictures){
        this.pictures=pictures;
        this.context=context;
        list=new ArrayList<Picture>();
        this.mInflater=LayoutInflater.from(context);
        imageLoader=ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_image)
                .showImageOnFail(R.mipmap.nomsg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    @Override
    public holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_list_picture,parent,false);
        holder holder=new holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final holder holder, final int position) {
        String imageUrl = ImageDownloader.Scheme.FILE.wrap(pictures.get(position).getPath());
        imageLoader.displayImage(imageUrl, holder.imageView, options);

        if(pictures.get(position).isCheckable()==1){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pictures.get(position).isCheckable()==1){
                    count--;
                    pictures.get(position).setCheckable(0);
                    holder.checkBox.setChecked(false);
                    list.remove(pictures.get(position));
                }else {
                    if(count>=5){
                        Toast.makeText(context,"一次最多选择5张图片",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    count++;
                    pictures.get(position).setCheckable(1);
                    holder.checkBox.setChecked(true);
                    list.add(pictures.get(position));
                }
                sendPicture.sendPicture(list);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }


    public void setInterface(sendPicture sendPicture){
        this.sendPicture=sendPicture;
    }


    class holder extends RecyclerView.ViewHolder{

        ImageView imageView;
        CheckBox checkBox;
        public holder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.iv_pictures);
            checkBox= (CheckBox) itemView.findViewById(R.id.ck_send);
        }
    }
}
