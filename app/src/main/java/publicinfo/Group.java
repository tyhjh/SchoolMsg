package publicinfo;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class Group {
    String  groupImageUrl,groupName,sendTime,text;
    int type,whoSend,status,textCount;

    public Group(String groupImageUrl, String groupName, String sendTime, String text, int type, int whoSend, int status,int textCount) {
        this.groupImageUrl = groupImageUrl;
        this.groupName = groupName;
        this.sendTime = sendTime;
        this.text = text;
        this.type = type;
        this.whoSend = whoSend;
        this.status = status;
        this.textCount=textCount;
    }

    public int getTextCount() {
        return textCount;
    }

    public void setTextCount(int textCount) {
        this.textCount = textCount;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        this.groupImageUrl = groupImageUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSendTime() {
        return sendTime;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWhoSend() {
        return whoSend;
    }

    public void setWhoSend(int whoSend) {
        this.whoSend = whoSend;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
