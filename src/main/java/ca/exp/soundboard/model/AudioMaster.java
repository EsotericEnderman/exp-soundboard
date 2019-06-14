package ca.exp.soundboard.model;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.sampled.*;

public class AudioMaster {

	// copied from old code
	public static final int standardBufferSize = 2048;
	public static final AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4,
			44100.0F, false);
	public static final DataLine.Info standardDataLine = new DataLine.Info(SourceDataLine.class, decodeFormat, standardBufferSize);


	public static SourceDataLine getSpeakerLine(Mixer mixer) throws LineUnavailableException {
		return (SourceDataLine) mixer.getLine(standardDataLine);  // TODO implement a search to grab the right line
	}

	public static FloatControl getMasterGain(SourceDataLine source) {
		return (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
	}



	public final ThreadGroup audioGroup = new ThreadGroup("Audio");

	private Mixer[] outputs;
	private ThreadPoolExecutor audioThreadManager;
	private List<SoundPlayer> active;
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
		active = new CopyOnWriteArrayList<SoundPlayer>();
		logger = Logger.getLogger(this.getClass().getName());

		// This constructor ensures all audio playing threads will be in the thread group accessible from this class.
		audioThreadManager = (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(audioGroup, r);
			}
		});

		// make sure the thread manager has a buffer for the first files to be played without interruption
		audioThreadManager.setCorePoolSize(10);
		audioThreadManager.prestartCoreThread();

		// Copies all available mixers.
		for (int i = 0; i < count; i++) {
			outputs[i] = mixers[i];
		}

		logger.log(Level.INFO, "Initialized Audio backend with " + count + " outputs");
	}

	public void play(File sound, int... indices) throws LineUnavailableException, UnsupportedAudioFileException, IOException, IllegalArgumentException {

		// precondition to save time
		if (!sound.exists()) {
			throw new IllegalArgumentException("File " + sound.getName() + " does not exist!");
		}
		if (!sound.canRead()) {
			throw new IllegalArgumentException("File " + sound.getName() + " cannot be read!");
		}

		AudioFormat format = decodeFormat;

		// Get each requested speaker by the array of indices
		SourceDataLine[] speakers = new SourceDataLine[indices.length];
		for (int i = 0; i < indices.length; i++) {
			int index = indices[i];
			Mixer speaker = outputs[index];
			speakers[i] = getSpeakerLine(speaker);
			speakers[i].open(decodeFormat, standardBufferSize);
			speakers[i].start();
		}

		// package audio player in its own thread
		SoundPlayer player = new SoundPlayer(this, sound, speakers);

		logger.log(Level.INFO, "Dispatching thread to play: \"" + sound.getName() + "\"");
		audioThreadManager.execute(player);
		active.add(player);
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

	public boolean addPlayer(SoundPlayer player) {
		return active.add(player);
	}

	public boolean removePlayer(SoundPlayer player) {
		return active.remove(player);
	}

	// --- Global Audio Controls --- //

	public void stopAll() {
		logger.log(Level.INFO, "Stopping all playing sounds");
		for (SoundPlayer player : active) {
			player.running.set(false); // TODO: shouldn't have concurrency errors, needs verification
			active.remove(player); // TODO: streamline
			logger.log(Level.INFO, "Removed thread: " + player);
		}
	}

	public void pauseAll() {
		logger.log(Level.INFO, "Pausing all playing sounds");
		for (SoundPlayer player : active) {
			player.paused.compareAndSet(false, true);
			logger.log(Level.INFO, "Paused thread: " + player);
		}
	}

	public void resumeAll() {
		logger.log(Level.INFO, "Unpausing all paused sounds");
		for (SoundPlayer player : active) {
			player.paused.compareAndSet(true, false);
			player.paused.notify();
			logger.log(Level.INFO, "Unpaused thead: " + player);
		}
	}

}
