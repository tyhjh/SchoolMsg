package publicinfo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tyhj on 2016/11/28.
 */

public class MyHttp {
    public static String ipAddress="http://120.27.49.173:8080";

    public static String getIpAddress() {
        return ipAddress;
    }

    public static void setIpAddress(String ipAddress) {
        MyHttp.ipAddress = ipAddress;
    }


    //用户注册
    public static String Signup(String sch_number, String stu_number, String sch_stu_pas, String phone, String stu_pas) {
        String url = ipAddress + "/v1.0/users";
        String date = "school_num=" + sch_number
                + "&st_num=" + stu_number
                + "&st_pwd=" + sch_stu_pas
                + "&phone=" + phone
                + "&pwd=" + stu_pas;
        JSONObject jsonObject = getJson(date, url, "POST");
        try {
            if (jsonObject != null) {
                if (jsonObject.getInt("code") == 200)
                    return null;
                else
                    return jsonObject.getString("msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "服务器无响应，请稍后再试";
        }
        return "服务器无响应，请稍后再试";
    }

    //发送消息
    public static String SendMessage(String userid,String groupid,String content){
        String url = ipAddress +"/v1.0/users/"+ userid+"/messages/push";
        String data="userid="+userid
                +"&groupid="+groupid
                +"&content="+content;
        JSONObject jsonObject=getJson(data,url,"POST");
        try {
            if (jsonObject != null) {
                if (jsonObject.getInt("code") == 200)
                    return null;
                else
                    return jsonObject.getString("msg");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "失败";
    }

    //获取用户所在群组
    public static boolean getStuGroup(){
        String url=ipAddress+"/v1.0/users/"+UserInfo.getId()+"/group";
        JSONObject jsonObject=getJson(null,url,"GET");
        try {
            if(jsonObject!=null&&jsonObject.getInt("code")==200){
                UserInfo.setGroupId(jsonObject.getJSONObject("data").getString("group"));
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    //获取信息
    public static JSONObject getJson(String data, String url, String way) {
        HttpURLConnection conn = null;
        URL mURL = null;
        if (way.equals("GET")) {
            try {
                if(data==null)
                    mURL = new URL(url);
                else
                    mURL = new URL(url + "?" + data);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.addRequestProperty("Nonce", "123");
                conn.addRequestProperty("Timestamp", "1477985732446");
                conn.addRequestProperty("Userid", UserInfo.getId());
                conn.addRequestProperty("Signature", new SHA1().getDigestOfString(("1231477985732446"+UserInfo.getId()).getBytes()));
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(30000);
                InputStream is = conn.getInputStream();
                String state = getStringFromInputStream(is);
                //Log.e("Tag", state);
                JSONObject jsonObject = new JSONObject(state);
                if (jsonObject != null)
                    return jsonObject;
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else if (way.equals("POST")) {
            try {
                mURL = new URL(url);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.addRequestProperty("Nonce", "123");
                conn.addRequestProperty("Timestamp", "1477985732446");
                conn.addRequestProperty("Userid", UserInfo.getId());
                conn.addRequestProperty("Signature", new SHA1().getDigestOfString(("1231477985732446"+UserInfo.getId()).getBytes()));
                //conn.addRequestProperty("头","我就是头");
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(30000);
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(data.getBytes());
                out.flush();
                out.close();
                int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    String state = getStringFromInputStream(is);
                    Log.e("Tag",state);
                    JSONObject jsonObject = new JSONObject(state);
                    if (jsonObject != null)
                        return jsonObject;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
        return null;
    }


    //数据流转字符串
    public static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        // 一定要写len=is.read(buffer)
        // 如果while((is.read(buffer))!=-1)则无法将数据写入buffer中
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }


    //扫描二维码登录
    public static void twoCode(String code,String action){

        String url=code;

        if(action.equals("reject")){
            String date="action="+action;
            JSONObject jsonObject=getJson(date,url,"POST");
            return;
        }

        String userid=UserInfo.getId();
        String date="action="+action+"&userid="+userid;
        JSONObject jsonObject=getJson(date,url,"POST");
    }

}

