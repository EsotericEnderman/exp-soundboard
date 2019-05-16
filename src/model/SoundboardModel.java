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

    public Entry[] getEntries() {
        return (Entry[]) entryList.toArray();
    }

    public boolean addEntry(Entry entry) {
        notifyObservers();
        return entryList.add(entry);
    }

    public Entry removeEntry(int index) {
        notifyObservers();
        return entryList.remove(index);
    }

    public boolean removeEntry(Entry entry) {
        notifyObservers();
        return entryList.remove(entry);
    }


}
