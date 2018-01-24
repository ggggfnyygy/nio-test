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
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    public void init() throws IOException, InterruptedException {
        //创建通道实例
        socketChannel = SocketChannel.open();
        //创建选择器实例
        selector = Selector.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6667);
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //连接
        socketChannel.connect(inetSocketAddress);
        //注册请求连接事件
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        /**
         * 轮询selector中的事件
         */
        while (true) {
            try{
                int n = selector.select();
                if (n <= 0){
                    continue;
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                if (it.hasNext()){
                    SelectionKey key = it.next();
                    handleKey(key);
                    //事件处理完毕，需要从键集中删除
                    it.remove();
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                Thread.sleep(100);
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (key.isConnectable()){ //是否完成套接字的连接操作
            socketChannel = (SocketChannel) key.channel();
            if (socketChannel.isConnectionPending()){//是否正在进行连接操作
                socketChannel.finishConnect();//完成连接操作
                System.out.println("连接成功！");
            }
            new SendMsg(socketChannel).start();
            //注册读事件
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()){
            try {
                String content = "";
                //把通道中的数据读入缓冲区
                while (socketChannel.read(byteBuffer) > 0){
                    //limit = position mark = -1 position = 0
                    byteBuffer.flip();
                    content += charset.decode(byteBuffer);
                    //limit = capcity position = 0 mark = -1
                    byteBuffer.clear();
                }
                System.out.println("服务器说：" + content);
            } catch (IOException e) {
                e.printStackTrace();
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
            new NioClient_new().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
