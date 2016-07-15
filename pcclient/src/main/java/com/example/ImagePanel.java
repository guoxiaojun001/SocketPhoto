package com.example;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Created by gxj on 2016/7/15.
 * A panel that displays a tiled image
 */
public class ImagePanel extends JPanel {

    private static final long serialVersionUID = -54451604355L;
    private ServerSocket ss;
    private Image image;
    private InputStream ins;

    public ImagePanel(ServerSocket ss) {
        this.ss = ss;
    }

    public void getimage() throws IOException {
        Socket s = this.ss.accept();
        System.out.println("connect success!");
        this.ins = s.getInputStream();
        this.image = ImageIO.read(ins);
        this.ins.close();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null)
            return;
        g.drawImage(image, 0, 0, null);
    }

}
