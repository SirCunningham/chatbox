package chatbox;

public class xmlTest {

    public static void main(String[] args) {
        String html = "<message sender=asd><encrypted type=\"asd key=asd></encrypted></message>";
        System.out.println(html.matches("<message sender=(.*)>(.*)<encrypted type=(.*) "
                + "key=(.*)>(.*)</encrypted>(.*)</message>"));
    }

    public static String removeKeyRequest(String xmlStr) {
        return xmlStr.replaceAll("<keyrequest.*>.*</keyrequest>", "");
    }
}