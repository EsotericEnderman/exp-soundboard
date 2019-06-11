package model;

import sun.audio.AudioDevice;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.*;

public class AudioMaster {

	// copied from old code
	public static final AudioFormat standardFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4,
			44100.0F, false);

	public static SourceDataLine getSpeakerLine(Mixer mixer) throws LineUnavailableException {
		Line.Info[] sourceLines = mixer.getSourceLineInfo();
		return (SourceDataLine) mixer.getLine(sourceLines[0]);  // TODO implement a search to grab the right line
	}

	public static FloatControl getMasterGain(SourceDataLine source) {
		return (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
	}

	static final int standardBufferSize = 2048;

	public final ThreadGroup audioGroup = new ThreadGroup("Audio");

	private Mixer[] outputs;

	private Map<SoundPlayer, Thread> active;

	private Logger logger;

	public AudioMaster(int count) {
		this(count, new Mixer[0]);
	}

	public AudioMaster(Mixer... mixers) {
		this(mixers.length, mixers);
	}

	public AudioMaster(int count, Mixer... mixers){
		// extends given array, to prevent an out of bounds exception
		if (mixers.length < count) mixers = Arrays.copyOf(mixers, count);

		this.outputs = new Mixer[count];
		active = new HashMap<SoundPlayer, Thread>();
		logger = Logger.getLogger(this.getClass().getName());

		// Copies all available mixers.
		for (int i = 0; i < count; i++) {
			outputs[i] = mixers[i];
		}

		logger.log(Level.INFO, "Initialized Audio backend with " + count + " outputs");
	}

	public void play(File sound, int... indices) throws LineUnavailableException, UnsupportedAudioFileException, IOException {

		// precondition to save time
		if (!sound.exists()) {
			throw new IOException("File " + sound.getName() + " does not exist!");
		}
		if (!sound.canRead()) {
			throw new IOException("File " + sound.getName() + " cannot be read!");
		}

		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sound);
		AudioFormat format = fileFormat.getFormat();

		// Get each requested speaker and gain from outputs, as defined by the numbers in indices.
		SourceDataLine[] speakers = new SourceDataLine[indices.length];
		for (int i = 0; i < indices.length; i++) {
			int index = indices[i];
			Mixer speaker = outputs[index];
			speakers[i] = getSpeakerLine(speaker);
		}

		logger.log(Level.INFO, "Dispatching thread to play: " + sound.getName());

		SoundPlayer player = new SoundPlayer(this, sound, speakers);
		Thread runner = new Thread(audioGroup, player, sound.getName());
		runner.start();
		active.put(player, runner);
	}

	// --- Output methods --- ///

	public Mixer getOutput(int index) {
		return outputs[index];
	}

	public final Mixer[] getOutputs() {
		return outputs;
	}

	public void setOutput(int index, Mixer.Info outputInfo) {
		outputs[index] = AudioSystem.getMixer(outputInfo);
	}
	
	public void setOutput(int index, Mixer mixer) {
		outputs[index] = mixer;
	}

	// --- Gain methods --- //

	public float getGain(int index) throws LineUnavailableException {
		return getMasterGain(getSpeakerLine(outputs[index])).getValue();
	}

	public void setGain(int index, float gain) throws LineUnavailableException {
		getMasterGain(getSpeakerLine(outputs[index])).setValue(gain);
	}

	// --- Player methods --- //

	public boolean removePlayer(SoundPlayer player) {
		return Objects.nonNull(active.remove(player));
	}

	// --- Global Audio Controls --- //

	public void stopAll() {
		logger.log(Level.INFO, "Stopping all playing sounds");
		for (SoundPlayer player : active.keySet()) {
			player.running.set(false); // TODO: shouldn't have concurrency errors, needs verification
			active.remove(player); // TODO: streamline
		}
	}

}
