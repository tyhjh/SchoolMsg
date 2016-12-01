package publicinfo;

import java.io.Serializable;
import java.sql.Time;

/**
 * Created by Tyhj on 2016/10/14.
 */

public class Msg_chat implements Serializable {
    int who,type,status;
    int time;
    String name;
    String text;
    String imageUrl;
    byte[] headImageUrl;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Msg_chat(int who, int type, int status, String text, String imageUrl, byte[] headImageUrl, String name, int time) {
        this.who = who;
        this.type = type;
        this.status = status;
        this.text = text;
        this.imageUrl = imageUrl;
        this.headImageUrl = headImageUrl;
        this.name=name;
        this.time=time;
    }

    public byte[] getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(byte[] headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public int getWho() {
        return who;
    }

    public void setWho(int who) {
        this.who = who;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
