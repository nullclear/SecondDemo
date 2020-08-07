package com.yxy.udp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Nuclear on 2020/8/7
 */
public class MyReceiver extends JFrame implements Runnable, ActionListener {
    private static final long serialVersionUID = -2381273738033979818L;

    int port = 8088; // 设置端口号
    InetAddress group = null; // 声明InetAddress对象
    MulticastSocket socket = null; // 创建多点广播套接字对象

    JButton start = new JButton("开始接收"); // 创建按钮对象
    JButton stop = new JButton("停止接收");
    JTextArea nowMessage = new JTextArea(10, 20); // 显示接收广播的文本域
    JTextArea displayBoard = new JTextArea(10, 20);
    Thread thread; // 创建Thread对象
    boolean flag = true; // 是否接收广播

    public MyReceiver() { // 构造方法
        super("广播数据报"); // 调用父类方法
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        thread = new Thread(this);
        start.addActionListener(this); // 绑定按钮start的单击事件
        stop.addActionListener(this); // 绑定按钮stop的单击事件
        nowMessage.setForeground(Color.blue); // 指定文本域中文字颜色
        JPanel north = new JPanel(); // 创建Jpanel对象
        north.add(start); // 将按钮添加到面板north上
        north.add(stop);
        add(north, BorderLayout.NORTH); // 将north放置在窗体的上部
        JPanel center = new JPanel(); // 创建面板对象center
        center.setLayout(new GridLayout(1, 2)); // 设置面板布局
        center.add(nowMessage); // 将文本域添加到面板上
        final JScrollPane scrollPane = new JScrollPane();
        center.add(scrollPane);
        scrollPane.setViewportView(displayBoard);
        add(center, BorderLayout.CENTER); // 设置面板布局
        validate(); // 刷新
        setBounds(100, 50, 360, 380); // 设置布局
        setVisible(true); // 将窗体设置为显示状态

        try {
            group = InetAddress.getByName("224.255.10.0"); // 指定接收地址
            socket = new MulticastSocket(port); // 绑定多点广播套接字
            socket.joinGroup(group); // 加入广播组
        } catch (Exception e) {
            e.printStackTrace(); // 输出异常信息
        }
    }

    @Override
    public void run() {
        // 当变量等于false时，退出循环
        do {
            byte[] data = new byte[1024]; // 创建byte数组
            DatagramPacket packet; // 创建DatagramPacket对象
            // 待接收的数据包
            packet = new DatagramPacket(data, data.length, group, port);
            try {
                socket.receive(packet); // 接收数据包
                String message = new String(packet.getData(),
                        0, packet.getLength(), StandardCharsets.UTF_8); // 获取数据包中内容
                // 将接收内容显示在文本域中
                nowMessage.setText("正在接收的内容：\n" + message);
                displayBoard.append(message + "\n"); // 每条信息为一行
            } catch (Exception e) {
                e.printStackTrace(); // 输出异常信息
            }
        } while (flag);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // 单击事件
        if (e.getSource() == start) { // 单击按钮start触发的事件
            start.setBackground(Color.red); // 设置按钮颜色
            stop.setBackground(Color.yellow);
            if (!thread.isAlive()) { // 如果线程死了
                thread = new Thread(this); // 实例化Thread对象
            }
            flag = true; // 接收广播
            thread.start(); // 启动线程
        }

        if (e.getSource() == stop) { // 单击按钮stop触发的事件
            start.setBackground(Color.yellow); // 设置按钮颜色
            stop.setBackground(Color.red);
            flag = false; // 停止接收广播
        }
    }

    public static void main(String[] args) { // 主方法
        MyReceiver rec = new MyReceiver(); // 创建本类对象
        rec.setSize(700, 400); // 设置窗体大小
    }
}
