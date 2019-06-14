package ca.exp.soundboard;

import java.nio.ByteBuffer;

public class ByteBufferTest {

    public static void main(String[] args) {
        // buffer to array
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 5 + Character.BYTES * 2);
        buffer.putInt(2);
        buffer.putInt(4);
        buffer.putChar('a');
        buffer.putChar('c');
        buffer.putInt(7);
        buffer.putInt(22);
        buffer.putInt(30);
        buffer.rewind();
        byte[] arr = buffer.array();

        // array to buffer
        ByteBuffer buffer2 = ByteBuffer.allocate(Integer.BYTES * 5 + Character.BYTES * 2);
        buffer2.put(arr);

        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());

    }
}
