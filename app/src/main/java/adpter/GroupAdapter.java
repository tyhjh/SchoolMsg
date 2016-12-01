package adpter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.Application;
import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage;
import com.example.tyhj.schoolmsg.SendMessage_;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuLayout;

import java.util.List;

import api.FormatTools;
import fragements.Msg;
import myViews.SharedData;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import publicinfo.UserInfo;
import service.ChatService;

import static android.content.ContentValues.TAG;
import static publicinfo.MyFunction.calculatePopWindowPos;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class GroupAdapter extends SwipeMenuAdapter<RecyclerView.ViewHolder> {

    List<Group> groups;
    Context context;
    private LayoutInflater mInflater;
    ImageLoader imageLoader;
    int remove;
    public GroupAdapter(Context context, List<Group> groups) {
        this.groups = groups;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }



    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        if(viewType==0||viewType==2)
            return mInflater.inflate(R.layout.item_qun, parent, false);
        else if(viewType==1)
            return mInflater.inflate(R.layout.item_notice, parent, false);
        else
            return null;
    }

    @Override
    public RecyclerView.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        if(viewType==0||viewType==2)
            return new GroupHolder(realContentView);
        else if(viewType==1)
            return new applyHolder(realContentView);
        else
            return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, int position) {

        if(holder1 instanceof GroupHolder){
            final GroupHolder holder=(GroupHolder) holder1;
            final Group group = groups.get(holder.getPosition());
            Msg_chat msg_chat = null;
            holder.tv_send_time.setVisibility(View.VISIBLE);
            holder.tv_who_send.setVisibility(View.VISIBLE);
            holder.iv_type.setVisibility(View.VISIBLE);
            holder.tv_msgCount.setVisibility(View.VISIBLE);
            holder.ib_status.setVisibility(View.VISIBLE);
            if (group.getDrawable() == null)
                Picasso.with(context).load(R.mipmap.ic_launcher).into(holder.iv_headImage);
            else
                holder.iv_headImage.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(group.getDrawable()));
            holder.tv_group_name.setText(group.getGroupName());
            holder.ib_status.setClipToOutline(true);
            holder.ib_status.setOutlineProvider(MyFunction.getOutline(true, 40, 0));
            holder.iv_headImage.setClipToOutline(true);
            holder.iv_headImage.setOutlineProvider(MyFunction.getOutline(true, 10, 0));
            List<Msg_chat> msgChatList = new SharedData(context).getData(group.getId());
            if (msgChatList != null && msgChatList.size() > 0) {
                msg_chat = msgChatList.get(msgChatList.size() - 1);
                holder.tv_send_time.setText(MyFunction.getTime2(msg_chat.getTime()));
                group.setLastTime(msg_chat.getTime());
                holder.tv_text.setText(msg_chat.getText());
                if (msg_chat.getWho() == 1)
                    holder.tv_who_send.setText("你：");
                else
                    holder.tv_who_send.setText("");
                //消息状态
                switch (msg_chat.getStatus()) {
                    //发送成功
                    case -1:
                        holder.tv_msgCount.setVisibility(View.GONE);
                        Picasso.with(context).load(R.drawable.ic_sent).into(holder.ib_status);
                        break;
                    //有消息
                    case 0:
                        holder.ib_status.setVisibility(View.GONE);
                        for (int i = msgChatList.size() - 1; i >= 0; i--) {
                            if (msgChatList.get(i).getWho() != 2 || msgChatList.get(i).getStatus() != 0) {
                                holder.tv_msgCount.setText(msgChatList.size() - 1 - i + "");
                                break;
                            }
                            holder.tv_msgCount.setText("1");
                        }
                        break;
                    //发送中
                    case 1:
                        holder.tv_msgCount.setVisibility(View.GONE);
                        Picasso.with(context).load(R.drawable.ic_sending).into(holder.ib_status);
                        break;
                    //接受成功
                    case 2:
                        holder.tv_msgCount.setVisibility(View.GONE);
                        holder.ib_status.setVisibility(View.GONE);
                        break;
                }
                switch (msg_chat.getType()) {
                    case 0:
                        holder.iv_type.setVisibility(View.GONE);
                        break;
                    case 1:
                        holder.iv_type.setImageResource(R.drawable.ic_camera_24dp);
                        holder.tv_text.setText("图片");
                        break;
                    case 2:
                        holder.iv_type.setImageResource(R.drawable.ic_mic_24dp);
                        holder.tv_text.setText("语音");
                        break;
                    case 3:
                        holder.iv_type.setImageResource(R.drawable.ic_file_24dp);
                        holder.tv_text.setText("文件");
                        break;
                }
            } else {
                holder.tv_msgCount.setVisibility(View.GONE);
                holder.tv_text.setText("");
                holder.iv_type.setVisibility(View.GONE);
                holder.ib_status.setVisibility(View.GONE);
                holder.tv_send_time.setVisibility(View.GONE);
                holder.tv_who_send.setVisibility(View.GONE);
            }
           holder.ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chat(group);
                }
            });
        }else if(holder1 instanceof  applyHolder){
            applyHolder holder= (applyHolder) holder1;
            final Group group = groups.get(holder.getPosition());
            holder.head.setClipToOutline(true);
            holder.head.setOutlineProvider(MyFunction.getOutline(true,10,0));
            holder.head.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(group.getDrawable()));
            holder.name.setText(group.getGroupName());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(groups.get(position).getIsgroup()==0)
            return 0;
        else if(groups.get(position).getIsgroup()==1)
            return 1;
        else if(groups.get(position).getIsgroup()==2)
            return 2;
        else if(groups.get(position).getIsgroup()==3)
            return 3;
        else
            return 4;
    }

    //进入
    public void chat(Group group) {
        Intent intent = new Intent(context, SendMessage_.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);
        intent.putExtras(bundle);
        int position=groups.indexOf(group);
        groups.remove(position);
        groups.add(0,group);
        notifyItemMoved(position,0);
        ChatService.savaDate(group);
        context.startActivity(intent);
    }

    public void addItem(Group group) {
        groups.add(0, group);
        notifyItemInserted(0);
    }

    public void deleteItem(Group group) {
        Group group1 = null;

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public List<Group> getGroups() {
        return groups;
    }

    class GroupHolder extends RecyclerView.ViewHolder {
        ImageView iv_headImage, iv_type;
        ImageView ib_status;
        LinearLayout ll_group;
        TextView tv_group_name, tv_send_time, tv_who_send, tv_text, tv_msgCount;
        public GroupHolder(View view) {
            super(view);
            ll_group = (LinearLayout) view.findViewById(R.id.ll_group);
            iv_headImage = (ImageView) view.findViewById(R.id.iv_headImage);
            iv_type = (ImageView) view.findViewById(R.id.iv_type);
            ib_status = (ImageView) view.findViewById(R.id.iv_status);
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            tv_send_time = (TextView) view.findViewById(R.id.tv_send_time);
            tv_who_send = (TextView) view.findViewById(R.id.tv_who_send);
            tv_text = (TextView) view.findViewById(R.id.tv_text);
            tv_msgCount = (TextView) view.findViewById(R.id.tv_msgCount);
        }
    }

    class applyHolder extends RecyclerView.ViewHolder{
        ImageView head;
        LinearLayout ll_group;
        Button button;
        TextView name;
        public applyHolder(View itemView) {
            super(itemView);
            head= (ImageView) itemView.findViewById(R.id.iv_headImage);
            ll_group= (LinearLayout) itemView.findViewById(R.id.ll_group);
            button= (Button) itemView.findViewById(R.id.btn_add);
            name= (TextView) itemView.findViewById(R.id.tv_name);
        }



    }



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    notifyItemRemoved(remove);
                    break;
            }
        }
    };
}
