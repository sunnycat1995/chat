package com.myproject.chat;

import com.myproject.chat.network.Connection;
import com.myproject.chat.network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements ConnectionListener {
    private final List<Connection> connections = new ArrayList<Connection>();

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        System.out.println("Server running...");
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.err.println("Connection exception" + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendToAllConnections("Client connected: " + connection);
    }

    public synchronized void onReceiveMessage(Connection connection, String message) {
        sendToAllConnections(message);
    }

    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        sendToAllConnections("Client disconnected: " + connection);
    }

    public synchronized void onException(Connection connection, Exception e) {
        System.err.println("Connection exception: " + e);
    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        int connectionsSize = connections.size();
        for (int i = 0; i < connectionsSize; ++i) {
            connections.get(i).sendString(value);
        }
    }
}
