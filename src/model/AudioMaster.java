package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioMaster {

	static ThreadGroup audio = new ThreadGroup("Audio");

	static final int standardBufferSize = 2048;

	private Mixer[] outputs;
	private float[] gains;

	private ThreadPoolExecutor soundGroup;
	private AtomicBoolean playing;

	public AudioMaster(int outputs) {
		this.outputs = new Mixer[outputs];
		gains = new float[outputs];
		soundGroup = (ThreadPoolExecutor) Executors.newCachedThreadPool(new AudioFactory());
		playing = new AtomicBoolean(true);
	}

	public void play(Entry entry, int... indices)
			throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		File sound = entry.getFile();
		if (!(sound.exists() && sound.canRead())) {
			throw new IOException("Given file cannot be read from or does not exist!");
		}

		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sound);
		AudioFormat format = fileFormat.getFormat();
		FloatControl gain;

		SourceDataLine[] speakers = new SourceDataLine[indices.length];
		float[] levels = new float[indices.length];
		for (int i = 0; i < speakers.length; i++) {
			int index = indices[i];
			Mixer output = outputs[index];
			speakers[i] = (SourceDataLine) output.getLine(output.getLineInfo());
			levels[i] = gains[index]; // TODO test one output off and one on
		}

		for (int i = 0; i < speakers.length; i++) {
			speakers[i].open(format);
			gain = (FloatControl) speakers[i].getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(levels[i]);
			speakers[i].start();
		}

		soundGroup.execute(new SoundRunner(sound, playing, speakers));
	}

	public boolean stopAll() {
		boolean stop = playing.compareAndSet(true, false);
		soundGroup.shutdown();
		// playing.lazySet(true); // should be set back to true by the time all the
		// threads have stopped
		return stop;
	}

	public Mixer getOutput(int index) {
		return outputs[index];
	}

	public final Mixer[] getOutputs() {
		return outputs;
	}

	public void setOutput(int index, Mixer.Info outputInfo) {
		outputs[index] = AudioSystem.getMixer(outputInfo);
	}

}

class AudioFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(AudioMaster.audio, r);
	}

}

class SoundRunner implements Runnable {

	private File sound;
	private SourceDataLine[] speakers;
	private AudioInputStream clip;
	private AudioFormat clipFormat;

	private AtomicBoolean master;
	private boolean playing;

	public SoundRunner(File sound, AtomicBoolean master, SourceDataLine... speakers)
			throws UnsupportedAudioFileException, IOException {
		this.sound = sound;
		this.speakers = speakers;
		this.master = master;

		clip = AudioSystem.getAudioInputStream(sound);
		clipFormat = clip.getFormat();
		playing = true;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[AudioMaster.standardBufferSize];
		int bytesRead = 0;

		while (playing && master.get()) {
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
