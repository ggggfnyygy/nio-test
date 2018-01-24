package com.xa.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Administrator on 2018/1/18.
 */
public class NioServer {

    public static void main(String[] args) {
        serverSocketChannel();
    }


    public static void serverSocketChannel(){
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            //每个serverScoketChannel都有一个ServerSocket
            ServerSocket serverSocket = serverSocketChannel.socket();

            //创建一个selector实例
            Selector selector = Selector.open();
            serverSocket.bind(new InetSocketAddress(6667));
            serverSocketChannel.configureBlocking(false);

            //把该通道注册到选择器中，每次注册通道到选择器都会生成一个选择键key，
            //可能通过selector.selectionKeys获取所有注册在该选择器上的key,
            // 通过key.channel()获取对应的通道
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                int n = selector.select();
                if (n == 0){
                    continue;
                }
                Iterator<SelectionKey> it = (Iterator) selector.selectedKeys().iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();

                    //判断该通道是否已准备好接受新的套接字连接 == k.readyOps() & OP_ACCEPT != 0
                    if (key.isAcceptable()){

                        //通过key获取其对应的ServerSocketChannel
                        ServerSocketChannel tempServerSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = tempServerSocketChannel.accept();
                        socketChannel.configureBlocking(false);

                        //注册读事件
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }

                    //测试此通道是否完成对其套接字的连接
                    if (key.isConnectable()){
                        System.out.println("连接成功！");
                    }

                    //可读
                    if (key.isReadable()) {
                        readData(key);
                    }

                    if (key.isWritable()){
                        writerData(key);
                    }


                    //从selected set中移除这个key，因为它已经被处理过了
                    it.remove();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     * @param key
     * @throws IOException
     */
    private static void writerData(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //Object obj = key.attachment();//获取此键的附加对象
        //key.attach("");//将指定对象附加到此键

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Scanner scanner = new Scanner(System.in);
        String str = scanner.next();
        byteBuffer.clear();
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);
    }


    /**
     * 读取通道中的数据
     * @return
     */
    private static void readData(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int count;
        //如果读到了流的末尾，则返回-1
        while ((count = socketChannel.read(byteBuffer)) > 0){
            byteBuffer.flip();//mark=-1 position=0 limit=position
            byte[] bytes = byteBuffer.array();
            String a = new String(bytes);
            System.out.println("客户端说：" + a);
            byteBuffer.clear();//mark=-1 position=0 limit=capcity
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    /**
     * serverScocketChannel
     */
    public static void simpleServerSocketChannel(){
        try {
            //打开通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //每个通道都有一个socket，通道并没有bind方法，所以通过对等的socket绑定IP和端口
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(6666));
            //设置成非阻塞
            serverSocketChannel.configureBlocking(false);

            String str = "fuck";
            ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
            while (true){
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null){
                    Thread.sleep(2000);
                } else {
                    System.out.println("Incoming conection from : " + socketChannel.socket().getInetAddress());
                    byteBuffer.rewind();
                    socketChannel.write(byteBuffer);
                    socketChannel.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
