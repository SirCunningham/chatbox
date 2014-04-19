package chatbox;

//Utsparkning: Skicka <disconnect> med servern till en klient som blir kickad, skriv "chatRoom.disableChat();" i kod!?
//Color switch does not work when encrypted, sends wrong message!!!
//Scroll does not always work - and lags!

//Problems: tab switching does not work without server, no left end, no focus when click on tab
//Trådbugg: ibland får nya klienter en tom rad vid intro
//Återskapad bugg: server dör för sent, påverkar nyskapade tabbar
//Bugg: sendknappen låser sig ibland, antagligen samma fel som ovan, utloggningsmeddelanden missas också ibland
//Lösning: adapter??

//Skriv IPv6-adress, då ser man att programmet ej är multitrådat - gammal implementation bättre!
//Kolla portars tal! Kan inte skriva a eller d!!

public class ChatBox {

    public static void main(String[] args) {
        new Controller();
    }
}