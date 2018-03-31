package com.myproject.chat;

import com.myproject.chat.network.Connection;
import com.myproject.chat.network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server implements ConnectionListener {

    private final ThreadPoolExecutor executor;

    private final List<Connection> connections = new CopyOnWriteArrayList<Connection>();

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        System.out.println("Server running...");
        executor = new ThreadPoolExecutor(1, 6, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(new Connection(this, clientSocket));
                } catch (IOException e) {
                    System.err.println("Connection exception" + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendToAllConnections("Client connected: " + connection);
    }

    public void onReceiveMessage(Connection connection, String message) {
        sendToAllConnections(message);
    }

    public void onDisconnect(Connection connection) {
        connections.remove(connection);
        sendToAllConnections("Client disconnected: " + connection);
    }

    public void onException(Connection connection, Exception e) {
        System.err.println("Connection exception: " + e);
    }

    private void sendToAllConnections(String message) {
        System.out.println(message);
        for (Connection connection : connections) {
            connection.sendString(message);
        }
    }
}
