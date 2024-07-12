package ca.exp.soundboard.rewrite.soundboard;

import ca.exp.soundboard.rewrite.gui.SettingsFrame;
import ca.exp.soundboard.rewrite.gui.SoundboardFrame;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Utils {

    public static final int BUFFER_SIZE = 2048;
    public static final float STANDARD_SAMPLE_RATE = 44100.0F;

    public static final float MODIFIED_SPEED_INCREMENT = 0.05F;
    public static final float MODIFIED_SPEED_DECREMENT = 0.05F;
    public static final float MINIMUM_MODIFIED_SPEED = 0.1F;
    public static final float MAXIMUM_MODIFIED_SPEED = 2.0F;

    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNEL_COUNT = 2;
    private static final int FRAME_SIZE = 4;

    private static final InputStream loaderfile = ClipPlayer.class.getResourceAsStream("loader.mp3");

    public static final String PREFERENCES_NODE_NAME = "Expenosa's SoundboardStage";

    public static final Preferences PREFERENCES = Preferences.userRoot().node(PREFERENCES_NODE_NAME);

    public static final AudioFormat AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);

    private static final String GLOBAL_HOTKEY_ERROR = "Error occured whilst initiating global hotkeys";

    private static final String ALERT_MESSAGE = "Alert!";
    private static final String UNSUPPORTED_FORMAT_MESSAGE = "Unsupported Format";
    private static final String PUSH_TO_TALK_KEY_CONFLICT = "A soundboard entry is using a key that conflicts with a 'Push to Talk' key. \n Disable 'Auto-hold PTT keys', or edit the entry or PTT keys.";

    public static AudioFormat modifiedPlaybackFormat;

    public static int stopKey = 19;
    public static int slowKey = 35;
    public static int modspeedupKey = 39;
    public static int modspeeddownKey = 37;
    private static int overlapSwitchKey = 36;

    public static boolean overlapSameClipWhilePlaying = true;
    public static boolean autoPTThold = true;
    private static boolean playAll = true;

    public static MicInjector micInjector = new MicInjector();
    public static String fileEncoding = System.getProperty("file.encoding");
    private static ThreadGroup clipPlayerThreadGroup = new ThreadGroup("Clip Player Group");
    private static float modifiedPlaybackSpeed;
    private static Robot robot;
    private static ArrayList<Integer> pttKeys = new ArrayList<>();
    private static int currentlyPlayingClipCount = 0;

    private static ConcurrentHashMap<String, Long> lastNativeKeyPressMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> lastRobotKeyPressMap = new ConcurrentHashMap<>();

    public static void playNewSoundClipThreaded(File file, final SourceDataLine primarySpeaker,
            final SourceDataLine secondarySpeaker) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Utils.ClipPlayer clipPlayer = new Utils.ClipPlayer(file, primarySpeaker, secondarySpeaker);

                if (!Utils.overlapSameClipWhilePlaying) {
                    Utils.stopFilePlaying(file);
                }

                clipPlayer.start();
            }
        });
    }

    public static String[] getMixerNames(DataLine.Info lineInfo) {
        ArrayList<String> mixerNames = new ArrayList<>();
        Mixer.Info[] mixerInfoArray = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : mixerInfoArray) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);

            try {
                if (mixer.isLineSupported(lineInfo)) {
                    mixerNames.add(mixerInfo.getName());
                }
            } catch (NullPointerException exception) {
                System.err.println(exception);
            }
        }

        String[] returnArray = new String[mixerNames.size()];
        return mixerNames.toArray(returnArray);
    }

    public static void stopAllClips() {
        playAll = false;
        zeroCurrentClipCount();
    }

    public static int getStopKey() {
        return stopKey;
    }

    public static void setStopKey(int stopKey) {
        Utils.stopKey = stopKey;
    }

    public static int getModifiedSpeedKey() {
        return slowKey;
    }

    public static void setModifiedSpeedKey(int slowKey) {
        Utils.slowKey = slowKey;
    }

    public static void startMicInjector(String inputMixerName, String outputMixerName) {
        boolean inputExists = false;
        boolean outputExists = false;

        if (isMicInjectorRunning()) {
            stopMicInjector();
        }

        for (String mixer : MicInjector.getMixerNames(MicInjector.targetDataLineInfo)) {
            if (mixer.equals(inputMixerName)) {
                inputExists = true;
            }
        }

        for (String mixer : MicInjector.getMixerNames(MicInjector.sourceDataLineInfo)) {
            if (mixer.equals(outputMixerName)) {
                outputExists = true;
            }
        }

        if (inputExists && outputExists) {
            micInjector.setInputMixer(inputMixerName);
            micInjector.setOutputMixer(outputMixerName);
            micInjector.start();
        }
    }

    public static void stopMicInjector() {
        micInjector.stopRunning();
    }

    public static float getMicInjectorGain() {
        return MicInjector.getGain();
    }

    public static void setMicInjectorGain(float level) {
        micInjector.setGain(level);
    }

    public static boolean isMicInjectorRunning() {
        return micInjector.isRunning();
    }

    public static void startMp3Decoder() {
        try {
            AudioSystem.getAudioFileFormat(loaderfile);
            AudioInputStream stream = AudioSystem.getAudioInputStream(loaderfile);
            stream.close();
        } catch (UnsupportedAudioFileException | IOException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean initGlobalKeyLibrary() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException exception) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(exception.getMessage());
            JOptionPane.showMessageDialog(null, exception.getMessage(), GLOBAL_HOTKEY_ERROR, 0);
        }

        return true;
    }

    public static boolean deregisterGlobalKeyLibrary() {
        if (GlobalScreen.isNativeHookRegistered()) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException exception) {
                exception.printStackTrace();
            }

            return true;
        }

        return false;
    }

    public static boolean isFileSupported(File file) {
        try {
            AudioSystem.getAudioFileFormat(file);
            return true;
        } catch (UnsupportedAudioFileException | IOException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public static synchronized float getModifiedPlaybackSpeed() {
        return modifiedPlaybackSpeed;
    }

    public static synchronized void setModifiedPlaybackSpeed(float speed) {
        modifiedPlaybackSpeed = speed;

        float newSampleRate = STANDARD_SAMPLE_RATE * speed;
        modifiedPlaybackFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, newSampleRate, SAMPLE_SIZE_IN_BITS, CHANNEL_COUNT, FRAME_SIZE, newSampleRate, false);
    }

    public static int getModspeedupKey() {
        return modspeedupKey;
    }

    public static void setModspeedupKey(int modspeedupKey) {
        Utils.modspeedupKey = modspeedupKey;
    }

    public static int getModspeeddownKey() {
        return modspeeddownKey;
    }

    public static void setModspeeddownKey(int modspeeddownKey) {
        Utils.modspeeddownKey = modspeeddownKey;
    }

    public static int getOverlapSwitchKey() {
        return overlapSwitchKey;
    }

    public static void setOverlapSwitchKey(int overlapSwitchKey) {
        Utils.overlapSwitchKey = overlapSwitchKey;
    }

    public static void incrementModSpeedUp() {
        float speed = modifiedPlaybackSpeed + MODIFIED_SPEED_INCREMENT;

        if (speed > MAXIMUM_MODIFIED_SPEED) {
            speed = MAXIMUM_MODIFIED_SPEED;
        }

        setModifiedPlaybackSpeed(speed);

        if (SettingsFrame.instance != null) {
            SettingsFrame.instance.updateDisplayedModSpeed();
        }
    }

    public static void decrementModSpeedDown() {
        float speed = modifiedPlaybackSpeed - MODIFIED_SPEED_DECREMENT;

        if (speed < MINIMUM_MODIFIED_SPEED) {
            speed = MINIMUM_MODIFIED_SPEED;
        }

        setModifiedPlaybackSpeed(speed);

        if (SettingsFrame.instance != null) {
            SettingsFrame.instance.updateDisplayedModSpeed();
        }
    }

    public static JFileChooser getFileChooser() {
        return SoundboardFrame.filechooser;
    }

    public static Robot getRobotInstance() {
        if (robot != null) {
            return robot;
        }

        try {
            robot = new Robot();
        } catch (AWTException exception) {
            exception.printStackTrace();
        }

        if (robot != null) {
            return robot;
        }

        return null;
    }

    public static boolean checkAndUseAutoPPThold() {
        if (!autoPTThold || pttKeys.size() == 0) {
            return false;
        }

        if (SoundboardFrame.soundboard.entriesContainPTTKeys(pttKeys)) {
            return false;
        }

        ArrayList<Integer> pressed = SoundboardFrame.macroListener.getPressedNativeKeys();

        Robot robot = getRobotInstance();

        for (int key : pttKeys) {
            boolean pressedAlready = false;

            for (int nativekey : pressed) {
                if (KeyEventIntConverter.getKeyEventText(key).toLowerCase().equals(NativeKeyEvent.getKeyText(nativekey).toLowerCase())) {
                    pressedAlready = true;
                    break;
                }
            }

            if (!pressedAlready) {
                robot.keyPress(key);
                submitRobotKeyPressTime(KeyEventIntConverter.getKeyEventText(key));
                System.out.println("Robot pressed: " + KeyEvent.getKeyText(key));
            }
        }

        return true;
    }

    public static boolean checkAndReleaseHeldPPTKeys() {
        if (!autoPTThold) {
            return false;
        }

        if (SoundboardFrame.soundboard.entriesContainPTTKeys(pttKeys)) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(
                            null,
                            PUSH_TO_TALK_KEY_CONFLICT,
                            ALERT_MESSAGE,
                            0
                    );
                }
            });

            return false;
        }

        if (currentlyPlayingClipCount == 0) {
            Robot robot = getRobotInstance();

            for (int key : pttKeys) {
                if (wasKeyLastPressedByRobot(KeyEventIntConverter.getKeyEventText(key))) {
                    robot.keyRelease(key);
                    System.out.println("Robot released: " + KeyEvent.getKeyText(key));
                }
            }
        }

        return true;
    }

    public static ArrayList<Integer> getPTTkeys() {
        return pttKeys;
    }

    public static void setPTTkeys(Collection<Integer> pTTkeys) {
        pttKeys = new ArrayList<Integer>(pTTkeys);
    }

    public static boolean isAutoPTThold() {
        return autoPTThold;
    }

    public static void setAutoPTThold(boolean autoPTThold) {
        Utils.autoPTThold = autoPTThold;
    }

    public static synchronized void incrementCurrentClipCount() {
        currentlyPlayingClipCount += 1;
    }

    public static synchronized void decrementCurrentClipCount() {
        if (currentlyPlayingClipCount >= 1) {
            currentlyPlayingClipCount -= 1;
        }
    }

    public static synchronized void zeroCurrentClipCount() {
        currentlyPlayingClipCount = 0;
    }

    public static ArrayList<Integer> stringToIntArrayList(String string) {
        ArrayList<Integer> output = new ArrayList<Integer>();

        String[] integerStringArray = string.replace('[', ' ').replace(']', ' ').trim().split(",");

        for (String integerString : integerStringArray) {
            String trimmedIntegerString = integerString.trim();

            if (!trimmedIntegerString.equals("")) {
                int integer = Integer.parseInt(trimmedIntegerString);
                output.add(integer);
            }
        }

        return output;
    }

    public static void submitNativeKeyPressTime(String key, long time) {
        lastNativeKeyPressMap.put(key.toLowerCase(), time);
    }

    public static void submitRobotKeyPressTime(String key) {
        long time = System.currentTimeMillis();

        lastNativeKeyPressMap.put(key.toLowerCase(), time);
        lastRobotKeyPressMap.put(key.toLowerCase(), time);
    }

    public static long getLastNativeKeyPressTimeForKey(String keyname) {
        Long time = lastNativeKeyPressMap.get(keyname.toLowerCase());

        if (time == null) {
            return 0L;
        }

        return time.longValue();
    }

    public static long getLastRobotKeyPressTimeForKey(String keyname) {
        Long time = lastRobotKeyPressMap.get(keyname.toLowerCase());

        if (time == null) {
            return 0L;
        }

        return time.longValue();
    }

    public static boolean wasKeyLastPressedByRobot(String keyname) {
        long human = getLastNativeKeyPressTimeForKey(keyname);
        long robot = getLastRobotKeyPressTimeForKey(keyname);

        return robot == human;
    }

    public static boolean isOverlapSameClipWhilePlaying() {
        return overlapSameClipWhilePlaying;
    }

    public static void setOverlapSameClipWhilePlaying(boolean overlap) {
        overlapSameClipWhilePlaying = overlap;

        if (SettingsFrame.instance != null) {
            SettingsFrame.instance.updateOverlapSwitchCheckBox();
        }
    }

    public static boolean stopFilePlaying(File file) {
        boolean stopped = false;
        String filepath = file.toString();

        Thread[] threads = new Thread[clipPlayerThreadGroup.activeCount()];
        clipPlayerThreadGroup.enumerate(threads);

        System.out.println("Thread count: " + threads.length);
        System.out.println("Thread groups: " + clipPlayerThreadGroup.activeGroupCount());
        System.out.println("Requesting: " + filepath + " to stop");

        for (Thread thread : threads) {
            System.out.println("thread name: " + thread.getName());

            if (filepath.equals(thread.getName())) {
                ClipPlayer clipPlayer = (ClipPlayer) thread;
                clipPlayer.stopPlaying();
                stopped = true;
            }
        }

        return stopped;
    }

    public MicInjector getMicInjector() {
        return micInjector;
    }

    private static class ClipPlayer extends Thread {
        File file;

        SourceDataLine primarySpeaker = null;
        SourceDataLine secondarySpeaker = null;
        boolean playing = true;

        public ClipPlayer(File file, SourceDataLine primarySpeaker, SourceDataLine secondarySpeaker) {
            super(file.toString());

            this.file = file;
            this.primarySpeaker = primarySpeaker;
            this.secondarySpeaker = secondarySpeaker;
        }

        public void run() {
            playSoundClip(file, primarySpeaker, secondarySpeaker);
        }

        public void stopPlaying() {
            System.out.println("Stopping clip: " + file.getName());

            playing = false;
        }

        private void playSoundClip(File file, SourceDataLine primarySpeaker, SourceDataLine secondarySpeaker) {
            Utils.playAll = true;

            AudioInputStream clip = null;
            AudioFormat clipFormat = null;

            try {
                clip = AudioSystem.getAudioInputStream(file);
                clipFormat = clip.getFormat();

                if (!clipFormat.equals(Utils.AUDIO_FORMAT)) {
                    clip = AudioSystem.getAudioInputStream(Utils.AUDIO_FORMAT, clip);
                }
            } catch (UnsupportedAudioFileException exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, file.getName() + " uses an unsupported format.", UNSUPPORTED_FORMAT_MESSAGE, 0);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            if (clip != null) {
                Utils.incrementCurrentClipCount();

                byte[] buffer = new byte[2048]; // TODO: fix character
                int bytesRead = 0;

                while (playing && Utils.playAll) {
                    try {
                        bytesRead = clip.read(buffer, 0, 2048);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    Utils.checkAndUseAutoPPThold();

                    if (bytesRead > 0) {
                        primarySpeaker.write(buffer, 0, bytesRead);

                        if (secondarySpeaker != null) {
                            secondarySpeaker.write(buffer, 0, bytesRead);
                        }
                    }

                    if (bytesRead < 2048) {
                        playing = false;
                    }
                }

                Utils.decrementCurrentClipCount();
                Utils.checkAndReleaseHeldPPTKeys();
            }

            if (clip != null) {
                try {
                    clip.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            primarySpeaker.close();

            if (secondarySpeaker != null) {
                secondarySpeaker.close();
            }
        }
    }
}
