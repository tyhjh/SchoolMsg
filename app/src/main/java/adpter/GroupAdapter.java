package adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tyhj.schoolmsg.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import publicinfo.Group;
import publicinfo.MyFunction;

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
    public void onBindViewHolder(GroupHolder holder, int position) {
        Group group=groups.get(position);
        Picasso.with(context).load(group.getGroupImageUrl()).into(holder.iv_headImage);
        holder.tv_group_name.setText(group.getGroupName());
        holder.tv_send_time.setText(group.getSendTime());
        holder.tv_text.setText(group.getText());
        holder.ib_status.setClipToOutline(true);
        holder.ib_status.setOutlineProvider(MyFunction.getOutline(true,40,0));
        holder.iv_headImage.setClipToOutline(true);
        holder.iv_headImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        if(group.getWhoSend()==0)
        holder.tv_who_send.setText("你：");
        //消息状态
        switch (group.getStatus()){
            //发送成功
            case -1:
                Picasso.with(context).load(R.drawable.ic_sent).into(holder.ib_status);
                break;
            //有消息
            case 0:

                break;
            //发送中
            case 1:
                Picasso.with(context).load(R.drawable.ic_sending).into(holder.ib_status);
                break;
        }

        switch (group.getType()){
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
        TextView tv_group_name,tv_send_time,tv_who_send,tv_text;
        public GroupHolder(View view) {
            super(view);
            iv_headImage= (ImageView) view.findViewById(R.id.iv_headImage);
            iv_type= (ImageView) view.findViewById(R.id.iv_type);
            ib_status= (ImageView) view.findViewById(R.id.iv_status);
            tv_group_name= (TextView) view.findViewById(R.id.tv_group_name);
            tv_send_time= (TextView) view.findViewById(R.id.tv_send_time);
            tv_who_send= (TextView) view.findViewById(R.id.tv_who_send);
            tv_text= (TextView) view.findViewById(R.id.tv_text);
        }
    }
}
