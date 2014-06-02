package chatbox;

public class Messages {

    public static String getFileMessage(ChatRoom chatRoom) {
        String description = chatRoom.getDescriptionPane().getText();
        if (description.equals("File description (optional)")) {
            description = "No description";
        }
        String fileData = String.format("File name: %s\nFile size: %s\n"
                + "File description: %s\nAccept file?", chatRoom.getFilePane().getText(),
                chatRoom.getFileSizePane().getText(), description);
        String message = chatRoom.getMessagePane().getText();
        /*
        appendToPane(String.format("<message sender=\"%s\"><filerequest name=\"%s\" size=\"%s\">%s</filerequest></message>",
        namePane.getText(), filePane.getText(), fileSizePane.getText(), description));
         * 
         */
        return String.format("<message sender=\"%s\"><filerequest name=\"%s\" size=\"%s\">%s</filerequest></message>",
                chatRoom.getNamePane().getText(), chatRoom.getFilePane().getText(), chatRoom.getFileSizePane().getText(), description);
    }

    public static String getQuitMessage(ChatRoom chatRoom) {
        return String.format("<message sender=\"%s\"><text color=\"%s\">I just left.</text><disconnect /></message>", chatRoom.getNamePane().getText(), chatRoom.color);
    }

    public static String getMessage(ChatRoom chatRoom) {
        try {
            String message;
            if (chatRoom.getCipherButton().isSelected() && !chatRoom.statusUpdate) {
                message = chatRoom.cipherMessage;
                chatRoom.getCipherButton().doClick();
            } else {
                message = XMLString.convertAngle(chatRoom.getMessagePane().getText());
            }
            chatRoom.getMessagePane().setText("");
            //String name = chatRoom.getNamePane().getText();
            return message;
            /*
            if (!message.isEmpty()) {
            if (!chatRoom.getKeyRequestBox().isSelected()) {
            return String.format("<message sender=\"%s\">"
            + "<text color=\"%s\">%s</text></message>",
            name, chatRoom.color,
            message);
            }
            return String.format("<message sender=\"%s\">"
            + "<text color=\"%s\"><keyrequest "
            + "type=\"%s\">%s"
            + "</keyrequest></text></message>",
            name, chatRoom.color,
            String.valueOf(chatRoom.getCipherBox().getSelectedItem()), message);
            
            timer.setType(String.valueOf(chatRoom.getCipherBox().getSelectedItem()));
            timer.start();
             * 
             */


            /*
            appendToPane(String.format("<message sender=\"%s\">"
            + "<text color=\"%s\">%s</text></message>",
            name, chatRoom.color,
            message));
             * 
            
            }
            
            if (message.contains("terminate my ass")) {
            appendToPane("<message sender=\"INFO\">"
            + "<text color=\"0000FF\">Med huvudet före!!!<disconnect /></text></message>");
            //kill();
            }
             * 
             */
        } catch (Exception ex) {
            chatRoom.appendToPane(String.format("<message sender=\"ERROR\">"
                    + "<text color=\"#ff0000\">Output stream failed</text></message>"));
        }
        System.out.println("Shouldn't be here!");
        return "";
    }
    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
}