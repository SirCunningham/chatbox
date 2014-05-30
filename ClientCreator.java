package chatbox;

public class ClientCreator implements Runnable {
    
    private final ChatRoom chatRoom;

    public ClientCreator(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    @Override
    public void run() {
        final Client client = new Client(chatRoom.host, chatRoom.port, chatRoom);
        chatRoom.o = client.o;
        new Thread(client).start();
        
        final int port;
        if (chatRoom.port < 65523) {
            port = chatRoom.port + 13;
        } else {
            port = chatRoom.port - 23;
        }
        final FileClient fileClient = new FileClient(chatRoom.host, port, chatRoom);
        new Thread(fileClient).start();
    }
}