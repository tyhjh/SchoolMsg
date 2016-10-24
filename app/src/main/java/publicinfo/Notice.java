package publicinfo;

import java.io.Serializable;

/**
 * Created by Tyhj on 2016/10/24.
 */

public class Notice implements Serializable {
    String name;
    String id;
    String signature;
    int time;
    byte[] head;
    int type;
    int status;



    public Notice(String name, String id, String signature, int time, byte[] head, int type,int status) {
        this.name = name;
        this.id = id;
        this.signature = signature;
        this.time = time;
        this.head = head;
        this.type = type;
        this.status=status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public byte[] getHead() {
        return head;
    }

    public void setHead(byte[] head) {
        this.head = head;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
