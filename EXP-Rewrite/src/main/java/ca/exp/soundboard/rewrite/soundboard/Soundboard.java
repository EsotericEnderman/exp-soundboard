package ca.exp.soundboard.rewrite.soundboard;

import com.google.gson.Gson;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class Soundboard {

    private static ArrayList<SoundboardEntry> soundboardEntriesClone = new ArrayList<SoundboardEntry>();
    private static boolean containsPPTKey = false;
    private static ArrayList<Integer> pttKeysClone = new ArrayList<Integer>();

    private ArrayList<SoundboardEntry> soundboardEntries;

    public Soundboard() {
        soundboardEntries = new ArrayList<SoundboardEntry>();
    }

    public static Soundboard loadFromJsonFile(File file) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }

        Gson json = new Gson();

        Soundboard soundboard = json.fromJson(bufferedReader, Soundboard.class);
        try {
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return soundboard;
    }

    public Object[][] getEntriesAsObjectArrayForTable() {
        Object[][] array = new Object[soundboardEntries.size()][4];
        for (int i = 0; i < array.length; i++) {
            SoundboardEntry entry = soundboardEntries.get(i);
            array[i][0] = entry.getFileName();
            array[i][1] = entry.getActivationKeysAsReadableString();
            array[i][2] = entry.getFilePath();
            array[i][3] = i;
        }

        return array;
    }

    public void addEntry(File file, int[] keyNumbers) {
        soundboardEntries.add(new SoundboardEntry(file, keyNumbers));
    }

    public SoundboardEntry getEntry(String fileName) {
        for (SoundboardEntry entry : soundboardEntries) {
            if (entry.getFileName().equals(fileName)) {
                return entry;
            }
        }
        return null;
    }

    public void removeEntry(int index) {
        soundboardEntries.remove(index);
    }

    public void removeEntry(String fileName) {
        for (SoundboardEntry entry : soundboardEntries) {
            if (entry.getFileName().equals(fileName)) {
                soundboardEntries.remove(entry);
                break;
            }
        }
    }

    public ArrayList<SoundboardEntry> getSoundboardEntries() {
        return soundboardEntries;
    }

    public File saveAsJsonFile(File file) {
        String fileString = file.getAbsolutePath();
        System.out.println(fileString);

        if (fileString.contains(".")) {
            fileString = fileString.substring(0, fileString.lastIndexOf('.'));
        }

        fileString = fileString + ".json";
        System.out.println("amended: " + fileString);
        Gson gson = new Gson();

        String json = gson.toJson(this);
        File realfile = new File(fileString);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(realfile));
            writer.write(json);
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return realfile;
    }

    public SoundboardEntry getEntry(int index) {
        try {
            return soundboardEntries.get(index);
        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean entriesContainPTTKeys(ArrayList<Integer> pttkeys) {
        if ((!pttkeys.equals(pttKeysClone)) || (hasSoundboardChanged())) {
            soundboardEntriesClone = (ArrayList<SoundboardEntry>) soundboardEntries.clone();
            pttKeysClone = (ArrayList<Integer>) pttkeys.clone();
            String key = null;
            int i;

            for (SoundboardEntry entry : soundboardEntries) {
                int[] arrayOfInt;
                arrayOfInt = entry.getActivationKeys();
                i = 0;
                int actKey = arrayOfInt[i];
                key = NativeKeyEvent.getKeyText(actKey).toLowerCase();
                for (int number : pttkeys) {
                    if (key.equals(KeyEventIntConverter.getKeyEventText(number).toLowerCase())) {
                        containsPPTKey = true;
                        return true;
                    }
                }

                i++;
            }
            containsPPTKey = false;
            return false;
        }
        return containsPPTKey;
    }

    public boolean hasSoundboardChanged() {
        if (!soundboardEntries.equals(soundboardEntriesClone)) {
            System.out.println("SoundboardStage changed");
            return true;
        }
        return false;
    }
}
