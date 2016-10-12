package apis.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;

import apis.conf.SmackConf;

public class GetOffLineMessages {
	
    /** 
     * ��ȡ������Ϣ 
     *  
     * @return 
     */  
    public Map<String, List<HashMap<String, String>>> getHisMessage(String SERVER_HOST,int SERVER_PORT,String SERVER_NAME  ) {  
    	XMPPConnection connection = null;
    	
    	try {  
            if (null == connection || !connection.isAuthenticated()) {  
                XMPPConnection.DEBUG_ENABLED = false;// ����DEBUGģʽ 
                // ��������  
                ConnectionConfiguration config = new ConnectionConfiguration(  
                        SERVER_HOST, SERVER_PORT, SERVER_NAME);  
                config.setReconnectionAllowed(true);  
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);  
//                config.setSendPresence(false); // ״̬��Ϊ���ߣ�Ŀ��Ϊ��ȡ������Ϣ  
                config.setSASLAuthenticationEnabled(false); 
                connection = new XMPPConnection(config);  
                connection.connect();// ���ӵ�������  
                // ���ø���Provider����������ã�����޷���������  
                SmackConf.configureConnection(ProviderManager.getInstance());
            }  
        } catch (XMPPException xe) {  
            xe.printStackTrace();  
            connection = null;  
        }  
    	
        if (connection == null)  
            return null;  
        Map<String, List<HashMap<String, String>>> offlineMsgs = null;  
  
        try {  
            OfflineMessageManager offlineManager = new OfflineMessageManager(  
                    connection);  
            Iterator<Message> it = offlineManager.getMessages();  
  
            int count = offlineManager.getMessageCount();  
            if (count <= 0)  
                return null;  
            offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();  
  
            while (it.hasNext()) {  
                Message message = it.next();  
                String fromUser = StringUtils.parseName(message.getFrom());  
                ;  
                HashMap<String, String> histrory = new HashMap<String, String>();  
                histrory.put("useraccount",  
                        StringUtils.parseName(connection.getUser()));  
                histrory.put("friendaccount", fromUser);  
                histrory.put("info", message.getBody());  
                histrory.put("type", "left");  
                if (offlineMsgs.containsKey(fromUser)) {  
                    offlineMsgs.get(fromUser).add(histrory);  
                } else {  
                    List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();  
                    temp.add(histrory);  
                    offlineMsgs.put(fromUser, temp);  
                }  
            }  
            offlineManager.deleteMessages();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return offlineMsgs;  
    }  
    
    public static void main(String[] args) {
    	Map<String, List<HashMap<String, String>>> map = new GetOffLineMessages().getHisMessage("192.168.222.130", 5222, "master");
    	System.out.println(map.size());
	}

}
