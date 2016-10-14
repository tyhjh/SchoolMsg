package publicinfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Tyhj on 2016/10/14.
 */

public class GetChatMsg implements Serializable {
    private List<Msg_chat> msg_chats;
    private String id;

    public GetChatMsg(List<Msg_chat> msg_chats, String id) {
        this.msg_chats = msg_chats;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Msg_chat> getMsg_chats() {
        return msg_chats;
    }

    public void setMsg_chats(List<Msg_chat> msg_chats) {
        this.msg_chats = msg_chats;
    }

}
