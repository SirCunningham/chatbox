package chatbox;

//Utsparkning: Skicka <disconnect> med servern till en klient som blir kickad, skriv "chatRoom.disableChat();" i kod!?
//Color switch does not work when encrypted, sends wrong message!!!
//Scroll does not always work - and lags!

//Problems: tab switching does not work without server, no left end, no focus when click on tab
//Trådbugg: ibland får nya klienter en tom rad vid intro
//Återskapad bugg: server dör för sent, påverkar nyskapade tabbar
//Bugg: sendknappen låser sig ibland, antagligen samma fel som ovan, utloggningsmeddelanden missas också ibland

//Write fake host, then a new server cannot be created after that! Is host info meaningful for server??

public class ChatBox {

    public static void main(String[] args) {
        new Controller(new ChatCreator());
    }
}