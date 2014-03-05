package chatbox;

//Scrollen fungerar ej - ibland!
//Begränsa tabbarnas tjocklek!
//Färg och backgrund på messagePane funkar inte alltid!
//Kan inte skicka HTML-kod (t.ex. "&gt;") eller trasig XML (t.ex. "<")!
public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}
    