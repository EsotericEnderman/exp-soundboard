package ca.exp.soundboard.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SoundboardModel {

    static Preferences userSettings = Preferences.userRoot().node("exp-soundboard");

    private AudioMaster audio;
    private ObservableList<Entry> entryList;
    private Logger logger;

    public SoundboardModel(int speakerCount) {
        audio = new AudioMaster(speakerCount);
        entryList = FXCollections.observableArrayList();
        logger = Logger.getLogger(this.getClass().getName());

        logger.log(Level.INFO, "Initialized ca.exp.soundboard.model with " + speakerCount + " outputs");
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
        // Stop all sounds hotkey
        // modified playback speed hotkey
        // modified playback speed multiplier
        // modifier speed increase key
        // modifier speed decrease key
        // VoIP ptt key
        // Overlap sound toggle hotkey
        // Mixer.Info for primary and secondary speakers
        // Mic Injector microphone and virtual cable
        // check for update on launch toggle
        // use mic injector toggle
        // auto-hold ptt keys
        // use second speaker toggle
        // primary, secondary, mic injector gains
        // converter output format
    }

}
