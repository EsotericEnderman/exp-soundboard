package ca.exp.soundboard.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SoundPlayer implements Runnable {

    private AudioMaster master;
    private Logger logger;

    private File sound;
    private SourceDataLine output;
    private AudioInputStream clip;
    private AudioFormat clipFormat;
    private final String name;
    public final int index;

    public final AtomicReference<PlayerState> state;
    private boolean running;

    public SoundPlayer(AudioMaster master, File sound, SourceDataLine output, int index)
            throws UnsupportedAudioFileException, IOException {
        this.master = master;
        this.sound = sound;
        this.output = output;
        this.index = index;
        logger = Logger.getLogger(this.getClass().getName());
        state = new AtomicReference<PlayerState>(PlayerState.RESETTING);
        name = Thread.currentThread().getName() + "/" + sound.getName();

        // grab formats from file
        try {
            clip = AudioSystem.getAudioInputStream(sound);
            clipFormat = clip.getFormat();
            if (!clipFormat.equals(AudioMaster.decodeFormat)) {
                clip = AudioSystem.getAudioInputStream(AudioMaster.decodeFormat, clip);
            }
        } catch (UnsupportedAudioFileException uafe) {
            logger.log(Level.INFO, "Target file: \"" + sound.getName() + "\" is unsupported");
            throw uafe;
        }

        logger.log(Level.INFO, "Initialized sound player on: \"" + sound.getName() + "\"");
    }

    public void run() {

        // setup buffer for containing samples of audio to be played
        byte[] buffer = new byte[output.getBufferSize()];
        int bytesRead = 0;
        int bytesWritten = 0;
        running = true;

        while (running) {
            switch (state.get()) {
                case RESETTING:
                    buffer = new byte[output.getBufferSize()];
                    bytesRead = 0;
                    bytesWritten = 0;
                    state.compareAndSet(PlayerState.RESETTING, PlayerState.READY);
                    break;
                case READY:
                    state.compareAndSet(PlayerState.READY, PlayerState.PLAYING);
                    logger.log(Level.INFO, "Starting playback");
                    /*try {
                        synchronized (state) {
                            state.wait();
                        }
                        state.compareAndSet(PlayerState.READY, PlayerState.PLAYING);
                        logger.log(Level.INFO, "Starting playback");
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to wait for starting signal", ie);
                    }*/
                    break;
                case PLAYING:
                    // read a sample from the audio file
                    try {
                        bytesRead = clip.read(buffer, 0, buffer.length);
                    } catch (IOException ioe) {
                        logger.log(Level.SEVERE, "Failed to read from file: \"" + sound.getName() + "\"", ioe);
                        state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED);
                    }

                    // if anything was read
                    if (bytesRead >= 0) {
                        // write the sample to the output
                        bytesWritten = output.write(buffer, 0, bytesRead);

                        if (bytesWritten < bytesRead) {
                            logger.log(Level.WARNING, "Line closed before stream was finished!");
                            state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED);
                            break;
                        }
                    } else {
                        // once there is nothing left to write
                        logger.log(Level.INFO, "Reached end of stream");
                        state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED);
                    }
                    break;
                case PAUSED:
                    try {
                        logger.log(Level.INFO, "Paused");
                        synchronized (state) {
                            state.wait();
                        }
                        logger.log(Level.INFO, "Unpaused");
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to pause thread: ", ie);
                        state.compareAndSet(PlayerState.PAUSED, PlayerState.FINISHED);
                    }
                    break;
                case WAIT:
                    try {
                        logger.log(Level.INFO, "Waiting for signal");
                        synchronized (state) {
                            state.compareAndSet(PlayerState.WAIT, PlayerState.WAITING);
                            state.wait();
                        }
                        logger.log(Level.INFO, "Finished waiting");
                        // waiting is done, continue playing
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to wait: ", ie);
                        state.compareAndSet(PlayerState.WAITING, PlayerState.FINISHED);
                    }
                    break;
                case WAITING:
                    try {
                        logger.log(Level.INFO, "Resuming wait for signal");
                        synchronized (state) {
                            state.wait();
                        }
                        logger.log(Level.INFO, "Finished waiting");
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to continue waiting: ", ie);
                        state.compareAndSet(PlayerState.WAITING, PlayerState.FINISHED);
                    }
                    break;
                case FINISHED:
                    // close remaining stream, clean buffer and release access to resource
                    try {
                        logger.log(Level.INFO, "Stopping and cleaning up");
                        clip.close();
                        output.drain();
                        output.flush();
                        output.close();
                        output.stop();
                        synchronized (state) {
                            state.wait();
                        }
                    } catch (IOException ioe) {
                        logger.log(Level.SEVERE, "Failed to close stream from file: \"" + sound.getName() + "\"", ioe);
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to wait for collection: ", ie);
                    }

                    running = false;
                    break;
            }
        }

        logger.log(Level.INFO, "Instance finished: \"" + sound.getName() + "\"");
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean updateGain(float gain) {
        if (state.get() == PlayerState.WAITING) {
            AudioMaster.getMasterGain(output).setValue(gain);
            return true;
        } else {
            return false;
        }
    }

}
