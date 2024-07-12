package ca.exp.soundboard.rewrite.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TreadedByteBuffer {

    private ArrayList<byte[]> bytes;
    private HashMap<String, Integer> indexTracker;
    private int bufferSize;

    public TreadedByteBuffer(int bufferSize) {
        this.bufferSize = bufferSize;

        indexTracker = new HashMap<>();
        bytes = new ArrayList<>();
    }

    public void concat(byte[] byteArray, int bytesRead) {
        if (byteArray.length == bufferSize) {
            bytes.add(byteArray);
        } else {
            byteArray = Arrays.copyOfRange(byteArray, 0, bytesRead - 1);
            bytes.add(byteArray);
        }
    }

    public byte[] getNext(String uuid) {
        if (!indexTracker.containsKey(uuid)) {
            indexTracker.put(uuid, 0);
        }

        int index = indexTracker.get(uuid);
        byte[] returnArray = bytes.get(index);

        if (returnArray.length != bufferSize) {
            indexTracker.remove(uuid);
        }

        return returnArray;
    }

    public long getCurrentBufferedSizeRounded() {
        int arraySize = bytes.size();
        long bytesBuffered = arraySize * bufferSize;
        return bytesBuffered;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void clear() {
        bytes.clear();
    }
}
