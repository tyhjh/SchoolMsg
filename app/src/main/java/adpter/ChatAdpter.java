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
import com.squareup.picasso.Picasso;

import java.util.List;

import publicinfo.Msg_chat;
import publicinfo.MyFunction;

import static android.content.ContentValues.TAG;

/**
 * Created by Tyhj on 2016/10/14.
 */

public class ChatAdpter extends ArrayAdapter<Msg_chat> {

    List<Msg_chat> msg_chats;
    public ChatAdpter(Context context, int resource, List<Msg_chat> objects) {
        super(context, resource, objects);
        msg_chats=objects;
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
                    Picasso.with(getContext()).load(msg_chat.getImageUrl()).into(viewHolder.image);
                }
                if(msg_chat.getStatus()==0){
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
                Picasso.with(getContext()).load(msg_chat.getHeadImageUrl()).into(viewHolder.headImage);
                if(msg_chat.getType()==0){
                    viewHolder.text.setText(msg_chat.getText());
                }else if(msg_chat.getType()==1){
                    viewHolder.text.setVisibility(View.GONE);
                    Picasso.with(getContext()).load(msg_chat.getImageUrl()).into(viewHolder.image);
                }

            } else if (msg_chat.getWho() == 0) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_time, null);
                viewHolder.text= (TextView) view.findViewById(R.id.tv_chatTiem);
                viewHolder.text.setText(msg_chat.getText());
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
                    msg_chats.get(i).setText(MyFunction.getTime2(msg_chats.get(i).getTime()));
                    Log.e(TAG, "add: 改时间成功啦xxxxxxx"+msg_chats.get(i).getText()+msg_chats.get(i).getTime());
                    break;
                }
            }
        }
        super.add(object);
    }

    public List<Msg_chat> getMsg_chats(){
        return  msg_chats;
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
