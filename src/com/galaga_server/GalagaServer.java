package com.galaga_server;

import com.galaga_game.Galaga;

import java.io.IOException;
import java.util.ArrayList;

public class GalagaServer extends Thread {
    private TCPServer tcpServer;
    public GalagaGameStatus galagaGameStatus;

    public static void main(String[] args) throws IOException {
        GalagaServer server = new GalagaServer();
        server.start();
    }

    @Override
    public void run() {
        galagaGameStatus = new GalagaGameStatus();
        try {
            tcpServer = new TCPServer(galagaGameStatus, new TCPServer.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    synchronized (this) {
                        received(message);
                    }
                }
            });
            new Thread(tcpServer).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void received(String message) {
        System.out.println("Client > " + message);
        if(message != null && !message.equals("")) {
            String[] splitted = message.split(" ");
            String command = splitted[0];
            switch (command) {
                case "start":   //Iniciar el juego en todos los clientes
                    String newShipPositionsMessage = "players " + tcpServer.numberClients;
                    for(Ship ship : galagaGameStatus.ships) {
                        newShipPositionsMessage += (" " + ship.xPos);
                    }
                    tcpServer.sendMessageTCPServer(newShipPositionsMessage);
                    sendStartGame();
                    break;
                case "key": //Recibe el input de un usuario
                    int idx = Integer.parseInt(splitted[1]);
                    int keyCode = Integer.parseInt(splitted[2]);
                    switch (keyCode) {
                        case 65:
                        case 68:
                            galagaGameStatus.ships.get(idx).moveShip(keyCode);
                            sendShipPosition(idx);
                            break;
                        case 75:
                            sendShooterPosition(idx);
                            break;
                        case 81:
                            tcpServer.clients[idx].stopClient();
                            break;
                    }

                    break;
            }
        }
    }

    public void sendStartGame() {
        tcpServer.sendMessageTCPServer("start");
    }

    //Send ship[idx] position: pos [idx] [xPos]
    public void sendShipPosition(int idx) {
        String message = "pos " + idx + " " + galagaGameStatus.ships.get(idx).xPos;
        tcpServer.sendMessageTCPServer(message);
    }

    public void sendShooterPosition(int idx) {
        tcpServer.sendMessageTCPServer("shoot " + idx);
    }

}

class GalagaGameStatus {
    public int height = 11, width = 39;
    public ArrayList<Ship> ships = new ArrayList<>();
    //public ArrayList<Enemy> enemies = new ArrayList<>();

    //Add a new Ship and reset all the ship positions
    public void addShip() {
        int gap = (width - (ships.size() + 1)) / (ships.size() + 2);
        int newPos = gap;
        for(int i = 0; i < ships.size(); i++) {
            ships.get(i).moveShipTo(newPos);
            newPos += (gap + 1);
        }
        Ship newShip = new Ship(newPos, 1, this);
        ships.add(newShip);
    }

}

class Ship {
    public int xPos;
    public int status;
    private GalagaGameStatus galagaGameStatus;

    //Creates a ship
    public Ship(int xPos, int status, GalagaGameStatus galagaGameStatus) {
        this.xPos = xPos;
        this.status = status;
        this.galagaGameStatus = galagaGameStatus;
    }

    //Moves a ship to a fixed positions
    public void moveShipTo(int xPos) {
        this.xPos = xPos;
    }

    //Moves a ship according to the keyCode
    public void moveShip(int keyCode) {
        switch(keyCode){
            // Izquierda
            case 65:
                if(this.xPos > 0)  this.xPos--;
                break;
            // Derecha
            case 68:
                if(this.xPos < galagaGameStatus.width - 1) this.xPos++;
                break;
        }
    }

}

/*class Enemy {
    public int xPos;
    public int yPos;
    public int status;
    private GalagaGameStatus galagaGameStatus;
    public void getEnemyStatus() {

    }
}

Comandos de envio

add 1 Indica que el jugado 1 se ha añadido al juego
player 1 Indica el nombre del jugador
move 1 65 Indica hacia donde mover el jugador 1
shot 1 Indica que el jugador 1 ha disparado
died 1 Indica que el jugado 1 ha muerto

 */