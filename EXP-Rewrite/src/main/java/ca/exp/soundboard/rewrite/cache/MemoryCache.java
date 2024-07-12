package ca.exp.soundboard.rewrite.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {

    private static final long MEGABYTES_TO_BYTES = 1000000L;

    private ConcurrentHashMap<String, SoftReference<BufferedFile>> map;
    private long maxCacheBytes;

    public MemoryCache(int maxMegabytes) {
        map = new ConcurrentHashMap<String, SoftReference<BufferedFile>>();
        maxCacheBytes = (maxMegabytes * MEGABYTES_TO_BYTES);
    }

    public BufferedFile getBufferedFile(File file, int bufferSize) {
        if (!map.containsKey(file.toString())) {
            BufferedFile bufferedFile = new BufferedFile(file, bufferSize);
            SoftReference<BufferedFile> sortPreference = new SoftReference<>(bufferedFile);

            map.put(file.toString(), sortPreference);
            cacheMaintainance();
        }

        BufferedFile bufferedFile = map.get(file.toString()).get();

        if (bufferedFile != null) {
            return bufferedFile;
        }

        map.remove(file.toString());
        return getBufferedFile(file, bufferSize);
    }

    private void cacheMaintainance() {
        long currentSize = 0L;
        HashMap<String, Long> timeMap = new HashMap<>();

        for (SoftReference<BufferedFile> sortReference : map.values()) {
            BufferedFile bufferedFile = sortReference.get();

            if (bufferedFile != null) {
                currentSize += bufferedFile.getCurrentBufferedSizeRounded();
                timeMap.put(bufferedFile.getFile().toString(), bufferedFile.getLastCallTimeMS());
            }
        }

        while (currentSize > maxCacheBytes) {
            removeOldestEntry(timeMap);
        }
    }

    private synchronized void removeOldestEntry(HashMap<String, Long> timeMap) {
        int oldest = 0;
        long oldestTime = 0L;

        ArrayList<Map.Entry<String, Long>> times = new ArrayList<>(timeMap.entrySet());

        for (int i = 0; i < times.size(); i++) {
            if (i == 0) {
                oldestTime = times.get(0).getValue();
            } else if (times.get(i).getValue() < oldestTime) {
                oldestTime = times.get(i).getValue();
                oldest = i;
            }
        }

        map.remove(times.get(oldest).getKey());
    }
}
