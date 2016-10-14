package adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage;
import com.example.tyhj.schoolmsg.SendMessage_;
import com.squareup.picasso.Picasso;

import java.util.List;

import fragements.Msg;
import myViews.SharedData;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;

import static android.content.ContentValues.TAG;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder>{

    List<Group> groups;
    Context context;
    private LayoutInflater mInflater;

    public GroupAdapter(Context context, List<Group> groups){
        this.groups=groups;
        this.context=context;
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_qun,parent,false);
        GroupHolder groupHolder=new GroupHolder(view);
        return groupHolder;
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, final int position) {
        Msg_chat msg_chat = null;
        holder.tv_msgCount.setVisibility(View.VISIBLE);
        holder.ib_status.setVisibility(View.VISIBLE);
        Group group=groups.get(position);
        Picasso.with(context).load(group.getGroupImageUrl()).into(holder.iv_headImage);
        holder.tv_group_name.setText(group.getGroupName());
        holder.ib_status.setClipToOutline(true);
        holder.ib_status.setOutlineProvider(MyFunction.getOutline(true,40,0));
        holder.iv_headImage.setClipToOutline(true);
        holder.iv_headImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        List<Msg_chat> msgChatList=new SharedData(context).getData(group.getGroupName());
        if(msgChatList!=null) {
            msg_chat = msgChatList.get(msgChatList.size() - 1);
            holder.tv_send_time.setText(MyFunction.getTime2(msg_chat.getTime()));
            holder.tv_text.setText(msg_chat.getText());
            if(msg_chat.getWho()==1)
                holder.tv_who_send.setText("你：");
            //消息状态
            switch (msg_chat.getStatus()){
                //发送成功
                case -1:
                    holder.tv_msgCount.setVisibility(View.GONE);
                    Picasso.with(context).load(R.drawable.ic_sent).into(holder.ib_status);
                    break;
                //有消息
                case 0:
                    holder.ib_status.setVisibility(View.GONE);
                    holder.tv_msgCount.setText(group.getTextCount()+"");
                    break;
                //发送中
                case 1:
                    holder.tv_msgCount.setVisibility(View.GONE);
                    Picasso.with(context).load(R.drawable.ic_sending).into(holder.ib_status);
                    break;
                //接受成功
                case 2:
                    holder.tv_msgCount.setVisibility(View.GONE);
                    Picasso.with(context).load(R.drawable.ic_read).into(holder.ib_status);
                    break;
            }
            switch (msg_chat.getType()){
                case 0:

                    break;
                case 1:
                    Picasso.with(context).load(R.drawable.ic_camera_24dp).into(holder.iv_type);
                    break;
                case 2:
                    Picasso.with(context).load(R.drawable.ic_mic_24dp).into(holder.iv_type);
                    break;
                case 3:
                    Picasso.with(context).load(R.drawable.ic_file_24dp).into(holder.iv_type);
                    break;
            }
        }else {
            holder.tv_msgCount.setVisibility(View.GONE);
        }

        holder.ll_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SendMessage_.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("group",groups.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
//                ((Activity)context).finish();
            }
        });
    }

    public void addItem(Group group){
        groups.add(0,group);
        notifyItemInserted(0);
    }

    public void deleteItem(Group group){
        Group group1=null;

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class GroupHolder extends RecyclerView.ViewHolder{
        ImageView iv_headImage,iv_type;
        ImageView ib_status;
        LinearLayout ll_group;
        TextView tv_group_name,tv_send_time,tv_who_send,tv_text,tv_msgCount;
        public GroupHolder(View view) {
            super(view);
            ll_group= (LinearLayout) view.findViewById(R.id.ll_group);
            iv_headImage= (ImageView) view.findViewById(R.id.iv_headImage);
            iv_type= (ImageView) view.findViewById(R.id.iv_type);
            ib_status= (ImageView) view.findViewById(R.id.iv_status);
            tv_group_name= (TextView) view.findViewById(R.id.tv_group_name);
            tv_send_time= (TextView) view.findViewById(R.id.tv_send_time);
            tv_who_send= (TextView) view.findViewById(R.id.tv_who_send);
            tv_text= (TextView) view.findViewById(R.id.tv_text);
            tv_msgCount= (TextView) view.findViewById(R.id.tv_msgCount);
        }
    }
}
