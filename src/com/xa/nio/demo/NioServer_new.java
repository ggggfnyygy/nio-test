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

    public void init() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        receiveMsgSelector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(6667));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server is ready...");

        new ReceiveMsg().start();

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
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = server.accept();
                        socketChannel.configureBlocking(false);
                        System.out.println(socketChannel.getRemoteAddress() + " 连接成功.");
                        socketChannel.register(receiveMsgSelector, SelectionKey.OP_READ);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



private class ReceiveMsg extends Thread {

    @Override
    public void run() {
        System.out.println("服务器接收消息线程启动...");

        while (true) {
            try {
                //筛选出一个准备就绪的IO组
                int n = receiveMsgSelector.select();
                if (n <= 0) {
                    continue;
                }

                //获取所有注册在selector上的selectionKey
                Iterator<SelectionKey> it = receiveMsgSelector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    //可读
                    if (key.isWritable()) {
                        //获取此selectionKey绑定的通道
                        SocketChannel sc = (SocketChannel) key.channel();
                        Scanner scanner = new Scanner(System.in);
                        String content = scanner.next();
                        sc.write(charset.encode(content));
                        //重新添加一个读事件
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    it.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

    public static void main(String[] args) {
        try {
            new NioServer_new().init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
