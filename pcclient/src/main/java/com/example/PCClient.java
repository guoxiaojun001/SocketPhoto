package com.example;


import java.io.IOException;
import java.net.ServerSocket;
import javax.swing.JFrame;


public class PCClient {

    public static ServerSocket serverSocket = null;

    public static void main(String args[]) throws IOException {
        serverSocket = new ServerSocket(7799);

        final ImageFrame frame = new ImageFrame(serverSocket);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        while (true) {
            frame.panel.getimage();
            frame.repaint();
        }
    }

}

