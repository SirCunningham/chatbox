package chatbox;

public class ClientAdapter extends Client {

    public ClientAdapter(ChatRoom chatRoom) {
        super(chatRoom.host, chatRoom.port, chatRoom);
        chatRoom.o = this.o;
    }
}