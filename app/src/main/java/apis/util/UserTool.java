package apis.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * �����࣬��ȡ�û��Ƿ�����
 * */
public class UserTool {
    /** 
     * �ж�OpenFire�û���״̬ strUrl :  
     * url��ʽ - http://my.openfire.com:9090/plugins/presence 
     * /status?jid=user1@SERVER_NAME&type=xml  
     * ����ֵ : 0 - �û�������; 1 - �û�����; 2 - �û�����  
     * ˵�� ������Ҫ�� OpenFire���� presence �����ͬʱ�����κ��˶����Է��� 
     */     
    public int IsUserOnLine(String user,String host,String server_name) {  
        String url = "http://"+host+":9090/plugins/presence/status?" +  
                "jid="+ user +"@"+ server_name +"&type=xml";  
//        System.out.println(url);
        int shOnLineState = 0; // ������  
        try {  
            URL oUrl = new URL(url);  
            URLConnection oConn = oUrl.openConnection();  
            if (oConn != null) {  
                BufferedReader oIn = new BufferedReader(new InputStreamReader(  
                        oConn.getInputStream()));  
                if (null != oIn) {  
                    String strFlag = oIn.readLine();  
                    oIn.close();  
                    System.out.println("strFlag"+strFlag);  
                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {  
                        shOnLineState = 2;  
                    }  
                    if (strFlag.indexOf("type=\"error\"") >= 0) {  
                        shOnLineState = 0;  
                    } else if (strFlag.indexOf("priority") >= 0  
                            || strFlag.indexOf("id=\"") >= 0) {  
                        shOnLineState = 1;  
                    }  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return shOnLineState;  
    } 

}
