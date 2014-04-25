package chatbox;

public class Messages {

    public static String getFileMessage(ChatRoom chatRoom) {
        String description = chatRoom.descriptionPane.getText();
        if (description.equals("File description (optional)")) {
            description = "No description";
        }
        String fileData = String.format("File name: %s\nFile size: %s\n"
                + "File description: %s\nAccept file?", chatRoom.filePane.getText(),
                chatRoom.fileSizePane.getText(), description);
        String message = chatRoom.messagePane.getText();
        /*
        appendToPane(String.format("<message sender=\"%s\"><filerequest namn=\"%s\" size=\"%s\">%s</filerequest></message>",
        namePane.getText(), filePane.getText(), fileSizePane.getText(), description));
         * 
         */
        return String.format("<message sender=\"%s\"><filerequest namn=\"%s\" size=\"%s\">%s</filerequest></message>",
                chatRoom.namePane.getText(), chatRoom.filePane.getText(), chatRoom.fileSizePane.getText(), description);
    }

    public static String getQuitMessage(ChatRoom chatRoom) {
        return String.format("<message sender=\"%s\"><text color=\"%s\">I just left.</text><disconnect /></message>", chatRoom.namePane.getText(), chatRoom.color);
    }

    public static String getMessage(ChatRoom chatRoom) {
        try {
            String message;
            if (chatRoom.cipherButton.isSelected() && !chatRoom.statusUpdate) {
                message = chatRoom.cipherMessage;
                chatRoom.cipherButton.doClick();
            } else {
                message = XMLString.convertAngle(chatRoom.messagePane.getText());
            }
            chatRoom.messagePane.setText("");
            String name = chatRoom.namePane.getText();
            if (!message.isEmpty()) {
                if (!chatRoom.keyRequestBox.isSelected()) {
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
                        String.valueOf(chatRoom.cipherBox.getSelectedItem()), message);
                /*
                timer.setType(String.valueOf(chatRoom.cipherBox.getSelectedItem()));
                timer.start();
                 * 
                 */


                /*
                appendToPane(String.format("<message sender=\"%s\">"
                + "<text color=\"%s\">%s</text></message>",
                name, chatRoom.color,
                message));
                 * 
                 */
            }
            /*
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
