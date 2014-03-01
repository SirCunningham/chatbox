package chatbox;

//Scrollen fungerar ej - ibland!
//Begränsa tabbarnas tjocklek!
//Tabbar funkar ej - samma bug som tidigare!
//Enter + messageField.isFocused() = send!
//Checkbox för att skicka nyckel!
public class ChatBox {

    public static void main(String[] args) {
        new Controller(new View());
    }
}
