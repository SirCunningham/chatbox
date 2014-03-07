package chatbox;

//Fel namn vid avslutning (sitt eget istället för annans)!
//Color switch does not work when encrypted!!! Scroll does not always work - and lags!
//Block client after disconnect (no new posts), and fix controller so that client can't start first!
public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}