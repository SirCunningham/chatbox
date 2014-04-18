package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;

    class ProgressBarButtonListener implements ActionListener {
        
        private final ChatRoom chatRoom;

        public ProgressBarButtonListener(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final JProgressBar progressBar;
                    final JPanel invisibleContainer;
                    final JLabel label;
                    final JOptionPane optionPane;
                    final JDialog dialog;
                    final SwingWorker worker;

                    progressBar = new JProgressBar(0, 100);
                    progressBar.setValue(0);
                    progressBar.setStringPainted(true);
                    progressBar.setVisible(false);

                    invisibleContainer = new JPanel(new GridLayout(2, 1));
                    label = new JLabel("Downloading...");
                    label.setBackground(invisibleContainer.getBackground());
                    label.setVisible(false);
                    invisibleContainer.add(label, BorderLayout.PAGE_START);
                    invisibleContainer.add(progressBar, BorderLayout.CENTER);

                    String description = chatRoom.descriptionPane.getText();
                    if (description.equals("File description (optional)")) {
                        description = "No description";
                    }
                    String fileData = String.format("File name: %s\nFile size: %s\n"
                            + "File description: %s\nAccept file?", chatRoom.filePane.getText(),
                            chatRoom.fileSizePane.getText(), description);

                    optionPane = new JOptionPane(fileData, JOptionPane.QUESTION_MESSAGE,
                            JOptionPane.YES_NO_OPTION);
                    dialog = new JDialog(chatRoom.chatCreator.frame, "File request", false);
                    worker = new SwingWorker<Object, Object>() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            Random generator = new Random();
                            int progress = 0;
                            setProgress(progress);
                            while (progress < 100) {
                                try {
                                    Thread.sleep(generator.nextInt(100));
                                } catch (InterruptedException e) {
                                }
                                progress += generator.nextInt(10);
                                setProgress(Math.min(progress, 100));
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            dialog.setCursor(null);
                            dialog.dispose();
                        }
                    };
                    optionPane.addPropertyChangeListener(
                            new PropertyChangeListener() {

                                @Override
                                public void propertyChange(PropertyChangeEvent e) {
                                    String name = e.getPropertyName();
                                    if (e.getSource() == optionPane
                                    && name.equals(JOptionPane.VALUE_PROPERTY)) {
                                        int reply = (int) optionPane.getValue();
                                        if (reply == JOptionPane.YES_OPTION) {
                                            progressBar.setVisible(true);
                                            label.setVisible(true);
                                            dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                            worker.execute();
                                        } else {
                                            dialog.dispose();
                                        }
                                    }
                                }
                            });

                    dialog.setContentPane(optionPane);
                    dialog.getContentPane().add(invisibleContainer);
                    dialog.pack();
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setLocationRelativeTo(chatRoom.chatCreator.frame);
                    dialog.setResizable(false);
                    dialog.setAlwaysOnTop(false);
                    dialog.setVisible(true);

                    worker.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent e) {
                            String name = e.getPropertyName();
                            if (name.equals("progress")) {
                                SwingWorker worker1 = (SwingWorker) e.getSource();
                                int progress = worker1.getProgress();
                                if (progress == 0) {
                                    progressBar.setIndeterminate(true);
                                } else {
                                    progressBar.setIndeterminate(false);
                                    progressBar.setValue(progress);
                                }
                            }
                        }
                    });
                }
            });
        }
    }