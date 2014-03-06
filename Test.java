package chatbox;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Test {

    private void initUI() {
        JFrame frame = new JFrame(Test.class.getSimpleName());
        frame.setLayout(new FlowLayout());
        final JTextField textfield = new JTextField(20);
        textfield.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = textfield.getText();
                    InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
                    System.err.println(text);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(textfield);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Test().initUI();
            }
        });
    }
}