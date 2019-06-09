package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.prefs.Preferences;

public class SoundboardModel extends Observable {

    static final String settingsLocation = "EXP-soundboard";

    private AudioMaster audio;
    private List<Entry> entryList;
    private Preferences settings;

    public SoundboardModel(int speakerCount) {
        audio = new AudioMaster(speakerCount);
        entryList = new ArrayList<Entry>();

        if (settings.nodeExists(settingsLocation)) {
            settings = Preferences.userRoot().node(settingsLocation);
        } else {
            settings = Preferences.userRoot().node(settingsLocation);
            initializeSettings();
        }
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

    public void initializeSettings() {
        settings.putByteArray("stopallsounds", );
    }

    public class SoundboardSettings {

    }

}
