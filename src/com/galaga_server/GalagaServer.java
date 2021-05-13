package com.galaga_server;

import com.galaga_game.Galaga;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GalagaServer extends Thread {
    private ServerSocket serverSocket;
    private Galaga galaga;
    private int PORT = 4444;
    PrintWriter out;
    BufferedReader in;
    public int numberClients = 0;
    public TCPServerThread[] clients = new TCPServerThread[4]; // Max. number of players 4

    public GalagaServer(Galaga galaga) throws IOException {
        this.galaga = galaga;
        this.serverSocket = new ServerSocket(PORT);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket client = serverSocket.accept();
                numberClients++;
                System.out.println("Jugador " + numberClients + " conectado");
                clients[numberClients] = new TCPServerThread(client, this, numberClients, clients);
                Thread t = new Thread(clients[numberClients]);
                t.start();
                System.out.println("En total " + numberClients + " jugadores conectados");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Envia un mensaje "[playerID] [key]" al server
    public void sendData(String data) {
        if (out != null && !out.checkError()) {
            out.println(data);
            out.flush();
        }
    }

    //A cada uno de los clientes enviar la pantalla del juego
    /*public void sendMessageTCPServer(String message) {
        for (int i = 1; i <= nrcli; i++) {
            sendclis[i].sendMessage(message);
            System.out.println("ENVIANDO A JUGADOR " + (i));
        }
    }*/
}
