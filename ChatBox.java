package chatbox;

//Dialogmeddelande för utsparkning, men ej avslutning av programmet!
//Color switch does not work when encrypted, sends wrong message!!!
//Scroll does not always work - and lags!
//Skicka <disconnect> med servern till en klient som blir kickad, skriv "chatRoom.disableChat();"!?

//Problems: tab switching does not work without server, no left end, no focus when click on tab

public class ChatBox {

    public static void main(String[] args) {
        new Controller(new ChatCreator());
    }
}