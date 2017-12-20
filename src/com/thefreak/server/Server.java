package com.thefreak.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int START_CODE = -1;
    public static final int DISCONNECT_CODE = -2;
    public static final int PORT = 6666;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {

                System.out.println("Waiting for a client...");
                Socket socketPlayer1 = serverSocket.accept();
                System.out.println("Player 1 connected! Wait second player...");
                Socket socketPlayer2 = serverSocket.accept();
                System.out.println("Player 2 connected! The battle begin!");

                DataInputStream inputPlayer1 = new DataInputStream(socketPlayer1.getInputStream());
                DataInputStream inputPlayer2 = new DataInputStream(socketPlayer2.getInputStream());

                DataOutputStream outputPlayer1 = new DataOutputStream(socketPlayer1.getOutputStream());
                DataOutputStream outputPlayer2 = new DataOutputStream(socketPlayer2.getOutputStream());

                outputPlayer1.writeInt(START_CODE);
                outputPlayer2.writeInt(START_CODE);

                int scorePlayer1;
                int scorePlayer2;

                while (true) {
                    try {
                        scorePlayer1 = inputPlayer1.readInt();
                    } catch (IOException e) {
                        System.out.println("Connection from Player 1 lost!");
                        outputPlayer2.writeInt(DISCONNECT_CODE);
                        break;
                    }
                    try {
                        scorePlayer2 = inputPlayer2.readInt();
                    } catch (IOException e) {
                        System.out.println("Connection from Player 2 lost!");
                        outputPlayer1.writeInt(DISCONNECT_CODE);
                        break;
                    }

                    outputPlayer1.writeInt(scorePlayer2);
                    outputPlayer2.writeInt(scorePlayer1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection lost! Trying reconnect...");
        }
    }

}
