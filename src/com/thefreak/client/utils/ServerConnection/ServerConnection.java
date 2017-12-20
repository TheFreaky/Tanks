package com.thefreak.client.utils.ServerConnection;

import com.thefreak.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {

    private ServerListener listener;

    private static final int REFRESH_TIME = 1000;

    public ServerConnection(ServerListener listener) {
        this.listener = listener;
    }

    public void connect() {
        String address = "127.0.0.1";

        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            Socket socket = new Socket(ipAddress, Server.PORT);

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        int inputData = inputStream.readInt();
                        listener.refreshData(inputData);

                        int sendData = listener.dataToSend();
                        outputStream.writeInt(sendData);
                        Thread.sleep(REFRESH_TIME);
                    }
                } catch (IOException | InterruptedException e) {
                    listener.disconnected();
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            listener.error("Ошибка покдлючение к серверу!");
            e.printStackTrace();
        }
    }

}
