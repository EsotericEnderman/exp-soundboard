package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

class SoundRunner implements Runnable {

    private AudioMaster master;
    private File sound;
    private SourceDataLine[] speakers;
    private AudioInputStream clip;
    private AudioFormat clipFormat;

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
        playing = true;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[AudioMaster.standardBufferSize];
        int bytesRead = 0;

        while (playing && master.getPlaying()) {
            try {
                bytesRead = clip.read(buffer, 0, AudioMaster.standardBufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bytesRead > 0) {
                for (SourceDataLine sdl : speakers) {
                    sdl.write(buffer, 0, AudioMaster.standardBufferSize);
                }
            }

            if (bytesRead < AudioMaster.standardBufferSize) {
                playing = false;
            }
        }

        try {
            clip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (SourceDataLine sdl : speakers) {
            sdl.close();
        }
    }

    public synchronized void stop() {
        System.out.println("Stopping clip: " + sound.getName());
        playing = false;
    }

}