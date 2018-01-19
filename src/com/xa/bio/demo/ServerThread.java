package com.xa.bio.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    Socket socket;
    ArrayList<Socket> list;
    InputStream is;
    OutputStream os;

    public ServerThread(Socket socket, ArrayList<Socket> list) {
        this.socket = socket;
        this.list = list;
    }

    @Override
    public void run() {
        try {
            while (true) {
                is = socket.getInputStream();
                byte[] b = new byte[2048];
                int len = is.read(b);
                String str = new String(b, 0, len);
                System.out.println(str);
                for (Socket socket : list) {
                    os = socket.getOutputStream();
                    os.write(str.getBytes());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}