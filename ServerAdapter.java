package chatbox;

public class ServerAdapter extends Server {

    public ServerAdapter(ChatRoom chatRoom) {
        super(chatRoom.port, chatRoom);
    }
}