package publicinfo;

import android.net.Uri;

import java.io.File;

/**
 * Created by Tyhj on 2016/10/17.
 */

public class Picture {
    String path;
    String url;
    int checkable;

    public int isCheckable() {
        return checkable;
    }

    public void setCheckable(int checkable) {
        this.checkable = checkable;
    }

    public Picture(String file) {
        this.path = file;
        checkable=0;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String file) {
        this.path = file;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
