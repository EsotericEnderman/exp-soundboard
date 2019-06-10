package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoundPlayer implements Runnable {

    private AudioMaster master;
    private Logger logger;

    private File sound;
    private SourceDataLine[] outputs;
    private AudioInputStream clip;
    private AudioFormat clipFormat;

    public AtomicBoolean running;

    public SoundPlayer(AudioMaster master, File sound, SourceDataLine... outputs)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.master = master;
        this.sound = sound;
        this.outputs = outputs;
        logger = Logger.getLogger(SoundPlayer.class.getName());
        running = new AtomicBoolean(true);

        // grab formats from file
        clip = AudioSystem.getAudioInputStream(sound);
        clipFormat = clip.getFormat();

        // in case the target clip is of a different format than is used for playing
        if (clipFormat.equals(AudioMaster.standardFormat)) {
            clip = AudioSystem.getAudioInputStream(AudioMaster.standardFormat, clip);
            logger.log(Level.INFO, "Target file: " + sound.getName() + " is using converted format");
        }

        // ready outputs for data
        for (SourceDataLine sdl : outputs) {
            sdl.open(clipFormat);
            sdl.start();
        }

        logger.log(Level.INFO, "Initialized sound player on: " + sound.getName());
    }

    @Override
    public void run() {
        // setup buffer for containing samples of audio to be played
        byte[] buffer = new byte[AudioMaster.standardBufferSize];
        int bytesRead = 0;
        int bytesWritten = 0;

        while (running.get()) { // TODO: update writing sound

            // read a sample from the audio file
            try {
                bytesRead = clip.read(buffer, 0, AudioMaster.standardBufferSize);
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Failed to read from file:" + sound.getName(), ioe);
                running.set(false);
            }

            // if anything was read
            if (bytesRead > 0) {

                // write the sample to all outputs
                for (SourceDataLine sdl : outputs) {
                    bytesWritten = sdl.write(buffer, 0, AudioMaster.standardBufferSize);
                }
            }

            // once there is nothing left to write
            if (bytesRead < AudioMaster.standardBufferSize) {
                running.set(false);
            }
        }

        // close remaining streams
        try {
            clip.close();
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Failed to close stream from file: " + sound.getName(), ioe);
        }

        for (SourceDataLine sdl : outputs) {
            sdl.close();
        }

        // remove self from active list
        master.removePlayer(this);
    }

}
