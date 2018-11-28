package model;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

@Deprecated
public class SoundFile { // TODO possibly remove overuse of stop and close, the clip object probably
							// holds some data, doesn't need to be reset so often

	private AudioInputStream audioInputStream;
	private File soundFile;
	private Clip clip;

	private long currentFrame;

	public SoundFile(File soundFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		clip = AudioSystem.getClip();
		clip.open(audioInputStream);
	}

	public final File getFile() {
		return soundFile;
	}

	public void play() {
		clip.start();
	}

	public void pause() {
		currentFrame = clip.getMicrosecondPosition();
		clip.stop();
	}

	public void resume() {
		clip.setMicrosecondPosition(currentFrame);
		play();
	}

	public void restart() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		clip.stop();
		clip.close();
		resetAudioStream();
		currentFrame = 0L;
		clip.setMicrosecondPosition(currentFrame);
		play();
	}

	public void stop() {
		clip.stop();
		clip.close();
	}

	public void jump(long location) throws IllegalArgumentException {
		if (location > 0 && location < clip.getMicrosecondLength()) {
			clip.setMicrosecondPosition(location);
		} else {
			throw new IllegalArgumentException("Jump position is outside scope of sound file!");
		}
	}

	public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		clip.open(audioInputStream);
	}
}
