package com.xa.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Created by Administrator on 2018/1/22.
 */
public class NioClient {

    public static void main(String[] args) {
        sendMsg();
    }

    public static void sendMsg() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost",6667);
        SocketChannel socketChannel = null;

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try{
            while (true){
                Scanner scanner = new Scanner(System.in);
                String str = scanner.next();
                new ClientSendMsgThread(socketChannel,byteBuffer,inetSocketAddress, str).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (socketChannel != null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




}
