package adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tyhj.schoolmsg.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

import publicinfo.Msg_chat;
import publicinfo.MyFunction;

import static android.content.ContentValues.TAG;

/**
 * Created by Tyhj on 2016/10/14.
 */

public class ChatAdpter extends ArrayAdapter<Msg_chat> {
    String headImage;
    List<Msg_chat> msg_chats;
    ImageLoader imageLoader;
    public ChatAdpter(Context context, int resource, List<Msg_chat> objects) {
        super(context, resource, objects);
        msg_chats=objects;
        imageLoader= ImageLoader.getInstance();
    }

    public ImageLoader getImageLoader(){
        return imageLoader;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg_chat msg_chat=getItem(position);
        View view=null;
        ViewHolder viewHolder;
            viewHolder=new ViewHolder();
            if (msg_chat.getWho() == 1) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_me, null);
                viewHolder.status= (ImageView) view.findViewById(R.id.iv_status);
                viewHolder.text= (TextView) view.findViewById(R.id.tv_mytext);
                viewHolder.image= (ImageView) view.findViewById(R.id.iv_myimage);
                if(msg_chat.getType()==0){
                    viewHolder.text.setText(msg_chat.getText());
                }else if(msg_chat.getType()==1){
                    viewHolder.text.setVisibility(View.GONE);
                    imageLoader.displayImage(msg_chat.getText(), viewHolder.image, MyFunction.getOption());

                }
                if(msg_chat.getStatus()==-1){
                    Picasso.with(getContext()).load(R.drawable.ic_sent).into(viewHolder.status);
                }else {
                    Picasso.with(getContext()).load(R.drawable.ic_sending).into(viewHolder.status);
                }

            } else if (msg_chat.getWho() == 2) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_you, null);
                viewHolder.text= (TextView) view.findViewById(R.id.tv_yourtext);
                viewHolder.image= (ImageView) view.findViewById(R.id.iv_yourimage);
                viewHolder.headImage= (ImageView) view.findViewById(R.id.iv_yourheadImage);
                viewHolder.headImage.setClipToOutline(true);
                viewHolder.headImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
                Picasso.with(getContext()).load(getHeadImage()).into(viewHolder.headImage);
                if(msg_chat.getType()==0){
                    viewHolder.text.setText(msg_chat.getText());
                }else if(msg_chat.getType()==1){
                    viewHolder.text.setVisibility(View.GONE);
                    Picasso.with(getContext()).load(msg_chat.getText()).into(viewHolder.image);
                }

            } else if (msg_chat.getWho() == 0) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_time, null);
                viewHolder.text= (TextView) view.findViewById(R.id.tv_chatTiem);
                viewHolder.text.setText(msg_chat.getText());
            }

        for(int i=msg_chats.size()-1;i>=0;i--){
            if(msg_chats.get(i).getWho()==0){
                if(msg_chats.get(i).getText().contains("月"))
                break;
                msg_chats.get(i).setText(MyFunction.getTime2(msg_chats.get(i).getTime()));
            }
        }

        return view;
    }

    public void chageStatus(int id,int status){

        notifyDataSetChanged();
    }
    @Override
    public void add(Msg_chat object) {
        if(object.getWho()==0){
            for(int i=msg_chats.size()-1;i>=0;i--){
                if(msg_chats.get(i).getWho()==0){
                    if(msg_chats.get(i).getText().contains("月"))
                        break;
                    msg_chats.get(i).setText(MyFunction.getTime2(msg_chats.get(i).getTime()));
                }
            }
        }
        super.add(object);
    }

    public List<Msg_chat> getMsg_chats(){
        return  msg_chats;
    }

    public void update(){
        for(int i=msg_chats.size()-1;i>=0;i--){
            if(msg_chats.get(i).getStatus()==-1&&msg_chats.get(i).getWho()==1){
                break;
            }else if(msg_chats.get(i).getWho()==1&&msg_chats.get(i).getStatus()==1){
                msg_chats.get(i).setStatus(-1);
            }
        }
    }

    @Override
    public int getCount() {
        return msg_chats.size();
    }

    class ViewHolder {
        TextView text;
        ImageView headImage,image,status;
    }
}
