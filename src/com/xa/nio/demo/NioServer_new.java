package com.xa.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Administrator on 2018/1/18.
 */
public class NioServer_new {

    private Selector selector = null;
    private Selector receiveMsgSelector = null;
    private Charset charset = Charset.forName("UTF-8");

    public void init() throws IOException, InterruptedException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        receiveMsgSelector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(6667));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server is ready...");

       // new ReceiveMsg().start();

        while (true) {
            try {
                int n = selector.select();
                if (n == 0) {
                    continue;
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                       try{
                           ServerSocketChannel server = (ServerSocketChannel) key.channel();
                           SocketChannel socketChannel = server.accept();
                           socketChannel.configureBlocking(false);
                           System.out.println(socketChannel.getRemoteAddress() + " 连接成功.");
                           socketChannel.register(selector, SelectionKey.OP_READ);
                           new SendMsg(socketChannel).start();
                       } catch (Exception e){
                           e.printStackTrace();
                       }
                    }

                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        String content = "";
                        while (socketChannel.read(byteBuffer) > 0) {
                            byteBuffer.flip();
                            content += charset.decode(byteBuffer);
                            byteBuffer.clear();
                        }
                        System.out.println("客户端说：" + content);
                    }
                }
                it.remove();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Thread.sleep(100);
            }
        }
    }


    class SendMsg extends Thread{
        private SocketChannel socketChannel;
        public SendMsg(SocketChannel socketChannel){
            this.socketChannel = socketChannel;
        }
        @Override
        public void run() {
            while (true){
                Scanner scanner = new Scanner(System.in);
                String content = "";
                if (scanner.hasNextLine()){
                    content = scanner.next();
                    try {
                        socketChannel.write(charset.encode(content));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new NioServer_new().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
