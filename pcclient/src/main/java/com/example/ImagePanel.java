package com.example;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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


    public void switchCamera() {

        try{
        Socket socket = this.ss.accept();

        //构建IO
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        //向服务器端发送一条消息
        bw.write("测试客户端和服务器通信，服务器接收到消息返回到客户端\n");
        bw.flush();


        //读取服务器返回的消息
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String mess = br.readLine();
        System.out.println("服务器："+mess);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
