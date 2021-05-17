package com.galaga_client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends Thread {
    public String IP;
    public int PORT;
    private OnMessageReceived mMessageListener = null;
    private boolean running = false;
    PrintWriter out;
    BufferedReader in;

    public TCPClient(String ip, int PORT, OnMessageReceived listener) {
        this.IP = ip;
        this.mMessageListener = listener;
        this.PORT = PORT;
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        try {
            InetAddress serverAddr = InetAddress.getByName(IP);
            Socket socket = new Socket(serverAddr, PORT);
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (running) {
                    String message = in.readLine();
                    if(message != null && mMessageListener != null) {
                        mMessageListener.messageReceived(message);
                    }
                }
            } catch (Exception e) {
                System.out.println("TCP" + "S: Error" + e);

            } finally {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("TCP" + "C: Error" + e);
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message) throws InterruptedException;
    }
}
