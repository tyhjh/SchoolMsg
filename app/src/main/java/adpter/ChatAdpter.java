package adpter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyhj.schoolmsg.R;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import myViews.CircularAnim;
import myinterface.ExpendImage;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;

import static android.content.ContentValues.TAG;

/**
 * Created by Tyhj on 2016/10/14.
 */

public class ChatAdpter extends ArrayAdapter<Msg_chat> {
    Drawable headImage;
    List<Msg_chat> msg_chats;
    ImageLoader imageLoader;
    private ExpendImage expendImage;

    public ChatAdpter(Context context, int resource, List<Msg_chat> objects) {
        super(context, resource, objects);
        msg_chats = objects;
        imageLoader = ImageLoader.getInstance();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public Drawable getHeadImage() {
        return headImage;
    }

    public void setHeadImage(Drawable headImage) {
        this.headImage = headImage;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Msg_chat msg_chat = getItem(position);
        View view = null;
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        if (msg_chat.getWho() == 1) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_me, null);
            viewHolder.iv_voice = (ImageView) view.findViewById(R.id.iv_voice);
            viewHolder.ll_voice = (LinearLayout) view.findViewById(R.id.ll_voice);
            viewHolder.status = (ImageView) view.findViewById(R.id.iv_status);
            viewHolder.text = (TextView) view.findViewById(R.id.tv_mytext);
            viewHolder.image = (ImageView) view.findViewById(R.id.iv_myimage);
            viewHolder.progressBar = (SimpleArcLoader) view.findViewById(R.id.progressBar);
            if (msg_chat.getType() == 0) {
                viewHolder.text.setText(msg_chat.getText());
            } else if (msg_chat.getType() == 1) {
                viewHolder.text.setVisibility(View.GONE);
                imageLoader.displayImage(msg_chat.getText(), viewHolder.image, MyFunction.getOption());
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expendImage.callBack(msg_chat);
                    }
                });
            } else if (msg_chat.getType() == 2) {
                //语音
                final boolean[] first = {true};
                viewHolder.text.setText(msg_chat.getImageUrl() + "''");
                final MediaPlayer player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(msg_chat.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                        CircularAnim.hide(viewHolder.ll_voice).go();
                    }
                });
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        // 伸展按钮
                        CircularAnim.show(viewHolder.ll_voice).go();
                    }
                });
                viewHolder.iv_voice.setVisibility(View.VISIBLE);
                viewHolder.ll_voice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!MyFunction.isIntenet(getContext()))
                            return;
                        if (player.isPlaying())
                            return;
                        else if (first[0]) {
                            player.prepareAsync();
                            first[0] = false;
                        } else if (!first[0]) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                            CircularAnim.hide(viewHolder.ll_voice).go();
                            player.start();
                        }
                    }
                });
            }
            if (msg_chat.getStatus() == -1) {
                Picasso.with(getContext()).load(R.drawable.ic_sent).into(viewHolder.status);
            } else {
                Picasso.with(getContext()).load(R.drawable.ic_sending).into(viewHolder.status);
            }

        } else if (msg_chat.getWho() == 2) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_you, null);
            viewHolder.text = (TextView) view.findViewById(R.id.tv_yourtext);
            viewHolder.ll_voice = (LinearLayout) view.findViewById(R.id.ll_voice);
            viewHolder.iv_voice = (ImageView) view.findViewById(R.id.iv_voice);
            viewHolder.image = (ImageView) view.findViewById(R.id.iv_yourimage);
            viewHolder.headImage = (ImageView) view.findViewById(R.id.iv_yourheadImage);
            viewHolder.headImage.setClipToOutline(true);
            viewHolder.headImage.setOutlineProvider(MyFunction.getOutline(true, 10, 0));
            viewHolder.headImage.setImageDrawable(getHeadImage());
            viewHolder.progressBar = (SimpleArcLoader) view.findViewById(R.id.progressBar);
            if (msg_chat.getType() == 0) {
                viewHolder.text.setText(msg_chat.getText());
            } else if (msg_chat.getType() == 1) {
                viewHolder.text.setVisibility(View.GONE);
                Picasso.with(getContext()).load(msg_chat.getText()).into(viewHolder.image);
                //imageLoader.displayImage(msg_chat.getText(), viewHolder.image, MyFunction.getOption());


                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expendImage.callBack(msg_chat);
                    }
                });
            } else if (msg_chat.getType() == 2) {
                //语音
                //语音
                final boolean[] first = {true};
                viewHolder.text.setText(msg_chat.getImageUrl() + "''");
                final MediaPlayer player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(msg_chat.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                        CircularAnim.hide(viewHolder.ll_voice).go();
                    }
                });
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        // 伸展按钮
                        CircularAnim.show(viewHolder.ll_voice).go();
                    }
                });
                viewHolder.iv_voice.setVisibility(View.VISIBLE);
                viewHolder.ll_voice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!MyFunction.isIntenet(getContext()))
                            return;
                        if (player.isPlaying())
                            return;
                        else if (first[0]) {
                            player.prepareAsync();
                            first[0] = false;
                        } else if (!first[0]) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                            CircularAnim.hide(viewHolder.ll_voice).go();
                            player.start();
                        }
                    }
                });

            }

        } else if (msg_chat.getWho() == 0) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_chatlist_time, null);
            viewHolder.text = (TextView) view.findViewById(R.id.tv_chatTiem);
            viewHolder.text.setText(msg_chat.getText());
        }

        for (int i = msg_chats.size() - 1; i >= 0; i--) {
            if (msg_chats.get(i).getWho() == 0) {
                if (msg_chats.get(i).getText().contains("月"))
                    break;
                msg_chats.get(i).setText(MyFunction.getTime2(msg_chats.get(i).getTime()));
            }
        }

        return view;
    }

    public void chageStatus(int id, int status) {

        notifyDataSetChanged();
    }

    @Override
    public void add(Msg_chat object) {
        if (object.getWho() == 0) {
            for (int i = msg_chats.size() - 1; i >= 0; i--) {
                if (msg_chats.get(i).getWho() == 0) {
                    if (msg_chats.get(i).getText().contains("月"))
                        break;
                    msg_chats.get(i).setText(MyFunction.getTime2(msg_chats.get(i).getTime()));
                }
            }
        }
        super.add(object);
    }

    public List<Msg_chat> getMsg_chats() {
        return msg_chats;
    }

    public void update() {
        for (int i = msg_chats.size() - 1; i >= 0; i--) {
            if (msg_chats.get(i).getStatus() == -1 && msg_chats.get(i).getWho() == 1) {
                break;
            } else if (msg_chats.get(i).getWho() == 1 && msg_chats.get(i).getStatus() == 1) {
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
        SimpleArcLoader progressBar;
        ImageView headImage, image, status, iv_voice;
        LinearLayout ll_voice;
    }

    public void setExpendImage(ExpendImage expendImage){
        this.expendImage=expendImage;
    }
}
