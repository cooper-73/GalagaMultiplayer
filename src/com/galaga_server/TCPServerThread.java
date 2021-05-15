package com.galaga_server;

import java.io.*;
import java.net.Socket;

//Hilo del servidor
public class TCPServerThread extends Thread{
    private Socket client;
    private TCPServer TCPServer; //Referencia al servidor
    TCPServerThread[] cli_amigos; //Referencia a los otros hilos del servidor
    public PrintWriter out;
    public BufferedReader in;
    private boolean running = true;
    private TCPServer.OnMessageReceived messageListener = null;

    public TCPServerThread(Socket client, TCPServer TCPServer, TCPServerThread[] cli_amigos) {
        this.client = client;
        this.TCPServer = TCPServer;
        this.cli_amigos = cli_amigos;
    }

    public void run() {
        try {
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                messageListener = TCPServer.getMessageListener();
                // Lee de la terminal del cliente
                while (running) {
                    String message = in.readLine();
                    if (message != null && messageListener != null) messageListener.messageReceived(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopClient() {
        running = false;
    }

    //Envia mensaje al cliente
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }
}
