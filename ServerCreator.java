package chatbox;

public class ServerCreator implements Runnable {

    private final ChatRoom chatRoom;

    public ServerCreator(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        final Server server = new Server(chatRoom.port, chatRoom);
        new Thread(server).start();

        final int port;
        if (chatRoom.port < 65523) {
            port = chatRoom.port + 13;
        } else {
            port = chatRoom.port - 23;
        }
        final Server fileServer = new Server(port, chatRoom);
        new Thread(fileServer).start();
    }
}