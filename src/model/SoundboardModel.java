package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class SoundboardModel extends Observable {

    AudioMaster audio;
    List<Entry> entryList;

    public SoundboardModel(int speakerCount) {
        audio = new AudioMaster(speakerCount);
        entryList = new ArrayList<Entry>();
    }

    public AudioMaster getAudio() {
        return audio;
    }

    public Entry[] getEntriesArr() {
        return (Entry[]) entryList.toArray();
    }

    public List<Entry> getEntries() {
        return entryList;
    }

}
