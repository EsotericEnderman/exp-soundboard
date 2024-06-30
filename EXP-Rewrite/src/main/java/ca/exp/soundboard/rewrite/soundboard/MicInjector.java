package ca.exp.soundboard.rewrite.soundboard;

import javax.sound.sampled.*;
import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class MicInjector extends Thread {
    private static float fFrameRate = 44100.0F;
    private static final AudioFormat signedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, fFrameRate, 16, 2,
            4, fFrameRate, false);
    public static final DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, signedFormat, 8192);
    public static final DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, signedFormat, 8192);
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
        this.inputBuffer = new byte[512]; // TODO: fix character
        this.inputLineName = "none selected";
        this.outputLineName = "none selected";
    }

    public static synchronized float getGain() {
        return gainLevel;
    }

    public synchronized void setGain(float level) {
        gainLevel = level;
        if (this.gainControl != null) {
            this.gainControl.setValue(level);
        }
    }

    public static String[] getMixerNames(DataLine.Info lineInfo) {
        ArrayList<String> mixerNames = new ArrayList<String>();
        Mixer.Info[] info = AudioSystem.getMixerInfo();
        Mixer.Info[] arrayOfInfo1;
        int j = (arrayOfInfo1 = info).length;
        for (int i = 0; i < j; i++) {
            Mixer.Info elem = arrayOfInfo1[i];
            Mixer mixer = AudioSystem.getMixer(elem);
            try {
                if (mixer.isLineSupported(lineInfo)) {
                    mixerNames.add(elem.getName());
                }
            } catch (NullPointerException e) {
                System.err.println(e);
            }
        }
        String[] returnarray = new String[mixerNames.size()];
        return mixerNames.toArray(returnarray);
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
            int ub1 = byteArray[(i * 2 + 0)] & 0xFF;
            int ub2 = byteArray[(i * 2 + 1)] & 0xFF;
            shortArray[i] = ((short) ((ub2 << 8) + ub1));
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
        String[] arrayOfString1;
        int j = (arrayOfString1 = mixers).length;
        for (int i = 0; i < j; i++) {
            Mixer.Info[] arrayOfInfo;
            int m = (arrayOfInfo = AudioSystem.getMixerInfo()).length;
            for (int k = 0; k < m; k++) {
                Mixer.Info mixerInfo = arrayOfInfo[k];
                if (mixerName.equals(mixerInfo.getName())) {
                    this.inputMixer = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }

    public synchronized void setOutputMixer(String mixerName) {
        String[] mixers = getMixerNames(sourceDataLineInfo);
        String[] arrayOfString1;
        int j = (arrayOfString1 = mixers).length;
        for (int i = 0; i < j; i++) {
            Mixer.Info[] arrayOfInfo;
            int m = (arrayOfInfo = AudioSystem.getMixerInfo()).length;
            for (int k = 0; k < m; k++) {
                Mixer.Info mixerInfo = arrayOfInfo[k];
                if (mixerName.equals(mixerInfo.getName())) {
                    this.outputMixer = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }

    public synchronized void setupGate() {
        if (this.targetDataLine != null) {
            clearLines();
        }
        try {
            this.targetDataLine = ((TargetDataLine) this.inputMixer.getLine(targetDataLineInfo));
            this.inputLineName = this.inputMixer.getMixerInfo().getName();
            this.targetDataLine.open(signedFormat, 8192);
            this.targetDataLine.start();
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(null,
                    "Selected Input Line " + this.inputLineName + " is currently unavailable.",
                    "Line Unavailable Exception", 0);
        }
        try {
            this.sourceDataLine = ((SourceDataLine) this.outputMixer.getLine(sourceDataLineInfo));
            this.outputLineName = this.outputMixer.getMixerInfo().getName();
            this.sourceDataLine.open(signedFormat, 8192);
            this.sourceDataLine.start();
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(null,
                    "Selected Output Line " + this.outputLineName + " is currently unavailable.",
                    "Line Unavailable Exception", 0);
        }
        this.gainControl = ((FloatControl) this.sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN));
        this.gainControl.setValue(gainLevel);
        System.out.println(this.targetDataLine.getLineInfo().toString());
        System.out.println("Buffer size is " + this.targetDataLine.getBufferSize());
    }

    private synchronized void clearLines() {
        this.targetDataLine.close();
        this.sourceDataLine.close();
    }

    protected void read() {
        this.bytesRead = this.targetDataLine.read(this.inputBuffer, 0, 512);
    }

    protected void write() {
        this.sourceDataLine.write(this.inputBuffer, 0, this.bytesRead);
    }

    public void run() {
        setupGate();
        this.run = true;
        this.nextDrift = (System.currentTimeMillis() + 1800000L);
        while (this.run) {
            read();
            write();

            if (System.currentTimeMillis() > this.nextDrift) {
                driftReset();
            }
        }
    }

    public boolean isRunning() {
        return this.run;
    }

    synchronized void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    synchronized void setFadeOut(boolean fadeOut) {
    }

    synchronized void setMute(boolean mute) {
        this.muted = mute;
        if (this.muted) {
            this.bypass = false;
        }
    }

    boolean isMuted() {
        return this.muted;
    }

    public void resetGain() {
        this.gainControl.setValue(this.userVolume);
    }

    public boolean isBypassing() {
        return this.bypass;
    }

    public String getSelectedInputLineName() {
        return this.inputLineName;
    }

    public String getSelectedOutputLineName() {
        return this.outputLineName;
    }

    public void stopRunning() {
        this.run = false;
    }

    private synchronized void driftReset() {
        if (System.currentTimeMillis() > this.nextDrift) {
            this.nextDrift = (System.currentTimeMillis() + 1800000L);
            try {
                this.targetDataLine.open(signedFormat, 8192);
                this.targetDataLine.start();
            } catch (LineUnavailableException ex) {
                JOptionPane.showMessageDialog(null, "Selected Input Line is currently unavailable",
                        "Line Unavailable Exception", 0);
            }
            try {
                this.sourceDataLine.open(signedFormat, 8192);
                this.sourceDataLine.start();
            } catch (LineUnavailableException ex) {
                JOptionPane.showMessageDialog(null, "Selected Output Line is currently unavailable.",
                        "Line Unavailable Exception", 0);
            }
            System.out.println("DriftReset");
        }
    }
}
