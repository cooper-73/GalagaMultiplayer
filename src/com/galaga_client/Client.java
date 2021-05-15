package com.galaga_client;

import com.galaga_game.Galaga;

public class Client {

    public static void main(String[] args) {
        //GalagaGUI galagaGUI = new GalagaGUI();
        Galaga galaga = new Galaga();
        /*new Thread(new TCPClient("192.168.0.27", new TCPClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                System.out.println(message);
            }
        })).start();*/
    }
}
