package adpter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tyhj.schoolmsg.R;
import com.example.tyhj.schoolmsg.SendMessage_;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import api.FormatTools;
import myViews.SharedData;
import publicinfo.Group;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import service.ChatService;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class FindUserAdapter extends SwipeMenuAdapter<RecyclerView.ViewHolder> {

    List<Group> groups;
    Context context;
    private LayoutInflater mInflater;
    ImageLoader imageLoader;
    int remove;
    public FindUserAdapter(Context context, List<Group> groups) {
        this.groups = groups;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return mInflater.inflate(R.layout.item_qun, parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new GroupHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, int position) {

            final GroupHolder holder=(GroupHolder) holder1;
            final Group group = groups.get(holder.getPosition());
            holder.iv_type.setVisibility(View.GONE);
            holder.tv_msgCount.setVisibility(View.GONE);
            holder.ib_status.setVisibility(View.GONE);
            if (group.getDrawable() == null)
                Picasso.with(context).load(R.mipmap.ic_launcher).into(holder.iv_headImage);
            else
                holder.iv_headImage.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(group.getDrawable()));
            holder.tv_group_name.setText(group.getGroupName());
            holder.iv_headImage.setClipToOutline(true);
            holder.iv_headImage.setOutlineProvider(MyFunction.getOutline(true, 10, 0));
           holder.ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chat(group);
                }
            });
    }

    @Override

    public int getItemViewType(int position) {
        if(groups.get(position).getIsgroup()==0)
            return 0;
        else if(groups.get(position).getIsgroup()==2)
            return 2;
        else
            return  0;
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
