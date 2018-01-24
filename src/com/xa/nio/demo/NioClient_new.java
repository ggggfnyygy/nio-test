package com.xa.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Administrator on 2018/1/22.
 */
public class NioClient_new {


    private SocketChannel socketChannel = null;
    private Selector selector = null;
    private Charset charset = Charset.forName("UTF-8");

    public void init() throws IOException {
        //创建通道实例
        socketChannel = SocketChannel.open();
        //创建选择器实例
        selector = Selector.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6667);
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //连接
        boolean isConnect = socketChannel.connect(inetSocketAddress);


        //注册读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //创建一个线程，当通道有可读事件时，执行读逻辑
        new receiveMsg().start();
        //控制台阻塞输入
        Scanner scanner = new Scanner(System.in);
        //当控制台有输入时，发送到服务器
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            socketChannel.write(charset.encode(line));
        }
    }

    private class receiveMsg extends Thread{
        @Override
        public void run() {
            System.out.println("客户端接收消息线程启动...");
            while(true){
                try {
                    //筛选出一个准备就绪的IO组
                    int n = selector.select();
                    if (n == 0){
                        continue;
                    }

                    //获取所有注册在selector上的selectionKey
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()){
                        SelectionKey key = it.next();
                        //可读
                        if (key.isReadable()){
                            //获取此selectionKey绑定的通道
                            SocketChannel sc = (SocketChannel) key.channel();
                            String content = "";
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            //把通道中的数据读入缓冲区
                            while (sc.read(byteBuffer) > 0){
                                //limit = position mark = -1 position = 0
                                byteBuffer.flip();
                                content += charset.decode(byteBuffer);
                                //limit = capcity position = 0 mark = -1
                                byteBuffer.clear();
                            }
                            System.out.println(content);
                        }
                        //重新添加一个读事件
                        key.interestOps(SelectionKey.OP_READ);
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
            new NioClient_new().init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
