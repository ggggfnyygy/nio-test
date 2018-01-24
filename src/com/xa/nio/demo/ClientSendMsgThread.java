package com.xa.nio.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2018/1/23.
 */
public class ClientSendMsgThread extends Thread{

        private SocketChannel socketChannel;
        private ByteBuffer byteBuffer;
        private InetSocketAddress inetSocketAddress;
        private String str;
        public ClientSendMsgThread(SocketChannel socketChannel, ByteBuffer byteBuffer, InetSocketAddress inetSocketAddress, String str){
            this.socketChannel = socketChannel;
            this.byteBuffer = byteBuffer;
            this.inetSocketAddress = inetSocketAddress;
            this.str = str;
        }

        int count;
        @Override
        public void run() {
            try{
                socketChannel = SocketChannel.open();
                socketChannel.connect(inetSocketAddress);
                byteBuffer.clear();
                byteBuffer.put(str.getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();

                while ((count = socketChannel.read(byteBuffer)) > 0) {
                    String str1 = new String(byteBuffer.array(), 0, count);
                    System.out.println(str1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
