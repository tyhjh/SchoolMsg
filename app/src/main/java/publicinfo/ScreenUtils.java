package publicinfo;

import android.content.Context;

/**
 * Created by Tyhj on 2016/10/21.
 */
public class ScreenUtils {
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
