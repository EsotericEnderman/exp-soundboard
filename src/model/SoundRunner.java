package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class SoundRunner implements Runnable {

    private AudioMaster master;
    private File sound;
    private SourceDataLine[] speakers;
    private AudioInputStream clip;
    private AudioFormat clipFormat;
    private Logger logger;

    private boolean playing;

    /**
     *
     * @param master The audio controlling backend this thread was spawned from.
     * @param sound The file that contains audio this thread will play.
     * @param speakers An array of outputs this thread will write audio data into.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public SoundRunner(AudioMaster master, File sound, SourceDataLine... speakers)
            throws UnsupportedAudioFileException, IOException {
        this.master = master;
        this.sound = sound;
        this.speakers = speakers;

        clip = AudioSystem.getAudioInputStream(sound);
        clipFormat = clip.getFormat();

        // in case the target clip is of a different format than is used for playing
        if (clipFormat.equals(AudioMaster.standardFormat)) {
            clip = AudioSystem.getAudioInputStream(AudioMaster.standardFormat, clip);
            logger.log(Level.INFO, "Target file: " + sound.getName() + " is using converted format");
        }

        logger = Logger.getLogger(SoundRunner.class.getName());
        logger.log(Level.INFO, "Initialized sound player on: " + sound.getName());
        playing = true;
    }

    @Override
    public void run() {
        // setup buffer for containing samples of audio to be played
        byte[] buffer = new byte[AudioMaster.standardBufferSize];
        int bytesRead = 0;
        int bytesWritten = 0;

        while (playing && master.getPlaying()) {

            // read a sample from the audio file
            try {
                bytesRead = clip.read(buffer, 0, AudioMaster.standardBufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bytesRead > 0) {

                // write the sample to all outputs
                for (SourceDataLine sdl : speakers) {
                    bytesWritten = sdl.write(buffer, 0, AudioMaster.standardBufferSize);
                }

                // in case of an incomplete write
                if (bytesWritten < AudioMaster.standardBufferSize) {
                    logger.log(Level.WARNING, "Failed to write buffer to output");
                    playing = false;
                    continue;
                }
            }

            // in case of an incomplete rad
            if (bytesRead < AudioMaster.standardBufferSize) {
                logger.log(Level.WARNING, "Failed to read buffer from file: " + sound.getName());
                playing = false;
                continue;
            }
        }

        // close remaining streams
        try {
            clip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (SourceDataLine sdl : speakers) {
            sdl.close();
        }

        logger.log(Level.INFO, "Finished playing clip: " + sound.getName());
    }

    // does not immediately stop the clip
    public synchronized void stop() {
        logger.log(Level.INFO, "Prematurely stopping clip: " + sound.getName());
        playing = false;
    }

}