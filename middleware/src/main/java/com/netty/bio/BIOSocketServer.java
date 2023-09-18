package com.netty.bio;


import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
  BIO blocking IO 阻塞IO

 */
public class BIOSocketServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            //使用telnet / socket.io tester 都可以触发连接
            serverSocket = new ServerSocket(9000);
            while (true){
                System.out.println("等待连接");
                Socket clientSocket = serverSocket.accept();//此处会阻塞，一次只能处理一个连接
                System.out.println("有客户端连接了："+ clientSocket.getInetAddress());
                handler(clientSocket);//此处如果不退出，上面的accept就无法接入新连接
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("准备读取。。。");
        int read = clientSocket.getInputStream().read(bytes);
        if (read != -1){
            String content = new String(bytes, 0, read);
            System.out.println("接受到客户端数据："+content);
        }
        System.out.println("读取完毕");
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write("helloClient".getBytes());
        outputStream.flush();
    }
}
