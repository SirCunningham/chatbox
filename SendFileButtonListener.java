package chatbox;

import java.awt.event.*;
import java.util.List;
import javax.swing.text.*;

// Skicka fil med klient
public class SendFileButtonListener implements ActionListener {

    public final ChatRoom chatRoom;

    public SendFileButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String description = chatRoom.getDescriptionPane().getText();
        if ("File description (optional)".equals(description)) {
            description = "No description";
        }
        String fileData = String.format("File name: %s\nFile size: %s\n"
                + "File description: %s\nAccept file?", chatRoom.getFilePane().getText(),
                chatRoom.getFileSizePane().getText(), description);
        String message = chatRoom.getMessagePane().getText();
        List<String> names = chatRoom.getList().getSelectedValuesList();
        
        System.out.println(names);
        chatRoom.o.println(String.format("<message sender=\"%s\"><text color=\"%s\"><fileUsers users=\"%s\"></fileUsers></text></message>", chatRoom.getName(), chatRoom.color,names));
        chatRoom.appendToPane(String.format("<message sender=\"%s\"><filerequest name=\"%s\" size=\"%s\">%s</filerequest></message>",
                chatRoom.getNamePane().getText(), chatRoom.getFilePane().getText(), chatRoom.getFileSizePane().getText(), description));
        try {
            chatRoom.doc.remove(0, message.length());
            chatRoom.doc.insertString(0, "I want to send this file: " + fileData, chatRoom.style);
            chatRoom.getSendButton().doClick();
            chatRoom.doc.insertString(0, message, chatRoom.style);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        /*
        try {
        FileSender thr = new FileSender(chatCreator.hostPane.getText(),
        Integer.parseInt(chatCreator.portPane.getText()),
        filePane.getText());
        thr.start();
        } catch (Exception ex) {
        System.err.println("Ett fel inträffade2: " + ex);
        }
         */
    }
}