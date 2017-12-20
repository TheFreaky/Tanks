package com.thefreak.utils.ServerConnection;

public interface ServerListener {

    void refreshData(int data);
    int dataToSend();
    void error(String text);
    void disconnected();

}
