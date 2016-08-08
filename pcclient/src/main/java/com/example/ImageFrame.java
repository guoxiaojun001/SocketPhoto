package com.example;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Created by gxj on 2016/7/15.
 * A frame with an image panel
 */
public class ImageFrame extends JFrame{

    public ImagePanel panel;
    public JButton jb;//截图
    public JButton switchCamera;//切换摄像头

    public ImageFrame(ServerSocket sSocket) {
        // get screen dimensions
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        // center frame in screen
        setTitle("ImageTest");
        setLocation((screenWidth - DEFAULT_WIDTH) / 2,
                (screenHeight - DEFAULT_HEIGHT) / 2);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        System.out.print("+++++++++++++++++++++++");

        // add panel to frame
        this.getContentPane().setLayout(null);
        panel = new ImagePanel(sSocket);
        panel.setSize(640, 480);
        panel.setLocation(0, 0);
        add(panel);
        jb = new JButton("Catch Photo");
        jb.setBounds(0, 480, 100, 50);

        add(jb);

        switchCamera = new JButton("switchCamera");
        switchCamera.setBounds(100, 480, 100, 50);

        add(switchCamera);

        jb.addActionListener(new SavePhotoListener(sSocket));//保存图片
        switchCamera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.switchCamera();
            }
        });
    }

    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 560;
}




