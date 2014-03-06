package chatbox;

public class Test2 implements Runnable {
    
    private String host;
    private int port;
    private MessageBox messageBox;
    
    public Test2(String host, int port, MessageBox messageBox) {
        this.host = host;
        this.port = port;
        this.messageBox = messageBox;
    }
    
    public void run() {
        new Server(port, messageBox);
        new Client(host, port, messageBox);
    }

}