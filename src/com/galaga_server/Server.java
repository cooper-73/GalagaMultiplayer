package com.galaga_server;

import com.galaga_game.Galaga;

public class Server {
    public static void main(String[] args) {
        new Thread(new Galaga("server")).start();
    }
}
