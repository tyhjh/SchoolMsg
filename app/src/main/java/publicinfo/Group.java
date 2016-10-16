package publicinfo;

import java.io.Serializable;

/**
 * Created by Tyhj on 2016/10/12.
 */

public class Group implements Serializable {
    String  groupImageUrl,groupName;
    int isgroup;

    public Group(String groupImageUrl, String groupName, int isgroup) {
        this.groupImageUrl = groupImageUrl;
        this.groupName = groupName;
        this.isgroup = isgroup;
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
}
