package publicinfo;

import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class Group implements Serializable {
    String  groupImageUrl,groupName;
    int isgroup;
    byte[] drawable;

    public Group(String groupImageUrl, String groupName, int isgroup,byte[] drawable) {
        this.groupImageUrl = groupImageUrl;
        this.groupName = groupName;
        this.isgroup = isgroup;
        this.drawable=drawable;
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Group){
            return this.getGroupName().equals(((Group) obj).getGroupName());
        }
        return super.equals(obj);
    }
}
