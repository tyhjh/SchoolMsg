package adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tyhj.schoolmsg.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import api.FormatTools;
import publicinfo.Notice;
import publicinfo.MyFunction;

/**
 * Created by Tyhj on 2016/10/24.
 */

public class NotificationAdpter extends SwipeMenuAdapter<NotificationAdpter.Holder> {

    Context context;
    List<Notice> list;
    LayoutInflater intentFilter;

    public NotificationAdpter(Context context, List<Notice> list){
        this.context=context;
        this.list=list;
        this.intentFilter=LayoutInflater.from(context);
    }



    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        Notice notice=list.get(holder.getPosition());
        holder.type.setVisibility(View.GONE);
        holder.who.setVisibility(View.GONE);
        holder.count.setVisibility(View.VISIBLE);
        holder.tv_status.setVisibility(View.VISIBLE);
        holder.head.setClipToOutline(true);
        holder.head.setOutlineProvider(MyFunction.getOutline(true,10,0));
        holder.head.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(notice.getHead()));
        holder.time.setText(MyFunction.getTime2(notice.getTime()));
        holder.name.setText(notice.getName());
        holder.Signature.setText(notice.getSignature());
        clik(holder.ll_group,notice);

        if(notice.getType()==0){
            if(notice.getStatus()==0){
                holder.tv_status.setVisibility(View.GONE);
                holder.count.setText("1");
            }else if(notice.getStatus()==1){
                holder.count.setVisibility(View.GONE);
                holder.tv_status.setText("已拒绝");
            }else {
                holder.count.setVisibility(View.GONE);
                holder.tv_status.setText("已同意");
            }
        }

    }

    private void clik(View view, final Notice notice) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (notice.getType()){
                    case 0:

                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        View view=intentFilter.inflate(R.layout.item_notice,parent,false);
        return view;
    }

    @Override
    public Holder onCompatCreateViewHolder(View realContentView, int viewType) {
        Holder holder=new Holder(realContentView);
        return holder;
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView head,type;
        LinearLayout ll_group;
        TextView who;
        TextView name,Signature,time,count,tv_status;
        public Holder(View itemView) {
            super(itemView);
            head= (ImageView) itemView.findViewById(R.id.iv_headImage);
            name= (TextView) itemView.findViewById(R.id.tv_group_name);
            Signature= (TextView) itemView.findViewById(R.id.tv_text);
            time= (TextView) itemView.findViewById(R.id.tv_send_time);
            count= (TextView) itemView.findViewById(R.id.tv_msgCount);
            tv_status= (TextView) itemView.findViewById(R.id.tv_status);
            ll_group= (LinearLayout) itemView.findViewById(R.id.ll_group);
            type= (ImageView) itemView.findViewById(R.id.iv_type);
            who= (TextView) itemView.findViewById(R.id.tv_who_send);
        }

    }

}
