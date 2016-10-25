package adpter;

import android.content.Context;
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
        holder.head.setClipToOutline(true);
        holder.head.setOutlineProvider(MyFunction.getOutline(true,10,0));
        holder.head.setImageDrawable(FormatTools.getInstance().Bytes2Drawable(notice.getHead()));
        holder.name.setText(notice.getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        ImageView head;
        LinearLayout ll_group;
        Button button;
        TextView name;
        public Holder(View itemView) {
            super(itemView);
            head= (ImageView) itemView.findViewById(R.id.iv_headImage);
            ll_group= (LinearLayout) itemView.findViewById(R.id.ll_group);
            button= (Button) itemView.findViewById(R.id.btn_add);
            name= (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

}
