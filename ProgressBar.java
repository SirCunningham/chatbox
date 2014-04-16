package chatbox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ProgressBar extends JFrame {

    // beta status
    // tutorial: http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
    JProgressBar current = new JProgressBar(0, 100);
    int num = 0;

    public ProgressBar() {
        //exit button, read more: http://stackoverflow.com/questions/6084039/create-custom-operation-for-setdefaultcloseoperation
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //create the panel to add the details
        JPanel pane = new JPanel();
        current.setValue(0);
        current.setStringPainted(true);
        pane.add(current);
        setContentPane(pane);
    }

    //to iterate so that it looks like progress bar  
    public void iterate() {
        SwingWorker worker;
        worker = new SwingWorker<Object, Object>() {
            
            @Override
            protected Object doInBackground() throws Exception {
                while (num < 2000) {
                    //current.setValue(num);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                    }
                    num += 95;
                    int p = Math.round(((float) Math.min(num, 2000) / 2000f) * 100f);
                    setProgress(p);
                }
                return null;
            }
        };
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                String name = e.getPropertyName();
                if ("progress".equals(name)) {
                    SwingWorker worker = (SwingWorker) e.getSource();
                    current.setValue(worker.getProgress());
                }
            }
        });
        worker.execute();
    }
}