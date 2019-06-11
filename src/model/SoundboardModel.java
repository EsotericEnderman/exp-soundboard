package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SoundboardModel {

    static final String settingsLocation = "EXP-soundboard";

    private AudioMaster audio;
    private ObservableList<Entry> entryList;
    private Preferences settings;
    private Logger logger;

    public SoundboardModel(int speakerCount) {
        audio = new AudioMaster(speakerCount);
        entryList = FXCollections.observableArrayList();
        settings = Preferences.userRoot().node(settingsLocation);
        logger = Logger.getLogger(this.getClass().getName());

        logger.log(Level.INFO, "Initialized model with " + speakerCount + " outputs");
    }

    public AudioMaster getAudio() {
        return audio;
    }

    public Entry[] getEntriesArr() {
        return (Entry[]) entryList.toArray();
    }

    public ObservableList<Entry> getEntries() {
        return entryList;
    }

    public class SoundboardSettings {

    }

}
