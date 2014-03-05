package chatbox;

//Begränsa tabbarnas tjocklek!
//Färg och backgrund på messagePane funkar inte alltid!
//Kan inte skicka HTML-kod (t.ex. "&gt;")!
//Lock for no name; scroll when error!!!

//Enter vid start
//Lista för klienter!
public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}