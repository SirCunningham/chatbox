package chatbox;

import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser;

public class FileButtonListener implements ActionListener {

    public final ChatRoom chatRoom;

    public FileButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(ChatCreator.frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            chatRoom.filePath = file.getAbsolutePath();
            chatRoom.getFilePane().setText(file.getName());
            //kan finnas bättre sätt att konvertera long till int!!
            chatRoom.getSendFileButton().setEnabled(true);
        }
    }
}