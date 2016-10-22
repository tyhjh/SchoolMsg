package api;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class File2Bytes {
	
    /** 
     * �ļ�ת�ֽ� 
     *  
     * @param file 
     * @return 
     * @throws IOException 
     */  
    public static byte[] getFileBytes(File file) throws IOException {  
        BufferedInputStream bis = null;  
        try {  
            bis = new BufferedInputStream(new FileInputStream(file));  
            int bytes = (int) file.length();  
            byte[] buffer = new byte[bytes];  
            int readBytes = bis.read(buffer);  
            if (readBytes != buffer.length) {  
                throw new IOException("Entire file not read");  
            }  
            return buffer;  
        } finally {  
            if (bis != null) {  
                bis.close();  
            }  
        }  
    }  
}
