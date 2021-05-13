package com.galaga_server;

import java.io.*;
import java.net.Socket;

//Hilo del servidor
public class TCPServerThread extends Thread{
    private Socket client;
    private int clientID;
    private GalagaServer galagaServer; //Referencia al servidor
    TCPServerThread[] cli_amigos; //Referencia a los otros hilos del servidor
    public PrintWriter out;
    public BufferedReader in;

    public TCPServerThread(Socket client, GalagaServer galagaServer, int clientID, TCPServerThread[] cli_amigos) {
        this.client = client;
        this.galagaServer = galagaServer;
        this.clientID = clientID;
        this.cli_amigos = cli_amigos;
    }

    public void run() {
        try {
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                //messageListener = tcpserver.getMessageListener();
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                // Lee de la terminal del cliente
                while (true) {
                    String message = in.readLine();
                    if (message != null) {
                        System.out.println("Client > " + message);
                        sendMessage(message);
                    }
                    if(message != null && (message.charAt(2) == 'q' || message.charAt(2) == 'Q'))    break;
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

    //Imprime mensaje en la terminal del cliente
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }
}
