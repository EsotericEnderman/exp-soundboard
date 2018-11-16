package test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundTest {

	public static void main(String[] args) {
		String filePath = "";
		SoundTest test;

		try {
			test = new SoundTest(new File(filePath));
			test.play();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private AudioInputStream audioInputStream;

	private File soundFile;
	private Clip clip;
	long currentFrame;

	public SoundTest(File soundFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		clip = AudioSystem.getClip();
		clip.open(audioInputStream);
	}

	public void play() {
		clip.start();
	}

	public void pause() {
		currentFrame = clip.getMicrosecondPosition();
		clip.stop();
	}

	public void resume() {
		clip.close();
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

	public void jump(long location) {
		if (location > 0 && location < clip.getMicrosecondLength()) {
			clip.setMicrosecondPosition(location);
		}
	}

	public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		clip.open(audioInputStream);
	}

}
