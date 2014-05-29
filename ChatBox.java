package chatbox;

//StartButtonListener: item-listan uppdateras bara ibland, vem skrev detta??
//Privata ELLER statiska variabler överallt - inga publika fält, tack!!

//Utsparkning: Skicka <disconnect> med servern till en klient som blir kickad, skriv "chatRoom.disableChat();" i kod!?
//Scroll does not always work - and lags! http://tips4java.wordpress.com/2008/10/22/text-area-scrolling/
//Problems: tab switching does not work without server, no left end, no focus when click on tab
//Trådbugg: ibland får nya klienter en tom rad vid intro
//Återskapad bugg: server dör för sent, påverkar nyskapade tabbar
//Bugg: sendknappen låser sig ibland, antagligen samma fel som ovan, utloggningsmeddelanden missas också ibland
//Lösning: adapter för server (enkel) och klient (med fälten i och o, som läggs till i ChatRoom)!?

//Skriv IPv6-adress, då ser man att programmet ej är multitrådat - gammal implementation bättre!

//Mellanrum skapas efter krypterad text
//Ingen tom rad efter nekad keyrequest!

//Kolla om man verkligen byter färg
//Unikt namn!

//Småfixar återstår för LeftPanel/RightPanel/ChatRoom...

import java.awt.event.*;

public class ChatBox {

    public ChatBox() {
        ChatCreator.frame.addWindowListener(new ExitListener());
        ChatCreator.hostPane.addFocusListener(new TextFieldListener());
        ChatCreator.hostPane.addKeyListener(new CreatorListener());
        ChatCreator.portPane.addFocusListener(new TextFieldListener());
        ChatCreator.portPane.addKeyListener(new CreatorListener());
        ChatCreator.passPane.addFocusListener(new TextFieldListener());
        ChatCreator.passPane.addKeyListener(new CreatorListener());
        ChatCreator.requestPane.addFocusListener(new TextFieldListener());
        ChatCreator.requestPane.addKeyListener(new CreatorListener());
        ChatCreator.namePane.addFocusListener(new TextFieldListener());
        ChatCreator.namePane.addKeyListener(new CreatorListener());
        ChatCreator.tabPane.addFocusListener(new TextFieldListener());
        ChatCreator.tabPane.addKeyListener(new CreatorListener());
        ChatCreator.startButton.addActionListener(new StartButtonListener());
        ChatCreator.startButton.addKeyListener(new CreatorListener());
        ChatCreator.clientButton.addKeyListener(new CreatorListener());
        ChatCreator.serverButton.addItemListener(new ServerButtonListener());
        ChatCreator.serverButton.addKeyListener(new CreatorListener());
        ChatCreator.serverOptions.addItemListener(new ServerOptionsListener());
        ChatCreator.closeButton.addActionListener(new CloseButtonListener());
        ChatCreator.closeButton.addKeyListener(new CreatorListener());
    }

    public static void main(String[] args) {
        new ChatBox();
    }

    public class ServerButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ChatCreator.startButton.setText("Create server");
                ChatCreator.hostLabel.setEnabled(false);
                ChatCreator.hostPane.setEnabled(false);
                ChatCreator.host = ChatCreator.hostPane.getText();
                ChatCreator.hostPane.setText("127.0.0.1");
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                ChatCreator.startButton.setText("Join server");
                ChatCreator.hostLabel.setEnabled(true);
                ChatCreator.hostPane.setEnabled(true);
                ChatCreator.hostPane.setText(ChatCreator.host);
            }
        }
    }

    public class ServerOptionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(
                    ChatCreator.serverOptions.getSelectedItem());
            if ("Protected".equals(chosen) || "Secret".equals(chosen)) {
                ChatCreator.passLabel.setVisible(true);
                ChatCreator.passPane.setVisible(true);
            } else {
                ChatCreator.passLabel.setVisible(false);
                ChatCreator.passPane.setVisible(false);
            }
            if ("Private".equals(chosen) || "Secret".equals(chosen)) {
                ChatCreator.requestLabel.setVisible(true);
                ChatCreator.requestPane.setVisible(true);
            } else {
                ChatCreator.requestLabel.setVisible(false);
                ChatCreator.requestPane.setVisible(false);
            }
        }
    }
}