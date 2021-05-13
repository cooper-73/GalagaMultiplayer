package com.galaga_client;

import com.galaga_game.Galaga;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class GalagaClient extends Thread {
    private InetAddress ipAddress;
    private Socket socket;
    private Galaga galaga;
    private int PORT = 4444;
    PrintWriter out;
    BufferedReader in;
    Scanner input;

    public GalagaClient(Galaga galaga, String ipAddress) throws IOException {
        this.galaga = galaga;
        this.ipAddress = InetAddress.getByName(ipAddress);
        this.socket = new Socket(this.ipAddress, PORT);
        input = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            try {
                while(true) {
                    //Recibe un mensaje
                    //Recibe pantalla
                    //Recibe status del jugador
                    //Recibe status del juego
                    //Procesar
                    String serverMessage = in.readLine();
                    if(serverMessage != null) {
                        System.out.println("Server > " + serverMessage);
                        galaga.moveShip(Integer.parseInt(serverMessage.substring(2)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Envia un mensaje "[playerID] [key]" al server
    public void sendData(String data) {
        if(out != null && !out.checkError()) {
            out.println(data);
            out.flush();
        }
    }
}
