package com.thefreak.utils.ServerConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {

    private ServerListener listener;

    private static final int REFRESH_TIME = 1000;

    public ServerConnection(ServerListener listener) {
        this.listener = listener;
    }

    public void connect() {
        int serverPort = 6666;
        String address = "127.0.0.1";

        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            Socket socket = new Socket(ipAddress, serverPort);

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
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}