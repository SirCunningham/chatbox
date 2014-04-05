package chatbox;

public class xmlTest {
    public static void main(String[] args) {
        String test = "<message><keyrequest></keyrequest></message>";
        System.out.println(removeKeyRequest(test));
    }
    public static String removeKeyRequest(String xmlStr) {
        return xmlStr.replaceAll("<keyrequest.*>.*</keyrequest>", "");
    }

}