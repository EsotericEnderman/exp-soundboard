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
	public static final int standardSampleSize = 16;
	public static final int standardChannels = 2;
	public static final int standardFrameSize = 4;
	public static final float standardSampleRate = 44100.0F;

	public static final AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, standardSampleRate,
			standardSampleSize, standardChannels, standardFrameSize, standardSampleRate, false);
	public static final DataLine.Info standardDataLine = new DataLine.Info(SourceDataLine.class, decodeFormat,
			standardBufferSize);

	public static SourceDataLine getSpeakerLine(Mixer mixer) throws LineUnavailableException, IllegalArgumentException {
		return (SourceDataLine) mixer.getLine(standardDataLine);
	}

	public static FloatControl getMasterGain(SourceDataLine source) throws IllegalArgumentException {
		return (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
	}

	private AudioFormat modDecodeFormat; // TODO format for modified speed, mult samplerate by float in [0,1]
	public final ThreadGroup audioGroup = new ThreadGroup("Audio");

	private ThreadPoolExecutor audioThreadManager;
	private Logger logger;

	private List<SoundPlayer> active;
	private Mixer[] outputs;
	private float[] gains;

	public AudioMaster(int count) {
		this(count, new Mixer[0]);
	}

	public AudioMaster(Mixer... mixers) {
		this(mixers.length, mixers);
	}

	public AudioMaster(int count, Mixer... mixers) {
		// extends given array, to prevent an out of bounds exception
		if (mixers.length < count) mixers = Arrays.copyOf(mixers, count);

		this.outputs = new Mixer[count];
		active = new ArrayList<SoundPlayer>();
		logger = Logger.getLogger(this.getClass().getName());
		gains = new float[count];

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

		// reset gains
		for (int i = 0; i < count; i++) {
			gains[i] = 0f;
		}

		logger.log(Level.INFO, "Initialized " + this.getClass().getName() + " with " + count + " outputs");
	}

	public void play(File sound, int... indices) throws LineUnavailableException, UnsupportedAudioFileException, IOException, IllegalArgumentException {

		// precondition to save time
		if (!sound.exists()) {
			throw new IllegalArgumentException("File \"" + sound.getName() + "\" does not exist!");
		}
		if (!sound.canRead()) {
			throw new IllegalArgumentException("File \"" + sound.getName() + "\" cannot be read!");
		}

		// Get each requested speaker by the array of indices
		SourceDataLine[] speakers = new SourceDataLine[indices.length];
		SoundPlayer[] players = new SoundPlayer[indices.length];
		for (int i = 0; i < indices.length; i++) {
			// retrieve requested output Mixer(s)
			int index = indices[i];
			Mixer speaker = outputs[index];

			// grab proper SourceDataLine
			speakers[i] = getSpeakerLine(speaker);
			//speakers[i].open(decodeFormat, standardBufferSize);
			//speakers[i].start();

			// set gain for SourceDataLine
			FloatControl gainControl = getMasterGain(speakers[i]);
			float speakerGain = gains[index];
			gainControl.setValue(speakerGain); // TODO: implement changing gain of currently running sound threads

			// make a thread for each output
			players[i] = new SoundPlayer(this, sound, speakers[i]);
			active.add(players[i]);
			logger.log(Level.INFO, "Dispatching thread to play: \"" + sound.getName() + "\" on " + speaker.getMixerInfo().getName());
		}

		// send threads to player
		for (SoundPlayer player : players) {
			audioThreadManager.execute(player);
		}
	}

	// --- Output methods --- ///

	public Mixer getOutput(int index) {
		return outputs[index];
	}

	public void setOutput(int index, Mixer.Info outputInfo) throws IllegalArgumentException, NullPointerException {
		outputs[index] = AudioSystem.getMixer(outputInfo);
	}
	
	public void setOutput(int index, Mixer mixer) {
		outputs[index] = mixer;
	}

	// --- Gain methods --- //

	public float getGain(int index) {
		return gains[index];
	}

	public void setGain(int index, float gain) {
		gains[index] = gain;

		try {
			getMasterGain(getSpeakerLine(outputs[index])).setValue(gain);
		} catch (LineUnavailableException | IllegalArgumentException e) {
			logger.log(Level.WARNING, "Failed to update line gain", e);
		}
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
		SoundPlayer player;
		for (int i = 0; i < active.size(); i++) {
			active.get(i).running.compareAndSet(true, false);// TODO: shouldn't have concurrency errors, needs verification
			player = active.remove(i);
			logger.log(Level.INFO, "Removed thread: \"" + player + "\"");
		}
	}

	public void pauseAll() {
		logger.log(Level.INFO, "Pausing all playing sounds");
		SoundPlayer player;
		for (int i = 0; i < active.size(); i++) {
			player = active.get(i);
			player.paused.compareAndSet(false, true);
			logger.log(Level.INFO, "Paused thread: \"" + player + "\"");
		}
	}

	public void resumeAll() {
		logger.log(Level.INFO, "Unpausing all paused sounds");
		for (SoundPlayer player : active) {
			player.paused.compareAndSet(true, false);
			player.paused.notify();
			logger.log(Level.INFO, "Unpaused thead: \"" + player + "\"");
		}
	}

}
