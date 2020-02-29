package ca.exp.soundboard.rewrite.cache;

import java.util.ArrayList;
import java.util.HashMap;

public class TreadedByteBuffer {
    private ArrayList<byte[]> fBytes;
    private HashMap<String, Integer> fIndexTracker;
    private int fBufferSize;

    public TreadedByteBuffer(int aBufferSize) {
        this.fBufferSize = aBufferSize;
        this.fIndexTracker = new HashMap<String, Integer>();
        this.fBytes = new ArrayList<byte[]>();
    }

    public void concat(byte[] aByteArray, int aBytesRead) {
        if (aByteArray.length == this.fBufferSize) {
            this.fBytes.add(aByteArray);
        } else {
            byte[] bytes = java.util.Arrays.copyOfRange(aByteArray, 0, aBytesRead - 1);
            this.fBytes.add(bytes);
        }
    }

    public byte[] getNext(String uuid) {
        if (!this.fIndexTracker.containsKey(uuid)) {
            this.fIndexTracker.put(uuid, Integer.valueOf(0));
        }

        int index = this.fIndexTracker.get(uuid).intValue();
        byte[] returnArray = this.fBytes.get(index);
        if (returnArray.length != this.fBufferSize)
            this.fIndexTracker.remove(uuid);
        return returnArray;
    }

    public long getCurrentBufferedSizeRounded() {
        int arraySize = this.fBytes.size();
        long bytesBuffered = arraySize * this.fBufferSize;
        return bytesBuffered;
    }

    public int getBufferSize() {
        return this.fBufferSize;
    }

    public void clear() {
        this.fBytes.clear();
    }
}
