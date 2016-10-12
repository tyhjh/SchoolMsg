package apis.listener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

public class FileRcvListener implements FileTransferListener{

	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		// TODO Auto-generated method stub
		final SimpleDateFormat sdf = new SimpleDateFormat();
		
		/*��������Ǿܾ��ļ����������reject*/
		final IncomingFileTransfer ift = request.accept();
		
		System.out.println("file income:"+request.getFileName()+"\tsize"+request.getFileSize());
		String filePathString = "D:\\datas\\"+request.getFileName();//�ļ��洢λ��
		try {
			ift.recieveFile(new File(filePathString));
			//�½�һ���߳̽����ļ�
			new Thread(){
				@Override
				public void run(){
					double starttime = System.currentTimeMillis();
					while(!ift.isDone()){
						if(ift.getStatus().equals(Status.error)){//�ļ����������
							System.out.println(sdf.format(new Date())+"error"+ift.getError());
						}
						else {
							double proccess = ift.getProgress();
							System.out.println("progress="+proccess+"%");
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println("used :"+(System.currentTimeMillis()-starttime)/1000+" second");
				}
			}.start();;
			
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
