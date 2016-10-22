package adpter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.example.tyhj.schoolmsg.Application;
import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage;
import com.example.tyhj.schoolmsg.SendMessage_;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

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

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {

    List<Group> groups;
    Context context;
    private LayoutInflater mInflater;
    ImageLoader imageLoader;

    View view;

    public void setView(View view) {
        this.view = view;
    }

    public GroupAdapter(Context context, List<Group> groups) {
        this.groups = groups;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }


    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_qun, parent, false);
        GroupHolder groupHolder = new GroupHolder(view);
        return groupHolder;
    }

    @Override
    public void onBindViewHolder(final GroupHolder holder, final int position) {
        final Group group = groups.get(holder.getPosition());
        Msg_chat msg_chat = null;
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
        List<Msg_chat> msgChatList = new SharedData(context).getData(group.getGroupName());
        if (msgChatList != null && msgChatList.size() > 0) {
            msg_chat = msgChatList.get(msgChatList.size() - 1);
            holder.tv_send_time.setText(MyFunction.getTime2(msg_chat.getTime()));
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
        }

        holder.ll_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat(group);
            }
        });

        holder.ll_group.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog di = new Dialog(context);
                di.setCancelable(true);
                LayoutInflater inflater = LayoutInflater.from(context);
                View layout = inflater.inflate(R.layout.item_set_friends, null);
                di.setContentView(layout);
                di.create();
                Window dialogWindow = di.getWindow();
                WindowManager m = ((Activity) context).getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                p.width = (int) (d.getWidth() * 0.75); // 宽度设置为屏幕的0.65
                dialogWindow.setAttributes(p);
                di.show();
                Button sendMessage,add,delete,messge;
                sendMessage= (Button) layout.findViewById(R.id.sendMessage);
                add= (Button) layout.findViewById(R.id.add);
                delete= (Button) layout.findViewById(R.id.delete);
                messge= (Button) layout.findViewById(R.id.messge);
                sendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chat(group);
                        di.cancel();
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfo.addUser(group.getGroupName());
                        di.cancel();
                    }
                });
                //删除好友
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        di.cancel();
                    }
                });

                //查看信息
                messge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        di.cancel();
                    }
                });

                if (UserInfo.getMyGroups() == null || !UserInfo.getMyGroups().contains(group)) {
                    delete.setVisibility(View.GONE);
                }else {
                    add.setVisibility(View.GONE);
                }
                return true;
            }
        });


    }

    public void chat(Group group) {
        Intent intent = new Intent(context, SendMessage_.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("group", group);
        intent.putExtras(bundle);
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
}
