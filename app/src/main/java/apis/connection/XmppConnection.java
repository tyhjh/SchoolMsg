package apis.connection;

import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;

import apis.conf.SMKProperties;
import apis.conf.SmackConf;


public class XmppConnection{
	private int SERVER_PORT;  
    private String SERVER_HOST;
    private String SERVER_NAME; 
    private XMPPConnection connection = null;   
    private static XmppConnection xmppConnection = new XmppConnection();  
    private static Properties properties = new Properties();
    
    /**
     * ���췽��
     * */
    private XmppConnection(){
//    	try {
//    		//��������
//			properties.load(this.getClass().getClassLoader().getResourceAsStream("conf/smack.properties"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	SERVER_HOST = properties.getProperty("SERVER_HOST");
//    	SERVER_PORT = Integer.parseInt(properties.getProperty("SERVER_PORT"));
//    	SERVER_NAME = properties.getProperty("SERVER_NAME");
    	
    	SERVER_HOST = SMKProperties.HOST;
    	SERVER_NAME = SMKProperties.HOSTNAME;
    	SERVER_PORT = SMKProperties.PORT;
    }
    
    /** 
     * ����ģʽ 
     *  
     * @return 
     */  
    synchronized public static XmppConnection getInstance() {  
        return xmppConnection;  
    }  
  
    /** 
     * �������� 
     */  
    public XMPPConnection getConnection() {  
        if (connection == null) {  
            openConnection();  
        }  
        return connection;  
    }
    
    /** 
     * ������ 
     */  
    public boolean openConnection() {  
        try {  
            if (null == connection || !connection.isAuthenticated()) {  
//                XMPPConnection.DEBUG_ENABLED = false;// ����DEBUGģʽ 
                XMPPConnection.DEBUG_ENABLED = false;// ����DEBUGģʽ
                // ��������  
                ConnectionConfiguration config = new ConnectionConfiguration(  
                        SERVER_HOST, SERVER_PORT, SERVER_NAME);  
                config.setReconnectionAllowed(true);  
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);  
//                config.setSendPresence(true); // ״̬��Ϊ���ߣ�Ŀ��Ϊ��ȡ������Ϣ  
                config.setSASLAuthenticationEnabled(false); 
                connection = new XMPPConnection(config);  
                connection.connect();// ���ӵ�������  
                // ���ø���Provider����������ã�����޷���������  
                SmackConf.configureConnection(ProviderManager.getInstance());
                return true;  
            }  
        } catch (XMPPException xe) {  
            xe.printStackTrace();  
            connection = null;  
        }  
        return false;  
    }  
    
    /** 
     * �ر����� 
     */  
    public void closeConnection() {  
        if(connection!=null){  
            //�Ƴ��B�ӱO   
            //connection.removeConnectionListener(connectionListener);  
            if(connection.isConnected())  
                connection.disconnect();  
            connection = null;  
        }  
//        Log.i("XmppConnection", "�P�]�B��");  
    } 
    
    
}