package ca.exp.soundboard.rewrite.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {
    private long maxCacheBytes;
    private ConcurrentHashMap<String, SoftReference<BufferedFile>> fMap;

    public MemoryCache(int maxMegabytes) {
        this.fMap = new ConcurrentHashMap<String, SoftReference<BufferedFile>>();
        this.maxCacheBytes = (maxMegabytes * 1000000);
    }

    public BufferedFile getBufferedFile(File file, int aBufferSize) {
        if (!this.fMap.containsKey(file.toString())) {
            BufferedFile bf = new BufferedFile(file, aBufferSize);
            SoftReference<BufferedFile> srbf = new SoftReference<BufferedFile>(bf);
            this.fMap.put(file.toString(), srbf);
            cacheMaintainance();
        }
        BufferedFile bf = this.fMap.get(file.toString()).get();
        if (bf != null) {
            return bf;
        }
        this.fMap.remove(file.toString());
        return getBufferedFile(file, aBufferSize);
    }

    private void cacheMaintainance() {
        long currentSize = 0L;
        HashMap<String, Long> timeMap = new HashMap<String, Long>();
        for (SoftReference<BufferedFile> sf : this.fMap.values()) {
            BufferedFile bf = null;
            if ((bf = sf.get()) != null) {
                currentSize += bf.getCurrentBufferedSizeRounded();
                timeMap.put(bf.getFile().toString(), Long.valueOf(bf.getLastCallTime()));
            }
        }
        while (currentSize > this.maxCacheBytes) {
            removeOldestEntry(timeMap);
        }
    }

    private synchronized void removeOldestEntry(HashMap<String, Long> aTimeMap) {
        int oldest = 0;
        long oldestTime = 0L;
        ArrayList<Map.Entry<String, Long>> times = new ArrayList<Map.Entry<String, Long>>(aTimeMap.entrySet());
        for (int i = 0; i < times.size(); i++) {
            if (i == 0) {
                oldestTime = times.get(0).getValue().longValue();
            } else if (times.get(i).getValue().longValue() < oldestTime) {
                oldestTime = times.get(i).getValue().longValue();
                oldest = i;
            }
        }

        this.fMap.remove(times.get(oldest).getKey());
    }
}
