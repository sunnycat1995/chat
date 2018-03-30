package com.myproject.chat.network;

public interface ConnectionListener {

    public void onConnectionReady(Connection connection);
    public void onReceiveMessage(Connection connection, String message);
    public void onDisconnect(Connection connection);
    public void onException(Connection connection, Exception e);
}
