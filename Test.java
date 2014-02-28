
package chatbox;

public class Test {
    public static void main(String[] args) {
        String test = encryptCaesar("men det är detta", 5);
        String test2 = encryptCaesar("och även detta", 10);
        System.out.println(test2);
        String xmlTest = "<message sender=\"dante\"> <text color=\"asdfasf\"> Detta är inte krypterat "
                + "<encrypted type=\"caesar\" key=\"5\">"+test+"</encrypted>"
                + "<encrypted type=\"caesar\" key=\"10\">"+test2+"</encrypted></text></message>";
        String msg="";
        for (int i=0; i<xmlTest.length();i++) {
            if (i==xmlTest.indexOf("<encrypted")) {
                msg+=xmlTest.substring(0,i);
                xmlTest = xmlTest.substring(i + 10);
                String temp = xmlTest.substring(xmlTest.indexOf("\"")+1);
                String type = temp.substring(0,temp.indexOf("\""));
                String key = temp.substring(temp.indexOf("key")+5,
                        temp.indexOf(">")-1);
                String encryptedMsg=temp.substring(temp.indexOf(">")+1, temp.indexOf("</encrypted>"));
                if (type.equals("caesar")) {
                    msg += decryptCaesar(encryptedMsg, Integer.valueOf(key));
                }
                System.out.println(type);
                System.out.println(key);
                xmlTest=" "+xmlTest.substring(xmlTest.indexOf("</encrypted>")+12);
                i=0;
            }
        }

        System.out.println(msg);
    }
    

    public String handleString(String xmlMsg) {
        String msg = "";
        for (int i = 0; i < xmlMsg.length(); i++) {
            if (i == xmlMsg.indexOf("<encrypted")) {
                msg += xmlMsg.substring(0, i);
                xmlMsg = xmlMsg.substring(i + 10);
                String temp = xmlMsg.substring(xmlMsg.indexOf("\"") + 1);
                String type = temp.substring(0, temp.indexOf("\""));
                String key = temp.substring(temp.indexOf("key") + 5,
                        temp.indexOf(">") - 1);
                String encryptedMsg = temp.substring(temp.indexOf(">") + 1, temp.indexOf("</encrypted>"));
                if (type.equals("caesar")) {
                    msg += decryptCaesar(encryptedMsg, Integer.valueOf(key));
                }
                System.out.println(type);
                System.out.println(key);
                xmlMsg = " " + xmlMsg.substring(xmlMsg.indexOf("</encrypted>") + 12);
                i = 0;
            }
        }
        msg += xmlMsg;
        return msg;
    }

    public static String encryptCaesar(String text, int shift) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            //Tag inte med control characters
            if (c >= 32 && c <= 127) {
                int x = c - 32;
                x = (x + shift) % 96;
                if (x < 0) {
                    x += 96;
                }
                chars[i] = (char) (x + 32);
            }
        }
        return new String(chars);
    }
    public static String decryptCaesar(String text, int shift) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            if (c >= 32 && c <= 127) {
                int x = c - 32;
                x = (x - shift) % 96;
                if (x < 0) {
                    x += 96;
                }
                chars[i] = (char) (x + 32);
            }
        }
        return new String(chars);
    }
}
