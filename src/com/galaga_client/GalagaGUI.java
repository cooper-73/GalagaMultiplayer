package com.galaga_client;

import com.galaga_game.Galaga;
import com.galaga_server.GalagaServer;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GalagaGUI extends JFrame {
    Galaga galaga = null;

    JPanel connectionPanel = new JPanel();
    JLabel ipAddressLabel = new JLabel("IP: ");
    JTextField ipAddresTextField = new JTextField();
    JLabel portLabel = new JLabel("PORT: ");
    JTextField portTextField = new JTextField();
    JPanel startPanel = new JPanel();
    JButton createServerButton = new JButton("Create Server");
    JButton joinGameButton = new JButton("Join Game");
    JButton startGameButton = new JButton("Start");
    JButton exitGameButton = new JButton("Exit");
    JButton closeServerButton = new JButton("Close Server");
    JPanel boardPanel = new JPanel();
    JTextPane boardTextPane = new JTextPane();//720, 540
    JTextPane messageTextPane = new JTextPane();

    public GalagaGUI(Galaga galaga) {
        this.galaga = galaga;
        //Send the keyCode of the key pressed
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (galaga != null && galaga.tcpClient != null && galaga.status == 1) galaga.tcpClient.sendMessage("key " + galaga.id + " " + keyCode);
            }
        });

        setTitle("Galaga");
        setPreferredSize(new Dimension(800, 700));
        setSize(800, 700);
        //setResizable(false);
        setLayout(new BorderLayout(20, 20));

        setConnectionPanel();
        setBoardPanel();

        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String IP = ipAddresTextField.getText();
                int PORT = Integer.parseInt(portTextField.getText());
                try {
                    new Thread(new GalagaServer(PORT)).start();
                    Thread.sleep(50);   //Delay entre crear el servidor y conectarse a el
                    galaga.startClient(IP, PORT);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                waitRoomMaster();
            }
        });

        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String IP = ipAddresTextField.getText();
                int PORT = Integer.parseInt(portTextField.getText());
                try {
                    galaga.startClient(IP, PORT);
                    Thread.sleep(20);//
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                waitRoomSlave();
            }
        });

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        exitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                galaga.tcpClient.sendMessage("leave " + galaga.id);
                setAllFocusable(true);
                homeRoom();
            }
        });

        closeServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                galaga.tcpClient.stopClient();
                homeRoom();
            }
        });

        startPanel.add(createServerButton);
        startPanel.add(joinGameButton);

        this.getContentPane().add(connectionPanel, BorderLayout.PAGE_START);
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);
        this.getContentPane().add(startPanel, BorderLayout.PAGE_END);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setConnectionPanel() {
        Font font = new Font("Arial", Font.BOLD, 16);
        ipAddresTextField.setPreferredSize(new Dimension(170, 30));
        ipAddresTextField.setFont(font);
        ipAddresTextField.setHorizontalAlignment(JTextField.CENTER);
        portTextField.setPreferredSize(new Dimension(60, 30));
        portTextField.setFont(font);
        portTextField.setHorizontalAlignment(JTextField.CENTER);
        connectionPanel.add(ipAddressLabel);
        connectionPanel.add(ipAddresTextField);
        connectionPanel.add(portLabel);
        connectionPanel.add(portTextField);
    }

    public void setBoardPanel() {
        boardPanel.removeAll();
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));
        //Set Board Styles
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 24);
        boardTextPane.setFont(font);
        boardTextPane.setForeground(Color.GREEN);
        boardTextPane.setBackground(Color.BLACK);
        StyledDocument docBoard = boardTextPane.getStyledDocument();
        SimpleAttributeSet centerBoard = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerBoard, StyleConstants.ALIGN_CENTER);
        docBoard.setParagraphAttributes(0, docBoard.getLength(), centerBoard, false);
        messageTextPane.setFont(font);
        messageTextPane.setForeground(Color.CYAN);
        messageTextPane.setBackground(Color.BLACK);
        StyledDocument docMessage = messageTextPane.getStyledDocument();
        SimpleAttributeSet centerMessage = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerMessage, StyleConstants.ALIGN_CENTER);
        docMessage.setParagraphAttributes(0, docMessage.getLength(), centerMessage, false);

        boardTextPane.setText("                    $$\\                               \n" +
                "                    $$ |                              \n" +
                " $$$$$$\\   $$$$$$\\  $$ | $$$$$$\\   $$$$$$\\   $$$$$$\\  \n" +
                "$$  __$$\\  \\____$$\\ $$ | \\____$$\\ $$  __$$\\  \\____$$\\ \n" +
                "$$ /  $$ | $$$$$$$ |$$ | $$$$$$$ |$$ /  $$ | $$$$$$$ |\n" +
                "$$ |  $$ |$$  __$$ |$$ |$$  __$$ |$$ |  $$ |$$  __$$ |\n" +
                "\\$$$$$$$ |\\$$$$$$$ |$$ |\\$$$$$$$ |\\$$$$$$$ |\\$$$$$$$ |\n" +
                " \\____$$ | \\_______|\\__| \\_______| \\____$$ | \\_______|\n" +
                "$$\\   $$ |                        $$\\   $$ |          \n" +
                "\\$$$$$$  |                        \\$$$$$$  |          \n" +
                " \\______/                          \\______/           \n");

        messageTextPane.setText("================ Members ================\n" +
                "|      Cerna Espiritu Roberto Alexis    |\n" +
                "|        Cruz Coro Cristhian Elian      |\n" +
                "|      Violeta Estrella Piero Alexis    |");

        boardTextPane.setEditable(false);
        messageTextPane.setEditable(false);
        boardPanel.add(boardTextPane);
        boardPanel.add(messageTextPane);
        boardPanel.repaint();
        boardPanel.revalidate();
    }

    public void setBoardText(String text) {
        boardTextPane.setText(text);
        boardPanel.repaint();
        boardPanel.revalidate();
    }

    public void setMessageText(String text) {
        messageTextPane.setText(text);
        boardPanel.repaint();
        boardPanel.revalidate();
    }

    public void homeRoom() {
        ipAddresTextField.setEditable(true);
        portTextField.setEditable(true);
        startPanel.removeAll();
        startPanel.add(createServerButton);
        startPanel.add(joinGameButton);
        startPanel.repaint();
        startPanel.revalidate();
        boardPanel.removeAll();
        setBoardPanel();
    }

    public void waitRoomMaster() {
        ipAddresTextField.setEditable(false);
        portTextField.setEditable(false);
        startPanel.removeAll();
        startPanel.add(startGameButton);
        startPanel.add(closeServerButton);
        startPanel.repaint();
        startPanel.revalidate();
        boardPanel.removeAll();
        boardPanel.add(messageTextPane);
        boardPanel.add(boardTextPane);
        boardPanel.repaint();
        boardPanel.revalidate();
        setMessageText("|   A - move left   |  K - pew pew pew  |\n" +
                "|   D - move right  |  Q - quit game    |\n" +
                "============= Instructions ==============");
    }

    public void waitRoomSlave() {
        ipAddresTextField.setEditable(false);
        portTextField.setEditable(false);
        startPanel.removeAll();
        startPanel.add(exitGameButton);
        startPanel.repaint();
        startPanel.revalidate();
        boardPanel.removeAll();
        boardPanel.add(messageTextPane);
        boardPanel.add(boardTextPane);
        boardPanel.repaint();
        boardPanel.revalidate();
        setMessageText("|   A - move left   |  K - pew pew pew  |\n" +
                "|   D - move right  |  Q - quit game    |\n" +
                "============= Instructions ==============");
        setAllFocusable(false);
        this.requestFocus();
    }

    public void startGame() {
        startPanel.remove(startGameButton);
        startPanel.repaint();
        startPanel.revalidate();
        if(galaga.tcpClient != null)    galaga.tcpClient.sendMessage("start");
        setAllFocusable(false);
        this.requestFocus();
    }

    public void setAllFocusable(boolean flag) {
        connectionPanel.setFocusable(flag);
        ipAddressLabel.setFocusable(flag);
        ipAddresTextField.setFocusable(flag);
        portLabel.setFocusable(flag);
        portTextField.setFocusable(flag);
        startPanel.setFocusable(flag);
        createServerButton.setFocusable(flag);
        joinGameButton.setFocusable(flag);
        startGameButton.setFocusable(flag);
        exitGameButton.setFocusable(flag);
        closeServerButton.setFocusable(flag);
        boardPanel.setFocusable(flag);
        boardTextPane.setFocusable(flag);
        messageTextPane.setFocusable(flag);
    }
}


