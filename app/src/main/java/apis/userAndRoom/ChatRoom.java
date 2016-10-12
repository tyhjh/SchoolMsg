package apis.userAndRoom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class ChatRoom {
	
	private XMPPConnection connection = null;
	public ChatRoom(XMPPConnection connection){
		this.connection = connection;
	}
	
    /** 
     * ��ʼ���������б� 
     */  
    public List<HostedRoom> getHostRooms() {  
        if (connection == null)  
            return null;  
        Collection<HostedRoom> hostrooms = null;  
        List<HostedRoom> roominfos = new ArrayList<HostedRoom>();  
        try {  
            new ServiceDiscoveryManager(connection);  
            hostrooms = MultiUserChat.getHostedRooms(connection,  
                    connection.getServiceName());  
            for (HostedRoom entry : hostrooms) {  
                roominfos.add(entry);  
//                Log.i("room",  
//                        "���֣�" + entry.getName() + " - ID:" + entry.getJid());  
            }  
//            Log.i("room", "�����������:" + roominfos.size());  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
        return roominfos;  
    }
    
    /** 
     * �������� 
     *  
     * @param roomName 
     *            �������� 
     */  
    public MultiUserChat createRoom(String user, String roomName,  
            String password) {  
        if (connection == null)  
            return null;  
  
        MultiUserChat muc = null;  
        try {  
            // ����һ��MultiUserChat  
            muc = new MultiUserChat(connection, roomName + "@conference."  
                    + connection.getServiceName());  
            // ����������  
            muc.create(roomName);  
            // ��������ҵ����ñ�  
            Form form = muc.getConfigurationForm();  
            // ����ԭʼ������һ��Ҫ�ύ���±���  
            Form submitForm = form.createAnswerForm();  
            // ��Ҫ�ύ�ı����Ĭ�ϴ�  
            for (Iterator<FormField> fields = form.getFields(); fields  
                    .hasNext();) {  
                FormField field = (FormField) fields.next();  
                if (!FormField.TYPE_HIDDEN.equals(field.getType())  
                        && field.getVariable() != null) {  
                    // ����Ĭ��ֵ��Ϊ��  
                    submitForm.setDefaultAnswer(field.getVariable());  
                }  
            }  
            // ���������ҵ���ӵ����  
            List<String> owners = new ArrayList<String>();  
            owners.add(connection.getUser());// �û�JID  
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);  
            // �����������ǳ־������ң�����Ҫ����������  
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);  
            // ������Գ�Ա����  
            submitForm.setAnswer("muc#roomconfig_membersonly", false);  
            // ����ռ��������������  
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);  
            if (!password.equals("")) {  
                // �����Ƿ���Ҫ����  
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",  
                        true);  
                // ���ý�������  
                submitForm.setAnswer("muc#roomconfig_roomsecret", password);  
            }  
            // �ܹ�����ռ������ʵ JID �Ľ�ɫ  
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
            // ��¼����Ի�  
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);  
            // ������ע����ǳƵ�¼  
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);  
            // ����ʹ�����޸��ǳ�  
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);  
            // �����û�ע�᷿��  
            submitForm.setAnswer("x-muc#roomconfig_registration", false);  
            // ��������ɵı�����Ĭ��ֵ����������������������  
            muc.sendConfigurationForm(submitForm);  
        } catch (XMPPException e) {  
            e.printStackTrace();  
            return null;  
        }  
        return muc;  
    }  

    
    /** 
     * ��������� 
     *  
     * @param user 
     *            �ǳ� 
     * @param password 
     *            ���������� 
     * @param roomsName 
     *            �������� 
     */  
    public MultiUserChat joinMultiUserChat(String user, String roomsName,  
            String password) {  
        if (connection == null)  
            return null;  
        try {  
            // ʹ��XMPPConnection����һ��MultiUserChat����  
            MultiUserChat muc = new MultiUserChat(connection, roomsName  
                    + "@conference." + connection.getServiceName());  
            // �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����  
            DiscussionHistory history = new DiscussionHistory();  
            history.setMaxChars(0);  
            // history.setSince(new Date());  
            // �û�����������  
            muc.join(user, password, history,  
                    SmackConfiguration.getPacketReplyTimeout());  
//            Log.i("MultiUserChat", "�����ҡ�"+roomsName+"������ɹ�........");  
            return muc;  
        } catch (XMPPException e) {  
            e.printStackTrace();  
//            Log.i("MultiUserChat", "�����ҡ�"+roomsName+"������ʧ��........");  
            return null;  
        }  
    }  
  
    /** 
     * ��ѯ�����ҳ�Ա���� 
     *  
     * @param muc 
     */  
    public List<String> findMulitUser(MultiUserChat muc) {  
        if (connection == null)  
            return null;  
        List<String> listUser = new ArrayList<String>();  
        Iterator<String> it = muc.getOccupants();  
        // ��������������Ա����  
        while (it.hasNext()) {  
            // �����ҳ�Ա����  
            String name = StringUtils.parseResource(it.next());  
            listUser.add(name);  
        }  
        return listUser;  
    }  
}
