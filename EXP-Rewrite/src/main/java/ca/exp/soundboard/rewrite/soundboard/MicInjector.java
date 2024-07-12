package ca.exp.soundboard.rewrite.soundboard;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JOptionPane;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MicInjector extends Thread {

    private static float frameRate = 44100.0F;

    private static final AudioFormat signedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, frameRate, 16, 2, 4, frameRate, false);
    public static final DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, signedFormat, 8192);
    public static final DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, signedFormat, 8192);

    private static final String LINE_UNAVAILABLE_EXCEPTION_MESSAGE = "Line Unavailable Exception";

    private static float gainLevel;
    private final byte[] inputBuffer;
    Mixer inputMixer;
    Mixer outputMixer;
    FloatControl gainControl;
    int userVolume;
    private String inputLineName;
    private String outputLineName;
    private SourceDataLine sourceDataLine;
    private TargetDataLine targetDataLine;
    private int bytesRead;
    private boolean bypass;
    private boolean muted = false;
    private boolean run = false;
    private long nextDrift;

    MicInjector() {
        inputBuffer = new byte[512]; // TODO: fix character
        inputLineName = "none selected";
        outputLineName = "none selected";
    }

    public static synchronized float getGain() {
        return gainLevel;
    }

    public synchronized void setGain(float level) {
        gainLevel = level;
        if (gainControl != null) {
            gainControl.setValue(level);
        }
    }

    public static String[] getMixerNames(DataLine.Info lineInfo) {
        ArrayList<String> mixerNames = new ArrayList<String>();
        Mixer.Info[] info = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : info) {
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

    public static float getdB(byte[] buffer) {
        double dB = 0.0D;
        short[] shortArray = new short[buffer.length / 2];
        ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortArray);

        for (int i = 0; i < shortArray.length; i++) {
            dB = 20.0D * Math.log10(Math.abs(shortArray[i] / 32767.0D));
            if ((dB == Double.NEGATIVE_INFINITY) || (dB == Double.NaN)) { // (dB == NaN.0D)) { // TODO: fix this value
                dB = -90.0D;
            }
        }

        float level = (float) dB + 91.0F;
        return level;
    }

    @Deprecated
    public static short[] byteToShortArray(byte[] byteArray) {
        short[] shortArray = new short[byteArray.length / 2];
        for (int i = 0; i < shortArray.length; i++) {
            int byteA = byteArray[(i * 2 + 0)] & 0xFF;
            int byteB = byteArray[(i * 2 + 1)] & 0xFF;

            shortArray[i] = ((short) ((byteB << 8) + byteA));
        }
        return shortArray;
    }

    @Deprecated
    public static byte[] shortArrayToByteArray(short[] shortArray) {
        byte[] byteArray = new byte[shortArray.length * 2];
        ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortArray);
        return byteArray;
    }

    public synchronized void setInputMixer(String mixerName) {
        String[] mixers = getMixerNames(targetDataLineInfo);
        int mixerCount = mixers.length;

        for (int i = 0; i < mixerCount; i++) {
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerName.equals(mixerInfo.getName())) {
                    inputMixer = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }

    public synchronized void setOutputMixer(String mixerName) {
        String[] mixers = getMixerNames(sourceDataLineInfo);
        int mixerCount = mixers.length;
        for (int i = 0; i < mixerCount; i++) {
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerName.equals(mixerInfo.getName())) {
                    outputMixer = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }

    public synchronized void setupGate() {
        if (targetDataLine != null) {
            clearLines();
        }

        try {
            targetDataLine = ((TargetDataLine) inputMixer.getLine(targetDataLineInfo));
            inputLineName = inputMixer.getMixerInfo().getName();
            targetDataLine.open(signedFormat, 8192);
            targetDataLine.start();
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(null,
                    "Selected Input Line " + inputLineName + " is currently unavailable.",
                    LINE_UNAVAILABLE_EXCEPTION_MESSAGE, 0);
        }

        try {
            sourceDataLine = ((SourceDataLine) outputMixer.getLine(sourceDataLineInfo));
            outputLineName = outputMixer.getMixerInfo().getName();
            sourceDataLine.open(signedFormat, 8192);
            sourceDataLine.start();
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(null,
                    "Selected Output Line " + outputLineName + " is currently unavailable.",
                    LINE_UNAVAILABLE_EXCEPTION_MESSAGE, 0);
        }

        gainControl = ((FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN));
        gainControl.setValue(gainLevel);
        System.out.println(targetDataLine.getLineInfo().toString());
        System.out.println("Buffer size is " + targetDataLine.getBufferSize());
    }

    private synchronized void clearLines() {
        targetDataLine.close();
        sourceDataLine.close();
    }

    protected void read() {
        bytesRead = targetDataLine.read(inputBuffer, 0, 512);
    }

    protected void write() {
        sourceDataLine.write(inputBuffer, 0, bytesRead);
    }

    public void run() {
        setupGate();
        run = true;
        nextDrift = (System.currentTimeMillis() + 1800000L);
        while (run) {
            read();
            write();

            if (System.currentTimeMillis() > nextDrift) {
                driftReset();
            }
        }
    }

    public boolean isRunning() {
        return run;
    }

    synchronized void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    synchronized void setFadeOut(boolean fadeOut) {
    }

    synchronized void setMute(boolean mute) {
        muted = mute;
        if (muted) {
            bypass = false;
        }
    }

    boolean isMuted() {
        return muted;
    }

    public void resetGain() {
        gainControl.setValue(userVolume);
    }

    public boolean isBypassing() {
        return bypass;
    }

    public String getSelectedInputLineName() {
        return inputLineName;
    }

    public String getSelectedOutputLineName() {
        return outputLineName;
    }

    public void stopRunning() {
        run = false;
    }

    private synchronized void driftReset() {
        if (System.currentTimeMillis() > nextDrift) {
            nextDrift = (System.currentTimeMillis() + 1800000L);
            try {
                targetDataLine.open(signedFormat, 8192);
                targetDataLine.start();
            } catch (LineUnavailableException ex) {
                JOptionPane.showMessageDialog(null, "Selected Input Line is currently unavailable",
                        LINE_UNAVAILABLE_EXCEPTION_MESSAGE, 0);
            }
            try {
                sourceDataLine.open(signedFormat, 8192);
                sourceDataLine.start();
            } catch (LineUnavailableException ex) {
                JOptionPane.showMessageDialog(null, "Selected Output Line is currently unavailable.",
                        LINE_UNAVAILABLE_EXCEPTION_MESSAGE, 0);
            }

            System.out.println("DriftReset");
        }
    }
}
