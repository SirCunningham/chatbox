package chatbox;

//Begränsa tabbarnas tjocklek!
//Färg och backgrund på messagePane funkar inte alltid!
//Lock for no name; scroll when error!!!
//Kan inte skriva texten "&nbsp;"
//Tomma namn fel!

//Enter vid start
//Lista för klienter!
public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}