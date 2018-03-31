package com.myproject.chat;


import com.myproject.chat.network.Connection;
import com.myproject.chat.network.ConnectionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

public class ClientWindow extends JFrame implements ConnectionListener, ActionListener {
    public static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private final JPanel panel = new JPanel();
    private final JTextArea log = new JTextArea();
    private JTextField fieldNickName;
    private final JTextField fieldInput = new JTextField();
    private Connection connection;
    private String nickName;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientWindow();
            }
        });
    }

    public ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        panel.setSize(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        fieldInput.getScrollOffset();
        add(fieldInput, BorderLayout.SOUTH);

        System.out.println("Enter your nickname: ");
        Scanner in = new Scanner(System.in);
        nickName = in.nextLine();
        fieldNickName = new JTextField(nickName);

        add(fieldNickName, BorderLayout.NORTH);
        setVisible(true);

        try {
            connection = new Connection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    public void onConnectionReady(Connection connection) {
        printMessage("Connection ready...\n");
    }

    public void onReceiveMessage(Connection connection, String message) {
        printMessage(message);
    }

    public void onDisconnect(Connection connection) {
        printMessage("Connection close");
    }

    public void onException(Connection connection, Exception e) {
        printMessage("Connection exception " + e);
    }
}

