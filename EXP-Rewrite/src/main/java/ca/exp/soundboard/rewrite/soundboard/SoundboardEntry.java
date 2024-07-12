package ca.exp.soundboard.rewrite.soundboard;

import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class SoundboardEntry {

    public int[] activationKeysNumbers;
    private String filePath;

    public SoundboardEntry(File file, int[] keys) {
        Path page = Paths.get(file.getAbsolutePath());
        filePath = page.toAbsolutePath().toString();

        activationKeysNumbers = keys;
        if (activationKeysNumbers == null) {
            activationKeysNumbers = new int[0];
        }
    }

    public boolean matchesPressed(ArrayList<Integer> pressedKeys) {
        int keysRemaining = activationKeysNumbers.length;

        if (keysRemaining == 0) {
            return false;
        }

        for (int actkey : activationKeysNumbers) {
            for (Iterator<Integer> localIterator = pressedKeys.iterator(); localIterator.hasNext();) {
                int presskey = localIterator.next();

                if (actkey == presskey) {
                    keysRemaining--;
                }
            }
        }

        return keysRemaining <= 0;
    }

    public int matchesHowManyPressed(ArrayList<Integer> pressedKeys) {
        int matches = 0;

        for (int i = 0; i < pressedKeys.size(); i++) {
            int key = pressedKeys.get(i);
            int keyCount = activationKeysNumbers.length;

            if (i < keyCount) {
                int hotkey = activationKeysNumbers[i];

                if (key == hotkey) {
                    matches++;
                }
            }
        }

        return matches;
    }

    public void play(AudioManager audioManager, boolean moddedSpeed) {
        File file = toFile();
        audioManager.playSoundClip(file, moddedSpeed);
    }

    public File toFile() {
        File file = new File(filePath);

        if (!file.exists()) {
            Path path = Paths.get(filePath);
            return path.toFile();
        }

        return file;
    }

    public void setFile(File file) {
        try {
            filePath = new String(file.getAbsolutePath().getBytes(Utils.fileEncoding));
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        char separator = File.separatorChar;
        return filePath.substring(filePath.lastIndexOf(separator) + 1);
    }

    public int[] getActivationKeys() {
        return activationKeysNumbers;
    }

    public void setActivationKeys(int[] activationKeys) {
        activationKeysNumbers = activationKeys;
    }

    public String getActivationKeysAsReadableString() {
        String activationKey = "";

        if (activationKeysNumbers.length == 0) {
            return activationKey;
        }

        for (int integer : getActivationKeys()) {
            activationKey = activationKey.concat(NativeKeyEvent.getKeyText(integer) + "+");
        }

        activationKey = activationKey.substring(0, activationKey.length() - 1);
        return activationKey;
    }
}
