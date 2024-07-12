package ca.exp.soundboard.rewrite.soundboard;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SoundUtils {

    public static short[] byteToShortArray(byte[] byteArray) {
        short[] shortArray = new short[byteArray.length / 2];

        for (int i = 0; i < shortArray.length; i++) {
            int a = byteArray[(i * 2 + 0)] & 0xFF;
            int b = byteArray[(i * 2 + 1)] & 0xFF;

            shortArray[i] = ((short) ((b << 8) + a));
        }

        return shortArray;
    }

    public static byte[] shortArrayToByteArray(short[] shortArray) {
        byte[] byteArray = new byte[shortArray.length * 2];

        ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortArray);

        return byteArray;
    }
}
