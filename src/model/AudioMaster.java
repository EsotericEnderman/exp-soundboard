package model;

import sun.audio.AudioDevice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

	static final int standardBufferSize = 2048;

	public final ThreadGroup audioGroup = new ThreadGroup("Audio");

	private Mixer[] outputs;
	private FloatControl[] gainControls;

	private List<Thread> active;
	private Logger logger;

	/**
	 * Constructs this class with a given number of mixer slots, this value will not change.
	 * @param count The number of total outputs this object will handle.
	 */
	public AudioMaster(int count) {
		this(count, new Mixer[0]);
	}

	/**
	 * Constructs this class with an array of mixers, the class will start with this amount and gain no more.
	 * The individual mixers can be changed however.
	 * @param mixers All the mixers this object will initially have access to.
	 */
	public AudioMaster(Mixer... mixers) {
		this(mixers.length, mixers);
	}

	/**
	 * Constructs this class with a total number of mixers, plus an array of inital mixers to work with.
	 * In the case that the number of mixers exceeds the size specified by count, the array will be truncated to fit.
	 * @param count The number of total outputs this object will handle.
	 * @param mixers The initial outputs the class has access to.
	 */
	public AudioMaster(int count, Mixer... mixers) throws LineUnavailableException {
		// extends given array, to prevent an out of bounds exception
		if (mixers.length < count) mixers = Arrays.copyOf(mixers, count);

		this.outputs = new Mixer[count];
		gainControls = new FloatControl[count];
		active = new ArrayList<Thread>();
		logger = Logger.getLogger(AudioMaster.class.getName());

		// Copies all available mixers.
		for (int i = 0; i < count; i++) {
			outputs[i] = mixers[i];
		}

		// Sets default values for each gain slider.
		SourceDataLine speaker;
		for (int i = 0; i < count; i++) {
			speaker = getSpeakerLine(outputs[i]);
			gainControls[i] = (FloatControl) speaker.getControl(FloatControl.Type.MASTER_GAIN);
		}

		logger.log(Level.INFO, "Initialized Audio backend with " + count + " outputs");
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
		float[] levels = new float[indices.length];
		for (int i = 0; i < speakers.length; i++) {
			int index = indices[i];
			Mixer output = outputs[index];
			Line.Info[] sourceLines = output.getSourceLineInfo();
			// For the meanwhile grab the first, it should be a sourcedataline.
			speakers[i] = (SourceDataLine) output.getLine(sourceLines[0]);
			levels[i] = gains[index]; // TODO test one output off and one on
		}

		// Open each speaker, set its gain to the proper level and prepare it for playing.
		for (int i = 0; i < speakers.length; i++) {
			speakers[i].open(format);
			gain = (FloatControl) speakers[i].getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(levels[i]);
			speakers[i].start();
		}

		// Delegate the task of playing audio to a separate thread, using a thread pool to manage each thread.
		// All the while keeping track of each thread in a separate list.
		SoundRunner player = new SoundRunner(this, sound, speakers);
		//active.add(player);
		soundGroup.execute(player);
	}

	public boolean stopAll() {
		logger.log(Level.INFO, "Stopping all playing sounds");
		boolean stop = playing.compareAndSet(true, false);
		for (SoundRunner run : active) {
			run.halt();
		}
		active.clear();
		playing.compareAndSet(false, true);
		return stop;
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

	public float getGain(int index) {
		return gainControls[index].getValue();
	}

	public void setGain(int index, float gain) {
		gainControls[index].setValue(gain);
	}

	public void setGains(float gain) {
		for (int i = 0; i < gainControls.length; i++) {
			gainControls[i].setValue(gain);
		}
	}

	// --- SoundRunner methods --- //

	public boolean removeRunner(SoundRunner runner) {
		return active.remove(runner);
	}

	public boolean addRunner(SoundRunner runner) {
		return active.add(runner);
	}

	// --- State methods --- //

	public boolean getPlaying() {
		return playing.get();
	}

	public void setPlaying(boolean value) {
		playing.set(value);
	}

}
