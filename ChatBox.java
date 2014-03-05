package chatbox;

//Fel namn vid avslutning (sitt eget istället för annans)!
//Red color does not work! Scroll does not work!
//Block client after disconnect, and fix controller so that client doesn't start first!
public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}