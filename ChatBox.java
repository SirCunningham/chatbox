package chatbox;

//Fel namn vid avslutning (sitt eget istället för annans)!
//Color switch does not work when encrypted!!! Scroll does not always work - and lags!
//Block client after disconnect - no new posts possible!
//Skicka <disconnect> med servern till en klient som blir kickad?
//Write which port it is, write a "waiting" message for server; others will not be alone!
//Enter for socket creation!

public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}