package ca.exp.soundboard.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.*;

public class AudioMaster {

	// copied from old code
	public static final int standardBufferSize = 2048;
	public static final int internalBufferSize = 8192;
	public static final int standardSampleSize = 16;
	public static final int standardChannels = 2;
	public static final int standardFrameSize = 4;
	public static final float standardSampleRate = 44100.0F;

	public static final AudioFormat decodeFormat = new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED,
			standardSampleRate,
			standardSampleSize,
			standardChannels,
			standardFrameSize,
			standardSampleRate,
			false);

	public static final DataLine.Info standardDataLine = new DataLine.Info(
			SourceDataLine.class,
			decodeFormat,
			standardBufferSize);

	public static SourceDataLine getSpeakerLine(Mixer mixer) throws LineUnavailableException, IllegalArgumentException {
		return (SourceDataLine) mixer.getLine(standardDataLine);
	}

	public static FloatControl getMasterGain(SourceDataLine source) throws IllegalArgumentException {
		return (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
	}

	public static List<Mixer.Info> getValidMixers() {

		Mixer.Info[] base = AudioSystem.getMixerInfo();
		List<Mixer.Info> out = new ArrayList<Mixer.Info>();

		// keep only usable mixers
		Mixer mix;
		for (Mixer.Info inf : base) {
			mix = AudioSystem.getMixer(inf);
			if (mix.isLineSupported(standardDataLine)) out.add(inf);
		}
		return out;
	}

	public static boolean isFileSupported(File target) {
		try {
			AudioSystem.getAudioFileFormat(target);
			return true;
		} catch (UnsupportedAudioFileException | IOException e) {
			return false;
		}
	}

	public static boolean startMP3Decoder() {
		InputStream loaderFile = AudioMaster.class.getClassLoader().getResourceAsStream("loader.mp3");
		try {
			AudioSystem.getAudioFileFormat(loaderFile);
			AudioInputStream stream = AudioSystem.getAudioInputStream(loaderFile);
			stream.close();
			return true;
		} catch (UnsupportedAudioFileException | IOException e) {
			return false;
		}
	}

	//private AudioFormat modDecodeFormat; // TODO format for modified speed, mult samplerate by float in [0,1]
	public final ThreadGroup audioGroup = new ThreadGroup("Audio");

	private ThreadPoolExecutor audioThreadManager;
	private Logger logger;

	private List<SoundPlayer> active;
	private Mixer[] outputs;
	private float[] gains;

	public AudioMaster(int count, Mixer... mixers) {
		if (count < 1) throw new IllegalArgumentException("Number of outputs must be a positive non-zero number");

		// setup mixers, copying in the given
		int min = mixers.length < count ? mixers.length : count;
		this.outputs = new Mixer[count];
		for (int i = 0; i < min; i++) outputs[i] = mixers[i];

		active = new Vector<SoundPlayer>();
		logger = Logger.getLogger(this.getClass().getName());

		// setup gains, starting all at zero
		gains = new float[count];
		for (int i = 0; i < count; i++) {
			gains[i] = 0f;
		}

		// This constructor ensures all audio playing threads will be in the thread group accessible from this class.
		audioThreadManager = (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(audioGroup, r);
			}
		});

		// make sure the thread manager has a buffer for the first files to be played without interruption
		audioThreadManager.setCorePoolSize(10);
		audioThreadManager.prestartCoreThread();

		logger.info( "Initialized " + this.getClass().getName() + " with " + count + " outputs");
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
			speakers[i].open(decodeFormat, internalBufferSize);

			// set gain for SourceDataLine
			FloatControl gainControl = getMasterGain(speakers[i]);
			float speakerGain = gains[index];
			gainControl.setValue(speakerGain); // TODO: implement changing gain of currently running sound threads

			speakers[i].start();

			// make a thread for each output
			players[i] = new SoundPlayer(this, sound, speakers[i], index);
			active.add(players[i]);
			logger.info( "Dispatching thread to play: \"" + sound.getName() + "\" on " + speaker.getMixerInfo().getName());
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

	public void setOutput(int index, Mixer.Info outputInfo) throws IllegalArgumentException {
		outputs[index] = AudioSystem.getMixer(outputInfo);
	}

	// --- Gain methods --- //

	public float getGain(int index) {
		return gains[index];
	}

	public void setGain(int index, float gain) {
		gains[index] = gain;
	}

	public void updateGain() {
		logger.info( "Pausing all sounds for gain update");
		List<PlayerState> updating = new ArrayList<PlayerState>();
		for (SoundPlayer player : active) {
			if (player.state.get() == PlayerState.PLAYING) {
				player.state.compareAndSet(PlayerState.PLAYING, PlayerState.WAIT);
				while(player.state.get() != PlayerState.WAITING); // TODO: check replacing with a hot potato wait (one waits for notify, then the other)
				if (!player.updateGain(gains[player.index]));
				player.state.compareAndSet(PlayerState.WAITING, PlayerState.PLAYING);
				synchronized (player.state) {
					player.state.notify();
				}
			} else {
				player.updateGain(gains[player.index]);
			}
		}
	}

	// --- Global Audio Controls --- //

	public void stopAll() {
		logger.info( "Halting all playing sounds");
		for (SoundPlayer player : active) {
			player.state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED); // TODO: shouldn't have concurrency errors, needs verification
			logger.info( "Halted thread: \"" + player + "\"");
		}
	}

	public void pauseAll() {
		logger.info( "Pausing all playing sounds");
		for (SoundPlayer player : active) {
			if (player.state.get() == PlayerState.PLAYING) {
				player.state.compareAndSet(PlayerState.PLAYING, PlayerState.PAUSED);
				logger.info( "Paused thread: \"" + player + "\"");
			}
		}
	}

	public void resumeAll() {
		logger.info( "Unpausing all paused sounds");
		for (SoundPlayer player : active) {
			if (player.state.get() == PlayerState.PAUSED) {
				player.state.compareAndSet(PlayerState.PAUSED, PlayerState.PLAYING);
				synchronized (player.state) {
					player.state.notify();
				}
				logger.info( "Unpaused thead: \"" + player + "\"");
			}
		}
	}

}
