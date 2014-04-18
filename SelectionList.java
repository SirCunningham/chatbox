package chatbox;

import javax.swing.*;

public class SelectionList extends JList {

    public SelectionList(ListModel items) {
        super(items);
        this.setSelectionModel(new DefaultListSelectionModel() {

            private static final long serialVersionUID = 1L;
            private boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (!gestureStarted) {
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1);
                    } else {
                        super.addSelectionInterval(index0, index1);
                    }
                }
                gestureStarted = true;
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (isAdjusting == false) {
                    gestureStarted = false;
                }
            }
        });
    }
}