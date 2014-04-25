package chatbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.BadLocationException;

// Skicka fil med klient
public class SendFileButtonListener implements ActionListener {

    public final ChatRoom chatRoom;

    public SendFileButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void actionPerformed(ActionEvent e) {
        String description = chatRoom.descriptionPane.getText();
        if ("File description (optional)".equals(description)) {
            description = "No description";
        }
        String fileData = String.format("File name: %s\nFile size: %s\n"
                + "File description: %s\nAccept file?", chatRoom.filePane.getText(),
                chatRoom.fileSizePane.getText(), description);
        String message = chatRoom.messagePane.getText();

        chatRoom.appendToPane(String.format("<message sender=\"%s\"><filerequest namn=\"%s\" size=\"%s\">%s</filerequest></message>",
                chatRoom.namePane.getText(), chatRoom.filePane.getText(), chatRoom.fileSizePane.getText(), description));
        try {
            chatRoom.doc.remove(0, message.length());
            chatRoom.doc.insertString(0, "Filerequest: " + fileData, chatRoom.style);
            chatRoom.sendButton.doClick();
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
