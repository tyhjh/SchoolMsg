package publicinfo;

import java.io.Serializable;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class Group implements Serializable {
    String groupName;
    int isgroup;
    byte[] drawable;
    int lastTime=0;
    String id;

    public Group(String id,String groupName, int isgroup,byte[] drawable) {
        this.groupName = groupName;
        this.isgroup = isgroup;
        this.drawable=drawable;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public byte[] getDrawable() {
        return drawable;
    }

    public void setDrawable(byte[] drawable) {
        this.drawable = drawable;
    }

    public int getIsgroup() {
        return isgroup;
    }

    public void setIsgroup(int isgroup) {
        this.isgroup = isgroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Group){
            return this.getGroupName().equals(((Group) obj).getGroupName());
        }
        return super.equals(obj);
    }
}
