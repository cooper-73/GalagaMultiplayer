package com.galaga_client;

import com.galaga_game.Galaga;

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
    GalagaGUI thisGalagaGUI = this;
    Galaga galaga = null;
    Galaga galagaClient = null;
    Galaga galagaServer = null;
    public boolean gameStarted = false;
    public int keyCode;
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

    public GalagaGUI() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyCode = e.getKeyCode();
                System.out.println(e.getKeyCode());
                if(galagaClient != null) {
                    galagaClient.client.sendData("0 " + e.getKeyCode());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        setTitle("Galaga");
        setPreferredSize(new Dimension(800, 600));
        setSize(800, 600);
        setResizable(false);
        setLayout(new BorderLayout(20, 20));

        setConnectionPanel();
        setBoardPanel();

        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Galaga("server")).start();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                galagaClient = new Galaga("client", thisGalagaGUI);
                galagaClient.start();
                waitRoomMaster();
            }
        });

        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                galagaClient = new Galaga("client", thisGalagaGUI);
                galagaClient.start();
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
                setAllFocusable(true);
                homeRoom();
            }
        });

        closeServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));
        //Set Board Styles
        Font font = new Font("Noto Mono", Font.BOLD, 24);
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

        boardTextPane.setText("-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n");
        boardTextPane.setEditable(false);
        messageTextPane.setText("-----------------------------------------\n" +
                "-----------------------------------------\n" +
                "-----------------------------------------\n");
        messageTextPane.setEditable(false);

        boardPanel.add(boardTextPane);
        boardPanel.add(messageTextPane);
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
    }

    public void waitRoomMaster() {
        ipAddresTextField.setEditable(false);
        portTextField.setEditable(false);
        startPanel.removeAll();
        startPanel.add(startGameButton);
        startPanel.add(closeServerButton);
        startPanel.repaint();
        startPanel.revalidate();
    }

    public void waitRoomSlave() {
        ipAddresTextField.setEditable(false);
        portTextField.setEditable(false);
        startPanel.removeAll();
        startPanel.add(exitGameButton);
        startPanel.repaint();
        startPanel.revalidate();
        setAllFocusable(false);
        this.requestFocus();
    }

    public void startGame() {
        setAllFocusable(false);
        this.requestFocus();
        //new Thread(new Galaga("client", this)).start();
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


