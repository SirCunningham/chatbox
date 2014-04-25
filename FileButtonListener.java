package chatbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;

public class FileButtonListener implements ActionListener {

    public final ChatRoom chatRoom;

    public FileButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(ChatCreator.frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            chatRoom.filePath = file.getAbsolutePath();
            chatRoom.filePane.setText(file.getName());
            chatRoom.fileSizePane.setText(Long.toString(file.length()) + " bytes");
            chatRoom.sendFileButton.setEnabled(true);
        }
    }
}
