package com.xa.test;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2018/1/19.
 */
public class Test_ByteBuffer {

    public static Test_ByteBuffer createByteBuffer(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put(9, (byte)'a');
        System.out.println(byteBuffer.get(0));
        System.out.println(byteBuffer.get(9));
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.position());
        byteBuffer.put((byte)'b');
        System.out.println(byteBuffer.position());
        return null;
    }

    public static ByteBuffer createWrapByteBuffer(){
        byte[] byteArray = new byte[100];
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        byteBuffer.put(10, (byte)'a');
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());

        return null;
    }

    public static ByteBuffer hasremaining(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        byteBuffer.put((byte)'a');
        //byteBuffer.put((byte)'b');
        System.out.println(byteBuffer.hasRemaining());
        return null;
    }

    public static void main(String[] args) {
        createByteBuffer();
        System.out.println("--------------------------------");
        createWrapByteBuffer();
        System.out.println("--------------------------------");
        hasremaining();
    }
}
