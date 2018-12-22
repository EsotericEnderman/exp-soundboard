package model;

import sun.audio.AudioDevice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.*;

public class AudioMaster {

	static final int standardBufferSize = 2048;

	public final ThreadGroup audioGroup = new ThreadGroup("Audio");

	private Mixer[] outputs;
	private float[] gains;

	private ThreadPoolExecutor soundGroup;
	private AtomicBoolean playing;
	private List<SoundRunner> active;

	/**
	 *
	 * @param count The number of total outputs this object will handle.
	 */
	public AudioMaster(int count) {
		this(count, new Mixer[0]);
	}

	/**
	 *
	 * @param mixers All the mixers this object will initially have access to.
	 */
	public AudioMaster(Mixer... mixers) {
		this(mixers.length, mixers);
	}

	/**
	 *
	 * @param count The number of total outputs this object will handle.
	 * @param mixers The initial outputs the mixer has access to.
	 */
	public AudioMaster(int count, Mixer... mixers) {
		this.outputs = new Mixer[count];
		gains = new float[count];
		active = new ArrayList<SoundRunner>();
		playing = new AtomicBoolean(true);

		soundGroup = (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(audioGroup, r);
			}
		});

		for (int i = 0; i < mixers.length; i++) {
			outputs[i] = mixers[i];
			gains[i] = 0f;
		}
	}

	/**
	 * Retrieves and constructs the necessary javax.sound.sampled objects in order to play the given sound file.
	 * @param sound The file that will be played.
	 * @param indices The indices of the mixers that will be played on.
	 * @throws LineUnavailableException
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */ // TODO add exception explanations
	public void play(File sound, int... indices) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		if (!sound.exists()) {
			throw new IOException("File " + sound.getName() + " does not exist!");
		}

		if (!sound.canRead()) {
			throw new IOException("File " + sound.getName() + " cannot be read!");
		}

		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sound);
		AudioFormat format = fileFormat.getFormat();
		FloatControl gain;

		SourceDataLine[] speakers = new SourceDataLine[indices.length];
		float[] levels = new float[indices.length];
		for (int i = 0; i < speakers.length; i++) {
			int index = indices[i];
			Mixer output = outputs[index];
			Line.Info[] sourceLines = output.getSourceLineInfo();
			// For the meanwhile grab the first, it should be a sourcedataline.
			speakers[i] = (SourceDataLine) output.getLine(sourceLines[0]); // TODO implement a search to grab the right line
			levels[i] = gains[index]; // TODO test one output off and one on
		}

		for (int i = 0; i < speakers.length; i++) {
			speakers[i].open(format);
			gain = (FloatControl) speakers[i].getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(levels[i]);
			speakers[i].start();
		}

		SoundRunner player = new SoundRunner(this, sound, playing, speakers);
		active.add(player);
		soundGroup.execute(player);
	}

	public boolean stopAll() {
		boolean stop = playing.compareAndSet(true, false);
		for (SoundRunner run : active) {
			run.stop();
		}
		active.clear();
		playing.compareAndSet(false, true);
		return stop;
	}

	public Mixer getOutput(int index) {
		return outputs[index];
	}

	public Mixer[] getOutputs() {
		return outputs;
	}

	public void setOutput(int index, Mixer.Info outputInfo) {
		outputs[index] = AudioSystem.getMixer(outputInfo);
	}
	
	public void setOutput(int index, Mixer mixer) {
		outputs[index] = mixer;
	}
	
	public void setOutputs(Mixer[] mixers) {
		outputs = mixers;
	}

	public void setGain(int index, float gain) {
		gains[index] = gain;
	}

}

class SoundRunner implements Runnable {

	private AudioMaster master;
	private File sound;
	private SourceDataLine[] speakers;
	private AudioInputStream clip;
	private AudioFormat clipFormat;

	private AtomicBoolean masterFlag;
	private boolean playing;

	public SoundRunner(AudioMaster master, File sound, AtomicBoolean masterFlag, SourceDataLine... speakers)
			throws UnsupportedAudioFileException, IOException {
		this.master = master;
		this.sound = sound;
		this.speakers = speakers;
		this.masterFlag = masterFlag;

		clip = AudioSystem.getAudioInputStream(sound);
		clipFormat = clip.getFormat();
		playing = true;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[AudioMaster.standardBufferSize];
		int bytesRead = 0;

		while (playing && masterFlag.get()) {
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
