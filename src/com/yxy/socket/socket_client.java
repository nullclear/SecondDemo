package com.yxy.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Nuclear on 2020/8/7
 */
public class socket_client {
    public static void main(String[] args) throws IOException {
        Socket client = null;
        OutputStream out = null;
        InputStream in = null;
        ByteArrayOutputStream stream = null;
        try {
            //连接服务器
            client = new Socket("127.0.0.1", 8080);
            if (client.isConnected()) {
                System.out.println("连接成功");
            }

            //向服务器发送消息
            out = client.getOutputStream();
            String message = "服务器你好,我是客户端!\0";
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
            //client.shutdownOutput();

            //获取客户端的消息
            in = client.getInputStream();
            stream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                stream.write(buffer, 0, count);
                if (buffer[count - 1] == 0) {
                    break;//如果已经达到设置的结束标志就跳出
                }
            }
            stream.flush();
            String data = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("服务端发来消息:" + data);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close();
            }
        }
    }
}
