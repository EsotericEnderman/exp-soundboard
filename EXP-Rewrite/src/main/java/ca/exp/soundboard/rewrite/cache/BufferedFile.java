package ca.exp.soundboard.rewrite.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class BufferedFile extends TreadedByteBuffer {

    private File file;

    private final long fileSize;

    private long lastCallMS;

    private HashMap<String, Long> readingTracker;
    private BufferedInputStream bufferedInput = null;
    private byte[] byteBuffer;

    public File getFile() {
        return file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getLastCallTimeMS() {
        return lastCallMS;
    }

    public BufferedFile(File file, int bufferSize) {
        super(bufferSize);

        fileSize = file.length();
        readingTracker = new HashMap<>();

        try {
            bufferedInput = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }

        byteBuffer = new byte[bufferSize];
        lastCallMS = System.currentTimeMillis();
    }

    public byte[] readNextBytes(String uuid) {
        lastCallMS = System.currentTimeMillis();

        if (!readingTracker.containsKey(uuid)) {
            readingTracker.put(uuid, 0L);
        }

        long totalRead = readingTracker.get(uuid);

        if (totalRead >= fileSize) {
            try {
                bufferedInput.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (getCurrentBufferedSizeRounded() < fileSize) {
            int bytesRead = 0;

            try {
                bytesRead = bufferedInput.read(byteBuffer);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            if (bytesRead > 0) {
                concat(byteBuffer, bytesRead);
            }

            long prevRead = readingTracker.get(uuid).longValue();
            readingTracker.put(uuid, Long.valueOf(prevRead + bytesRead));
        }

        byte[] returnBytes = getNext(uuid);
        return returnBytes;
    }
}
