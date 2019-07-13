package ca.exp.soundboard.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class SoundboardModel {

    public static void saveToFile(SoundboardModel model, File target) throws IOException, BackingStoreException {
        FileOutputStream out = new FileOutputStream(target);
        model.modelPreferences.exportNode(out);
    }

    public static void loadFromFile(SoundboardModel model, File source) throws IOException, InvalidPreferencesFormatException {
        FileInputStream in = new FileInputStream(source);
        Preferences.importPreferences(in);
    }

    private AudioMaster audio;
    private ObservableList<Entry> entryList;
    private Preferences modelPreferences;
    private Logger logger;

    public SoundboardModel(int speakerCount) {
        audio = new AudioMaster(speakerCount);
        entryList = FXCollections.observableArrayList();
        logger = Logger.getLogger(this.getClass().getName());
        modelPreferences = Preferences.userRoot().node(this.getClass().getCanonicalName());

        logger.log(Level.INFO, "Initialized " + this.getClass().getName() + " with " + speakerCount + " outputs");
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

    public void saveToFile(File target) {

    }

    public void loadFromFile(File source) {

    }

}
