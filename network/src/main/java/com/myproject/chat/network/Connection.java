package com.myproject.chat.network;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final ConnectionListener eventListener;

    public Connection(ConnectionListener eventListener, String ipAddress, int port) throws IOException {
        this(eventListener, new Socket(ipAddress, port));
    }

    public Connection(final ConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
    }

    public void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
           handleException(this, e);
        }
    }

    private void disconnect() {
        Thread.currentThread().interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    @Override
    public void run() {
        try {
            eventListener.onConnectionReady(this);
            while (!Thread.currentThread().isInterrupted()) {
                String msg = in.readLine();
                eventListener.onReceiveMessage(this, msg);
            }
        } catch (IOException e) {
            handleException(this, e);
        } finally {
            eventListener.onDisconnect(this);
        }
    }

    private void handleException(Connection connection, Exception e) {
        eventListener.onException(connection, e);
        disconnect();
    }
}
