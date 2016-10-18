package apis.userAndRoom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import apis.connection.XmppConnection;
import apis.util.File2Bytes;
import publicinfo.MyFunction;


/**
 * �û�ע��������
 * 
 * */
public class User {

    private XmppConnection connection = null;
	private XMPPConnection connection1 = null;
	
	/**
	 * 	���췽��
	 * @param connect xmpp����
	 * */
	public User(XmppConnection connection){
		this.connection = connection;
        connection1=connection.getConnection();
	}
	
	/**
	 * ��ȡ����
	 * */
	public XmppConnection getXmppConnection(){
		if(connection!=null)
			return connection;
		return null;
	}

    public void logout(){
        if(connection!=null){
            connection.getInstance().closeConnection();
        }
        MyFunction.setUser(null);
    }
    /** 
     * ��¼ 
     *  
     * @param account 
     *            ��¼�ʺ� 
     * @param password 
     *            ��¼���� 
     * @return 
     */  
    public boolean login(String account, String password) {  
        try {  
            if (connection1 == null)
                return false;

            connection1.login(account, password);
            // �����ھQ��B  
            Presence presence = new Presence(Presence.Type.available);
            connection1.sendPacket(presence);
            // ����B�ӱO  
            
//            connectionListener = new TaxiConnectionListener();  
//            connection.addConnectionListener(connectionListener);  
            
            return true;  
        } catch (XMPPException xe) {  
            xe.printStackTrace();  
        }  
        return false;  
    } 

    /** 
     * ע�� 
     *  
     * @param account 
     *            ע���ʺ� 
     * @param password 
     *            ע������ 
     * @return 1��ע��ɹ� 0��������û�з��ؽ��2��ע��ʧ�� 
     */  
    public String regist(String account, String password) {  
        if (connection1 == null)
            return "0";  
        
        AccountManager manager = new AccountManager(connection1);
        try {
			manager.createAccount(account, password);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "2";
		}
        return "1";


        
//        Registration reg = new Registration();
//        reg.setType(IQ.Type.SET);
//        reg.setTo(connection.getServiceName());
        // ע������createAccountע��ʱ��������UserName������jid����"@"ǰ��Ĳ��֡�
//        reg.setUsername(account);
//        reg.setPassword(password);
        // ���addAttribute����Ϊ�գ������������������־��android�ֻ������İɣ���������
//        reg.addAttribute("android", "geolo_createUser_android");
//        reg.setAttributes(attributes);
//        PacketFilter filter = new AndFilter(new PacketIDFilter(
//                reg.getPacketID()), new PacketTypeFilter(IQ.class));
//        PacketCollector collector = connection.createPacketCollector(
//                filter);
//        connection.sendPacket(reg);
//        IQ result = (IQ) collector.nextResult(SmackConfiguration
//                .getPacketReplyTimeout());
//        // Stop queuing resultsֹͣ����results���Ƿ�ɹ��Ľ����
//        collector.cancel();
//        if (result == null) {
//            return "0";
//        } else if (result.getType() == IQ.Type.RESULT) {
//            return "1";
//        } else { // if (result.getType() == IQ.Type.ERROR)
//            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
//                return "2";
//            } else {
//                return "3";
//            }
//        }
    }
    
    /** 
     * �����û�״̬ 
     */  
    public void setPresence(int code) {
        XMPPConnection con = connection1;
        if (con == null)  
            return;
        Presence presence;
        switch (code) {  
        case 0:  
            presence = new Presence(Presence.Type.available);  
            con.sendPacket(presence);  
//            Log.v("state", "��������");  
            break;  
        case 1:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.chat);  
            con.sendPacket(presence);  
//            Log.v("state", "����Q�Ұ�");  
            break;  
        case 2:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.dnd);  
            con.sendPacket(presence);  
//            Log.v("state", "����æµ");  
            break;  
        case 3:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.away);  
            con.sendPacket(presence);  
//            Log.v("state", "�����뿪");  
            break;  
        case 4:  
            Roster roster = con.getRoster();  
            Collection<RosterEntry> entries = roster.getEntries();  
            for (RosterEntry entry : entries) {  
                presence = new Presence(Presence.Type.unavailable);  
                presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
                presence.setFrom(con.getUser());  
                presence.setTo(entry.getUser());  
                con.sendPacket(presence);  
//                Log.v("state", presence.toXML());  
            }  
            // ��ͬһ�û��������ͻ��˷�������״̬  
            presence = new Presence(Presence.Type.unavailable);  
            presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
            presence.setFrom(con.getUser());  
            presence.setTo(StringUtils.parseBareAddress(con.getUser()));  
            con.sendPacket(presence);  
//            Log.v("state", "��������");  
            break;  
        case 5:  
            presence = new Presence(Presence.Type.unavailable);  
            con.sendPacket(presence);  
//            Log.v("state", "��������");  
            break;  
        default:  
            break;  
        }
    }  
    
    /** 
     * ��ȡ������ 
     *  
     * @return �����鼯�� 
     */  
    public List<RosterGroup> getGroups() {  
        if (connection1 == null)
            return null;  
        List<RosterGroup> grouplist = new ArrayList<RosterGroup>();  
        Collection<RosterGroup> rosterGroup =connection1.getRoster()
                .getGroups();  
        Iterator<RosterGroup> i = rosterGroup.iterator();  
        while (i.hasNext()) {  
            grouplist.add(i.next());  
        }  
        return grouplist;  
    }  
  
    /** 
     * ��ȡĳ������������к��� 
     *  
     * @param roster 
     * @param groupName 
     *            ���� 
     * @return 
     */  
    public List<RosterEntry> getEntriesByGroup(String groupName) {  
        if (connection1 == null)
            return null;  
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();  
        RosterGroup rosterGroup = connection1.getRoster().getGroup(
                groupName);  
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        while (i.hasNext()) {  
            Entrieslist.add(i.next());  
        }  
        return Entrieslist;  
    }  
  
    
    /** 
     * ��ȡ���к�����Ϣ 
     *  
     * @return 
     */  
    public List<RosterEntry> getAllEntries() {  
        if (connection1 == null)
            return null;  
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();  
        Collection<RosterEntry> rosterEntry = connection1.getRoster()
                .getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        while (i.hasNext()) {  
            Entrieslist.add(i.next());  
        }  
        return Entrieslist;  
    }  
  
    /** 
     * ��ȡ�û�VCard��Ϣ 
     *  
     * @param connection 
     * @param user 
     * @return 
     * @throws XMPPException 
     */  
    public VCard getUserVCard(String user) {  
        if (connection1 == null)
            return null;  
        VCard vcard = new VCard();  
        try {  
            vcard.load(connection1, user);
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
        return vcard;  
    }
    
    /** 
     * ��ȡ�û�ͷ����Ϣ 
     *  
     * @param connection 
     * @param user 
     * @return 
     */  
//    public Drawable getUserImage(String user) {  
//        if (getConnection() == null)  
//            return null;  
//        ByteArrayInputStream bais = null;  
//        try {  
//            VCard vcard = new VCard();  
//            // ���������룬���No VCard for  
//            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",  
//                    new org.jivesoftware.smackx.provider.VCardProvider());  
//            if (user == "" || user == null || user.trim().length() <= 0) {  
//                return null;  
//            }  
//            vcard.load(getConnection(), user + "@"  
//                    + getConnection().getServiceName());  
//  
//            if (vcard == null || vcard.getAvatar() == null)  
//                return null;  
//            bais = new ByteArrayInputStream(vcard.getAvatar());  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//            return null;  
//        }  
//        return FormatTools.getInstance().InputStream2Drawable(bais);  
//    } 
    
    /** 
     * ���һ������ 
     *  
     * @param groupName 
     * @return 
     */  
    public boolean addGroup(String groupName) {  
        if (connection1 == null)
            return false;  
        try {  
            connection1.getRoster().createGroup(groupName);
//            Log.v("addGroup", groupName + "�����ɹ�");  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
  
    /** 
     * ɾ������ 
     *  
     * @param groupName 
     * @return 
     */  
//    public boolean removeGroup(String groupName) {  
//        return true;  
//    }  
    
    /** 
     * ��Ӻ��� �޷��� 
     *  
     * @param userName 
     * @param name 
     * @return 
     */  
    public boolean addUser(String userName, String name) {  
        if (connection1 == null)
            return false;  
        try {  
        	connection1.getRoster().createEntry(userName, name, null);
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
  
    /** 
     * ��Ӻ��� �з��� 
     *  
     * @param userName 
     * @param name 
     * @param groupName 
     * @return 
     */  
    public boolean addUser(String userName, String name, String groupName) {  
        if (connection1 == null)
            return false;  
        try {  
            Presence subscription = new Presence(Presence.Type.subscribed);  
            subscription.setTo(userName);  
            userName += "@" + connection1.getServiceName();
            connection1.sendPacket(subscription);
            connection1.getRoster().createEntry(userName, name,
                    new String[] { groupName });  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
  
    /** 
     * ɾ������ 
     *  
     * @param userName 
     * @return 
     */  
    public boolean removeUser(String userName) {  
        if (connection1 == null)
            return false;  
        try {  
            RosterEntry entry = null;  
            if (userName.contains("@"))  
                entry = connection1.getRoster().getEntry(userName);
            else  
                entry = connection1.getRoster().getEntry(
                        userName + "@" + connection1.getServiceName());
            if (entry == null)  
                entry = connection1.getRoster().getEntry(userName);
            connection1.getRoster().removeEntry(entry);
  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
  
    /** 
     * ��ѯ�û� 
     *  
     * @param userName 
     * @return 
     * @throws XMPPException 
     */  
    public List<HashMap<String, String>> searchUsers(String userName) {  
        if (connection1 == null)
            return null;  
        HashMap<String, String> user = null;  
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();  
        try {  
            new ServiceDiscoveryManager(connection1);
  
            UserSearchManager usm = new UserSearchManager(connection1);
  
            Form searchForm = usm.getSearchForm(connection1
                    .getServiceName());  
            Form answerForm = searchForm.createAnswerForm();  
            answerForm.setAnswer("userAccount", true);  
            answerForm.setAnswer("userPhote", userName);  
            ReportedData data = usm.getSearchResults(answerForm, "search"  
                    + connection1.getServiceName());
  
            Iterator<Row> it = data.getRows();  
            Row row = null;  
            while (it.hasNext()) {  
                user = new HashMap<String, String>();  
                row = it.next();  
                user.put("userAccount", row.getValues("userAccount").next()  
                        .toString());  
                user.put("userPhote", row.getValues("userPhote").next()  
                        .toString());  
                results.add(user);  
                // �����ڣ����з���,UserNameһ���ǿգ����������������裬һ���ǿ�  
            }  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
        return results;  
    }
    
    
    
    /** 
     * �޸����� 
     *  
     * @param connection
     * @param status 
     */  
    public void changeStateMessage(String status) {  
        if (connection1 == null)
            return;  
        Presence presence = new Presence(Presence.Type.available);  
        presence.setStatus(status);  
        connection1.sendPacket(presence);
    }  
  
    /** 
     * �޸��û�ͷ�� 
     *  
     * @param file 
     */  
    public boolean changeImage(File file) {  
        if (connection1 == null)
            return false;  
        try {  
            VCard vcard = new VCard();  
            vcard.load(connection1);
  
            byte[] bytes;  
  
            bytes = File2Bytes.getFileBytes(file);
            String encodedImage = StringUtils.encodeBase64(bytes);  
//            vcard.setAvatar(bytes, encodedImage);  
            vcard.setAvatar(bytes);
            vcard.setEncodedImage(encodedImage);  
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"  
                    + encodedImage + "</BINVAL>", true);  
  
            ByteArrayInputStream bais = new ByteArrayInputStream(  
                    vcard.getAvatar());  
            
//            FormatTools.getInstance().InputStream2Bitmap(bais);  
  
            vcard.save(connection1);
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
    
    /** 
     * ɾ����ǰ�û� 
     *  
     * @return 
     */  
    public boolean deleteAccount() {  
        if (connection1== null)
            return false;  
        try {  
            connection1.getAccountManager().deleteAccount();
            return true;  
        } catch (XMPPException e) {  
            return false;  
        }  
    }  
  
    /** 
     * �޸����� 
     *  
     * @return 
     */  
    public boolean changePassword(String pwd) {  
        if (connection1 == null)
            return false;  
        try {  
            connection1.getAccountManager().changePassword(pwd);
            return true;  
        } catch (XMPPException e) {  
            return false;  
        }  
    }
    
    /** 
     * �����ļ� 
     *  
     * @param user 
     * @param filePath 
     */  
    public void sendFile(String user, String filePath) {  
        if (connection1 == null)
            return;  
        // �����ļ����������  
        FileTransferManager manager = new FileTransferManager(connection1);
  
        // ����������ļ�����  
        OutgoingFileTransfer transfer = manager  
                .createOutgoingFileTransfer(user);  
  
        // �����ļ�  
        try {  
            transfer.sendFile(new File(filePath), "You won't believe this!");  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * ��ȡ����ChatManager��
     * */
    public ChatManager getChatManager(){
    	ChatManager chatmanager = connection1.getChatManager();
    	return chatmanager;
    }

}

