package com.thefreak.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int START_CODE = -1;

    public static void main(String[] args) throws IOException {
        while (true) {
            try {
                int port = 6666;
                ServerSocket serverSocket = new ServerSocket(port);
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
                    scorePlayer1 = inputPlayer1.readInt();
                    scorePlayer2 = inputPlayer2.readInt();

                    outputPlayer1.writeInt(scorePlayer2);
                    outputPlayer2.writeInt(scorePlayer1);
                }
            } catch (IOException e) {
                System.out.println("Connection lost! Trying reconnect...");
            }
        }
    }

}
