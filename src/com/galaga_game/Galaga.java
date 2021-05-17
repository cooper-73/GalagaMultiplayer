package com.galaga_game;

import com.galaga_client.GalagaGUI;
import com.galaga_client.TCPClient;

import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Galaga extends Thread {
	public int id;
	public int status = 1;
	public char symbol;	//Id del palyer 1, 2, 3, ...
	public GalagaGUI galagaGUI;
	public TCPClient tcpClient;

	Board newBoard;
	Game newGame;
	private ArrayList<Ship> ships = new ArrayList<>();
	Swarm newSwarm;
	Bomb newBomb;

	public Galaga() {
		this.galagaGUI = new GalagaGUI(this);
		newBoard = new Board();
		newGame = new Game(newBoard, galagaGUI);
		newSwarm = new Swarm(newBoard);
		newBomb = new Bomb(newBoard, newSwarm);
	}

	@Override
	public void run() {

	}

	public void init() {
		galagaGUI.setMessageText("");
		// initialize all threads
		newSwarm.start();
		newBomb.start();

		for(Ship ship : ships)	new Thread(ship).start();

		try {
			newGame.join();
		}
		catch (InterruptedException err) {
			err.printStackTrace();
		}

	}

	public void startClient(String IP, int PORT) throws InterruptedException {
		tcpClient = new TCPClient(IP, PORT, new TCPClient.OnMessageReceived() {
			@Override
			public void messageReceived(String message) {
				System.out.println(message);
				received(message);
			}
		});
		new Thread(tcpClient).start();
		Thread.sleep(10);
		new Thread(newGame).start();
	}

	public void setID(int id) {
		this.id = id;
		this.symbol = (char) (id + '0');
		System.out.println("id " + id);
		System.out.println("symbol " + symbol);
	}

	public void addNewShip(String addNewShipMessage) {
		String[] message = addNewShipMessage.split(" ");
		int numberShips = Integer.parseInt(message[1]);
		if(ships.size() == 0) {
			for(int i = 0; i < numberShips; i++) {
				char symbol = (char) (i + '0');
				int newPos = Integer.parseInt(message[i + 2]);
				ships.add(new Ship(symbol, newPos, 1, newBoard));
				ships.get(i).start();
			}
		}
		else {
			for(int i = 0; i < numberShips; i++) {
				int newPos = Integer.parseInt(message[i + 2]);
				if(i != numberShips - 1) ships.get(i).moveTo(newPos);
				else {
					char symbol = (char) (i + '0');
					ships.add(new Ship(symbol, newPos, 1, newBoard));
					ships.get(i).start();
				}
			}
		}
	}

	public void received(String message) {
		int idx;
		System.out.println("Server > " + message);
		if(message != null && !message.equals("")) {
			String[] splitted = message.split(" ");
			String command = splitted[0];
			switch (command) {
				case "id":
					int id = Integer.parseInt(splitted[1]);
					setID(id);
					break;
				case "players":
					addNewShip(message);
					break;
				case "start":
					init();
					break;
				case "pos":
					idx = Integer.parseInt(splitted[1]);
					int pos = Integer.parseInt(splitted[2]);
					ships.get(idx).moveTo(pos);
					break;
				case "shoot":
					idx = Integer.parseInt(splitted[1]);
					ships.get(idx).shoot();
					break;
			}
		}
	}
}

class Board {

	public int gameState, shipState, swarmState, shipFrontPos, swarmFrontPos;
	public int height = 11, width = 39;
	public char[] line0 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line1 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line2 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line3 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line4 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line5 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line6 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line7 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line8 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line9 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
	public char[] line10 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};

	public List<char[]> boardList = new ArrayList<>();

	//Establece estados en 1 y agrega los espacios en blanco a board
	Board() {
		gameState = 1;
		shipState = 1;
		swarmState = 1;
		boardList.add(line0);
		boardList.add(line1);
		boardList.add(line2);
		boardList.add(line3);
		boardList.add(line4);
		boardList.add(line5);
		boardList.add(line6);
		boardList.add(line7);
		boardList.add(line8);
		boardList.add(line9);
		boardList.add(line10);
	}

	public String draw() {
		String board = "";

		// Dibuja la pantalla de la batalla
		for(char[] line : boardList) {
			board += ("|");
			for (int i = 0; i < width; i++) {
				board += (line[i]);
			}
			board += ("|\n");
		}

		return board;
	}

	//Cuenta el numero de enemigos
	public int countSwarm(){
		int count = 0;
		for (char[] element : boardList) {
			for (int i = 0; i < width; i++) {
				if (element[i] == '*')	count++;
			}
		}
		return count;
	}
}


class Game extends Thread {
	public int state = 0;
	private Board board;
	private GalagaGUI galagaGUI = null;

	Game(Board board, GalagaGUI galagaGUI) {
		this.board = board;
		this.galagaGUI = galagaGUI;
	}

	@ Override
	public void run() {
		try {
			// Mientras el juego no haya acabado se va a imprimir la pantalla del juego cada 75 ms
			while (!gameOver()) {
				if(galagaGUI != null) {
					String boardText = board.draw();
					galagaGUI.setBoardText(boardText);
					Thread.sleep(80);
				}

			}
		}
		catch (InterruptedException err) {
			err.printStackTrace();
		}
	}

	//
	public synchronized void setMessage() {
		String message = "";
		if (board.shipState == 0) {
			message += ("+=====================+\n");
			message += ("+== G A M E O V E R ==+\n");
			message += ("+=====================+\n");
		}
		else {
			message += ("+=====================+\n");
			message += ("+==== YOU  WIN!!! ====+\n");
			message += ("+=====================+\n");
		}
		if(galagaGUI != null)	galagaGUI.setMessageText(message);
	}

	//Verifica si es que ya existe un perdedor
	private synchronized boolean gameOver(){
		return false;
		/*if (board.swarmState == 0 || board.shipState == 0)
			return true;
		else
			return false;*/
	}

}



class Ship extends Thread {
	public char symbol;
	public int xPos;
	public int status;
	private Board board;
	private boolean willShot = false;

	// Copia la referencia a Board y Laser, y dibuja la posicion inicial de la nave
	Ship(char symbol, int xPos, int status, Board board) {
		this.symbol = symbol;
		this.xPos = xPos;
		board.line9[xPos] = '^';
		board.line10[xPos] = symbol;
		this.status = status;
		this.board = board;
	}

	@Override
	public void run() {
		/*while(status == 1) {

		}*/
	}

	public void moveTo(int xPos) {
		board.line9[this.xPos] = ' ';
		board.line10[this.xPos] = ' ';

		this.xPos = xPos;

		if (board.shipState != 0) {
			board.line9[this.xPos] = '^';
			board.line10[this.xPos] = symbol;
		}
		else {
			board.line9[this.xPos] = ' ';
			board.line10[this.xPos] = ' ';
		}
	}

	public void shoot() {
		if(!willShot) {
			willShot = true;
			new Thread(new Laser(xPos)).start();
		}

		//Rafaga de disparos
		//new Thread(new Laser(xPos)).start();

	}

	class Laser extends Thread {
		private final int laserColumnPos;

		Laser(int laserColumnPos) {
			this.laserColumnPos = laserColumnPos;
		}

		@Override
		public void run() {
			try{
				int k = 100;
				char[] currentLaserRow;
				for (int i = board.height - 3; i >= 0; i--) {
					//Obtiene la fila donde se encuentra el disparo
					currentLaserRow = board.boardList.get(i);
					// Si el disparo llega a un enemigo, lo destruye y termina su trayectoria
					if (currentLaserRow[laserColumnPos] == '*'){
						currentLaserRow[laserColumnPos] = ' ';
						break;
					}
					//Dibuja el disparo antes del retardo
					currentLaserRow[laserColumnPos] = '\'';
					Thread.sleep(k);
					//Borra el disparo luego del retardo
					currentLaserRow[laserColumnPos] = ' ';
				}
			}
			catch (InterruptedException err) {
				err.printStackTrace();
			}
			willShot = false;
		}
	}
}


class Swarm extends Thread {
	private Board board;
	public int xpos;
	public int ypos;

	Swarm(Board board){
		this.board = board;
		// Dibuja el conjunto de naves enemigas de dos lineas
		for (int i = 15; i < 26; i++)	board.line0[i] = '*';
		for (int i = 16; i < 25; i++)	board.line1[i] = '*';
		// Posicion inicial del frente de las naves enemigas
		board.swarmFrontPos = 1;
		// Define la posicion del frente y la izquierda del conjunto de naves enemigas
		this.ypos = 1;
		this.xpos = 15;
	}

	@Override
	public void run() {
		// Velocidad de movimiento de las naves
		int speed = 250;
		try {
			Thread.sleep(speed);
			// El movimiento del conjunto de naves es desde el centro -> izquierda -> centro -> derecha -> abajo
			// hasta que llegue al fondo (demora 8 iteraciones)
			for (int i = 0; i < board.height - 3; i++) { // De acuerto a la pantalla y el tamaño de la nave
				for (int j = 0; j < 5; j++) {
					moveSwarmLeft();
					this.xpos--;
					Thread.sleep(speed);
				}
				for (int j = 0; j < 10; j++) {
					moveSwarmRight();
					this.xpos++;
					Thread.sleep(speed);
				}
				for (int j = 0; j < 5; j++) {
					moveSwarmLeft();
					this.xpos--;
					Thread.sleep(speed);
				}
				moveSwarmDown();
				this.ypos++;
				Thread.sleep(speed);
			}
		}
		catch (InterruptedException err){
			err.printStackTrace();
		}
	}

	public synchronized void moveSwarmLeft(){
		char[] frontArmy = board.boardList.get(board.swarmFrontPos);
		char[] backArmy = board.boardList.get(board.swarmFrontPos -1);
		for (int i = 0; i < board.width - 1; i++){
			// Si se disparó a una nave, se elimina su dibujo
			if (frontArmy[i + 1] == '\'') frontArmy[i + 1] = ' ';
			if (backArmy[i + 1] == '\'') backArmy[i + 1] = ' ';
			// Dibuja el desplazamiento
			frontArmy[i] = frontArmy[i + 1];
			backArmy[i] = backArmy[i + 1];
		}
		frontArmy[board.width - 1] = ' ';
		backArmy[board.width - 1] = ' ';
	}

	public synchronized void moveSwarmRight(){
		char[] frontArmy = board.boardList.get(board.swarmFrontPos);
		char[] backArmy = board.boardList.get(board.swarmFrontPos -1);
		for (int i = board.width - 1; i > 0; i--){
			if (frontArmy[i - 1] == '\'') frontArmy[i - 1] = ' ';
			if (backArmy[i - 1] == '\'') backArmy[i - 1] = ' ';
			frontArmy[i] = frontArmy[i - 1];
			backArmy[i] = backArmy[i - 1];
		}
		frontArmy[0] = ' ';
		backArmy[0] = ' ';
	}

	public synchronized void moveSwarmDown(){
		char[] frontArmy = board.boardList.get(board.swarmFrontPos);
		char[] backArmy = board.boardList.get(board.swarmFrontPos - 1);
		if(board.swarmFrontPos != board.height - 3){
			char[] newFront = board.boardList.get(board.swarmFrontPos + 1);
			System.arraycopy(frontArmy, 0, newFront, 0, board.width);
			System.arraycopy(backArmy, 0, frontArmy, 0, board.width);
			for (int i = 0; i < board.width; i++)	backArmy[i] = ' ';
			board.swarmFrontPos++;
		}
		else {
			// Si el conjunto de naves llega hasta el fondo, el jugador pierde
			for(int i = 0; i < board.width; i++)	board.line9[i] = board.line8[i];
			for (int i = 0; i < board.width; i++){
				board.line8[i] = board.line7[i];
				board.line7[i] = ' ';
			}
			board.shipState = 0;
		}
	}
}


class Bomb extends Thread {
	private Board board;
	private Swarm swarm;

	Bomb(Board board, Swarm swarm) {
		this.board = board;
		this.swarm = swarm;
	}

	@Override
	public void run() {
		while(true) {
			try{
				// Cada 200 ms el conjunto de naves disparará
				Thread.sleep(200);
				pew();
			}
			catch(InterruptedException err) {}
		}
	}

	private synchronized void pew() {
		try {
			int k = 300; //Velocidad del disparo entre filas
			char[] ypos;
			int xpos = ThreadLocalRandom.current().nextInt(swarm.xpos, swarm.xpos + 11); //Genera un lugar entre la nave para el disparo - cambiar el 11
			for (int i = 1; swarm.ypos + i < board.height; i++){
				ypos = board.boardList.get(swarm.ypos + i); // Fila donde se encuentra la bomba
				// Si la bomba alcanza al jugador, se hace un efecto de explosion
				if (ypos[xpos] == '^') {
					ypos[xpos] = 'o';
					Thread.sleep(50);
					if (xpos  - 1 >= 0) ypos[xpos  - 1] = 'o';
					if (xpos  + 1 <= board.width) ypos[xpos  + 1] = 'o';
					Thread.sleep(50);
					ypos[xpos ] = ' ';
					if (xpos  - 2 >= 0) ypos[xpos  - 2] = 'o';
					if (xpos  + 2 <= board.width) ypos[xpos  + 2] = 'o';
					Thread.sleep(50);
					if (xpos -1 >= 0) ypos[xpos - 1] = ' ';
					if (xpos + 1 <= board.width) ypos[xpos + 1] = ' ';
					Thread.sleep(50);
					if (xpos - 2 >= 0) ypos[xpos - 2] = ' ';
					if (xpos + 2 <= board.width) ypos[xpos + 2] = ' ';
					Thread.sleep(50);
					board.shipState = 0;
					break;
				}
				// Dibuja la bomba, y luego de un retardo la borra
				ypos[xpos ] = '\"';
				Thread.sleep(k);
				ypos[xpos ] = ' ';
			}
		}
		catch (InterruptedException err) {
			err.printStackTrace();
		}
	}
}
