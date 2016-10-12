package apis.util;

import java.io.File;
import java.text.SimpleDateFormat;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import apis.listener.FileRcvListener;


public class FileTranTool {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	/**
	 * �����ļ�
	 * @param file ��������ļ�
	 * @param rcvUserJid �����û���jid
	 * @param connection ps:<b>��¼֮��</b>������
	 * */
	public static boolean sendFile(File file,String toUserJid,XMPPConnection connection){
		 Presence p = connection.getRoster().getPresence(toUserJid);
		  if(p==null){
		    System.out.println("�û�������");
		    return false;
		  }
		  toUserJid = p.getFrom();//��ȡ�������û�����
		
		FileTransferManager fileManager = new FileTransferManager(connection);
		OutgoingFileTransfer transfer = fileManager.createOutgoingFileTransfer(toUserJid);
		try {
			transfer.sendFile(file, "sending file");//������ֻ������һ�¶��ѣ���û��ʲô������˼
			
			 System.out.println("sending file status="+transfer.getStatus());
			  long startTime = -1;
			  while (!transfer.isDone()){
			    if (transfer.getStatus().equals(Status.error)){
			      System.out.println("error!!!"+transfer.getError());
			    }else{
			      double progress = transfer.getProgress();
			      if(progress>0.0 && startTime==-1){
			        startTime = System.currentTimeMillis();
			      }
			      progress*=100;
			      System.out.println("status="+transfer.getStatus());
			      System.out.println("progress="+progress+"%");
			    }
			    try {
			      Thread.sleep(1000);//1s��ӡһ�ν���
			    } catch (InterruptedException e) {
			      e.printStackTrace();
			    }
			  }
			  System.out.println("used "+((System.currentTimeMillis()-startTime)/1000)+" seconds  ");
			  
			  return true;
			
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * ����ļ����ռ�����
	 * @param connection �������Ѿ�<b>��¼</b>������
	 * */
	public static void addrcvFileListener(XMPPConnection connection){
		
		FileTransferManager manager = new FileTransferManager(connection);
		/*���������Ҫʱ�Լ���д���̳�fileTransforListenner*/
		manager.addFileTransferListener(new FileRcvListener());
		
	}
	

}
