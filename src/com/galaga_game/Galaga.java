package com.galaga_game;

import com.galaga_client.GalagaGUI;
import com.galaga_server.GalagaServer;
import com.galaga_client.GalagaClient;

import java.util.*;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class Galaga extends Thread {
	private GalagaGUI galagaGUI = null;
	private GalagaServer server = null;
	public GalagaClient client = null;
	private String type;
	Board newBoard;
	Game newGame;
	Laser newLaser;
	public Ship newShip;
	Swarm newSwarm;
	Bomb newBomb;

	public Galaga(String type) {
		this.type = type;
	}

	public Galaga(String type, GalagaGUI galagaGUI) {
		this.type = type;
		this.galagaGUI = galagaGUI;
	}

	@Override
	public void run() {
		try {
			if(type.equals("server")) {
				server = new GalagaServer(this);
				server.start();
			}
			if(type.equals("client")) {
				client = new GalagaClient(this, "192.168.0.27");
				client.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// initialize all threads
		newBoard = new Board();
		newGame = new Game(newBoard, galagaGUI);
		newLaser = new Laser(newBoard);
		newShip = new Ship(newBoard, newLaser, galagaGUI);
		newSwarm = new Swarm(newBoard);
		newBomb = new Bomb(newBoard, newSwarm);

		// get all threads to start();
		newShip.start();
		newSwarm.start();
		newLaser.start();
		newBomb.start();
		newGame.start();

		try {
			newGame.join();
		}
		catch (InterruptedException err) {
			err.printStackTrace();
		}
	}

	public void moveShip(int keyCode) {
		if(newShip != null)	newShip.moveShip(keyCode);
	}
}

class Board {

	public int gameState, shipState, swarmState, shipFrontPos, swarmFrontPos;
	public int height = 11, width = 43;
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

	// Dibuja la pantalla del jeugo
	/*public void draw() {

		//Limpia la pantalla
		final String ANSI_CLS = "\u001b[2J";
		final String ANSI_HOME = "\u001b[H";
		System.out.print(ANSI_CLS + ANSI_HOME);
		System.out.flush();

		// Banner
		System.out.println("+=====================+");
		System.out.println("|=====================|");
		System.out.println("|== G E R L E G E R ==|");
		System.out.println("|=====================|");
		System.out.println("+=====================+");

		// Dibuja la pantalla de la batalla
		for(char[] line : boardList) {
			System.out.print("|");
			for (int i = 0; i < width; i++) {
				System.out.print("" + line[i]);
			}
			System.out.print("|\n");
		}

		// Instrucciones
		System.out.println("+=====================+");
		System.out.println("| A/D - move ship     |");
		System.out.println("| K   - pew pew pew   |");
		System.out.println("| Q   - bye osm game  |");
		System.out.println("+=====================+");

	}*/

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
	public synchronized int countSwarm(){
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
				//board.draw();
				if(galagaGUI != null)	galagaGUI.setBoardText(board.draw());
				Thread.sleep(75);
			}

			// Se vuelve a encender ECHO e ICANON al finalizar el juego
			//String[] cmd = {"/bin/sh", "-c", "stty sane </dev/tty"};
			//Runtime.getRuntime().exec(cmd);

			// Dibuja la pantalla del juego
			//board.draw();
			if(galagaGUI != null)	galagaGUI.setBoardText(board.draw());

			// Imprime el mensaje final del juego
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
			//System.exit(1);
		}
		catch (InterruptedException err) {
			err.printStackTrace();
		}
	}

	//Verifica si es que ya existe un perdedor
	private synchronized boolean gameOver(){
		if (board.swarmState == 0 || board.shipState == 0)
			return true;
		else
			return false;
	}

}


class Laser extends Thread {

	private Board board;
	private boolean shoot = false;

	Laser(Board board) {
		this.board = board;
	}

	@Override
	public void run() {
		while(true) {
			// Si va a disparar, modifica el flag disparo a false y dispara
			if(willShoot()) {
				shoot = false;
				pew();
			}
		}
	}

	// Modifica el flag disparo a True
	public synchronized void blast() {
		shoot = true;
	}

	// Retorna True si hay un disparo, false caso contrario. Si no hay enemigos, pone su estado en 0
	synchronized boolean willShoot(){
		if (board.countSwarm() == 0)	board.swarmState = 0;
		return shoot;
	}

	private synchronized void pew() {
		try{
			int k = 100;
			char[] currentLaserRow;
			int laserColumnPos = board.shipFrontPos;
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
	}
}


class Ship extends Thread {
	private GalagaGUI galagaGUI = null;
	private Board board;
	private Laser laser;
	private boolean run;

	// Copia la referencia a Board y Laser, y dibuja la posicion inicial de la nave
	Ship(Board board, Laser laser, GalagaGUI galagaGUI) {
		this.board = board;
		this.laser = laser;
		this.galagaGUI = galagaGUI;
		board.shipFrontPos = board.width / 2;
		board.line9[board.width / 2] = '^';
		board.line10[board.width / 2] = '0';
		run = true;
	}

	@Override
	public void run() {

		//try {
			// Deshabilita ECHO e ICANON
		    //String[] cmd0 = {"/bin/sh", "-c", "stty -echo </dev/tty"};
		    //String[] cmd1 = {"/bin/sh", "-c", "stty -icanon time 0 min 0 </dev/tty"};
		    //String[] cmd2 = {"/bin/sh", "-c", "stty sane </dev/tty"};
			//Runtime.getRuntime().exec(cmd0);
			//Runtime.getRuntime().exec(cmd1);



			// Lee del teclado y mueve la nave de acuerdo a la entrada, hasta que el usuario presione Q
			while(run) {
				/*if(galagaGUI != null && galagaGUI.keyCode != 0) {
					System.out.println("Galaga game: " + galagaGUI.keyCode);
					moveShip(galagaGUI.keyCode);
				}
				galagaGUI.keyCode = 0;*/
			}

			/*while (keyPress != 'q' && keyPress != 'Q') {
				keyPress = (char) System.in.read();
				moveShip(keyPress);
			}*/

			// Finaliza el juego
			//Runtime.getRuntime().exec(cmd2); // Enciende ECHO e ICANON de nuevo
			System.out.println("+====  YOU  QUIT  ====+");
			System.out.println("+=====================+\n");
			System.exit(1);
		//}
		//catch (IOException err) {
		//	err.printStackTrace();
		//}
	}

	// Mueve la nave de acuerdo a la tecla presionada
	public void moveShip(int keyCode) {
		switch(keyCode){
			// Izquierda
			case 65:
				if (board.shipFrontPos > 0){
					board.shipFrontPos--;
					board.line9[board.shipFrontPos + 1] = ' ';
					board.line10[board.shipFrontPos + 1] = ' ';
				}
				System.out.println("izquierda");
				break;
			// Derecha
			case 68:
				if (board.shipFrontPos < board.width - 1){
					board.shipFrontPos++;
					board.line9[board.shipFrontPos - 1] = ' ';
					board.line10[board.shipFrontPos - 1] = ' ';
				}
				break;
			// Disparo
			case 75:
				laser.blast();
				break;
			case 81:
				run = false;
				break;
		}

		// Si el estado de la nave es 1 la dibuja, sino dibuja un espacio en blanco
		if (board.shipState != 0) {
			board.line9[board.shipFrontPos] = '^';
			board.line10[board.shipFrontPos] = '0';
		}
		else {
			board.line9[board.shipFrontPos] = ' ';
			board.line10[board.shipFrontPos] = ' ';
		}
	}


	public interface KeyPressReceived {
		void keyPress();
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
