package com.galaga_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
    private GalagaGameStatus galagaGameStatus;
    private ServerSocket serverSocket;
    private int PORT = 4444;
    public int numberClients = 0;
    public TCPServerThread[] clients = new TCPServerThread[5]; // Max. number of players 4
    public int maxNumberClients = 4;
    private OnMessageReceived messageListener;

    public TCPServer(GalagaGameStatus galagaGameStatus, OnMessageReceived messageListener) throws IOException {
        this.galagaGameStatus = galagaGameStatus;
        this.messageListener = messageListener;
        this.serverSocket = new ServerSocket(PORT);
    }

    @Override
    public void run() {
        try {
            while (numberClients < maxNumberClients) {
                Socket client = serverSocket.accept();
                clients[numberClients] = new TCPServerThread(client, this, clients);
                new Thread(clients[numberClients]).start();
                Thread.sleep(20);
                replyToNewShipRequest();
                numberClients++;
                System.out.println("Server: Jugador " + numberClients + " conectado");
                System.out.println("Server: En total " + numberClients + " jugadores conectados");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Envia info a cada uno de los clientes enviar la pantalla del juego
    public void sendMessageTCPServer(String message) {
        for (int i = 0; i < numberClients; i++) {
            clients[i].sendMessage(message);
        }
    }

    //Obtener el reques de nuevo ship
    public void replyToNewShipRequest() {
        clients[numberClients].sendMessage("id " + numberClients); //Show the client their id in the game
        galagaGameStatus.addShip();
        String newShipPositionsMessage = "players " + numberClients;
        for(Ship ship : galagaGameStatus.ships) {
            newShipPositionsMessage += (" " + ship.xPos);
        }
        sendMessageTCPServer(newShipPositionsMessage);
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    public OnMessageReceived getMessageListener() {
        return this.messageListener;
    }
}
