package ca.exp.soundboard.rewrite.soundboard;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

public class AudioManager {

    private static final int STANDARD_DATA_LINE_INFO_BUFFER_SIZE = 2048;

    private static final int SECONDARY_SPEAKER_BUFFER_SIZE = 8192;

    private static final String PRIMARY_SPEAKER_UNAVAILABLE_MESSAGE = "Selected Output Line: Primary Speaker is currently unavailable.";
    private static final String SECONDARY_SPEAKER_UNAVAILABLE_MESSAGE = "Selected Output Line: Secondary Speaker is currently unavailable.";

    private static final String LINE_UNAVAILABLE_EXCEPTION = "Line Unavailable Exception";

    private static float firstOutputGain;
    private static float secondOutputGain;

    public final DataLine.Info standardDataLineInfo;

    Mixer primaryOutput;
    Mixer secondaryOutput;

    private boolean useSecondary = false;

    public AudioManager() {
        standardDataLineInfo = new DataLine.Info(SourceDataLine.class, Utils.AUDIO_FORMAT, STANDARD_DATA_LINE_INFO_BUFFER_SIZE);
    }

    public static float getFirstOutputGain() {
        return firstOutputGain;
    }

    public static void setFirstOutputGain(float gain) {
        firstOutputGain = gain;
    }

    public static float getSecondOutputGain() {
        return secondOutputGain;
    }

    public static void setSecondOutputGain(float gain) {
        secondOutputGain = gain;
    }

    void playSoundClip(File file, boolean halfSpeed) {
        AudioFormat format;

        if (halfSpeed) {
            format = Utils.modifiedPlaybackFormat;
        } else {
            format = Utils.AUDIO_FORMAT;
        }

        if (file.exists() && file.canRead()) {
            SourceDataLine primarySpeaker = null;
            SourceDataLine secondarySpeaker = null;

            try {
                primarySpeaker = (SourceDataLine) primaryOutput.getLine(standardDataLineInfo);
                primarySpeaker.open(format, 8192);

                FloatControl gain = (FloatControl) primarySpeaker.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(firstOutputGain);

                primarySpeaker.start();
            } catch (LineUnavailableException exception) {
                JOptionPane.showMessageDialog(null, PRIMARY_SPEAKER_UNAVAILABLE_MESSAGE, LINE_UNAVAILABLE_EXCEPTION,
                        JOptionPane.OK_OPTION);
            }

            if (secondaryOutput != null && useSecondary) {
                try {
                    secondarySpeaker = (SourceDataLine) secondaryOutput.getLine(standardDataLineInfo);
                    secondarySpeaker.open(format, SECONDARY_SPEAKER_BUFFER_SIZE);

                    FloatControl gain = (FloatControl) secondarySpeaker.getControl(FloatControl.Type.MASTER_GAIN);
                    gain.setValue(secondOutputGain);

                    secondarySpeaker.start();
                } catch (LineUnavailableException exception) {
                    JOptionPane.showMessageDialog(null, SECONDARY_SPEAKER_UNAVAILABLE_MESSAGE,
                            LINE_UNAVAILABLE_EXCEPTION, JOptionPane.OK_OPTION);
                }
            }

            Utils.playNewSoundClipThreaded(file, primarySpeaker, secondarySpeaker);
        }
    }

    public synchronized void setPrimaryOutputMixer(String mixerName) {
        String[] mixers = Utils.getMixerNames(standardDataLineInfo);
        int mixerCount = mixers.length;
        for (int i = 0; i < mixerCount; i++) {
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerName.equals(mixerInfo.getName())) {
                    primaryOutput = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }

    public void setUseSecondary(boolean use) {
        useSecondary = use;
    }

    public boolean useSecondary() {
        return useSecondary;
    }

    public synchronized void setSecondaryOutputMixer(String mixerName) {
        String[] mixers = Utils.getMixerNames(standardDataLineInfo);
        int mixerCount = mixers.length;
        for (int i = 0; i < mixerCount; i++) {
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerName.equals(mixerInfo.getName())) {
                    secondaryOutput = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }
}
