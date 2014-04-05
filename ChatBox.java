package chatbox;

//Dialogmeddelande för utsparkning, men ej avslutning av programmet!
//Color switch does not work when encrypted, sends wrong message!!!
//Scroll does not always work - and lags!
//Block client after disconnect - no new posts possible!
//Skicka <disconnect> med servern till en klient som blir kickad?

public class ChatBox {

    public static void main(String[] args) {
        new Controller(new ChatCreator());
    }
}   
