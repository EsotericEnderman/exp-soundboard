package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class AudioMaster {

	private static final int standardBufferSize = 2048;

	private Mixer primaryOutput;
	private Mixer secondaryOutput;

	private List<Entry> registeredEntries;

	private ThreadGroup audioThreadGroup;
	private List<PlayThread> audioThreads;

	public AudioMaster() {
		registeredEntries = new ArrayList<Entry>();
	}

	public void register(Entry entry) {
		try {
			File sound = entry.getFile();
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sound);
			AudioFormat format = fileFormat.getFormat();
			
			if (sound.exists() && sound.canRead()) {
				SourceDataLine primarySpeaker;
				SourceDataLine secondarySpeaker;
				
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, standardBufferSize);
				primarySpeaker = (SourceDataLine) primaryOutput.getLine(info);
				secondarySpeaker = (SourceDataLine) primaryOutput.getLine(info);
			}
		} catch (Exception e) {

		}
	}

	public void play(Entry entry) {

	}

	public void stopAll() {
	}

}

class PlayThread extends Thread {

	private File soundFile;

	public PlayThread(File soundFile) {
		super();
		init(soundFile);
	}

	public PlayThread(Runnable target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	public PlayThread(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public PlayThread(ThreadGroup group, Runnable target) {
		super(group, target);
		// TODO Auto-generated constructor stub
	}

	public PlayThread(ThreadGroup group, String name) {
		super(group, name);
		// TODO Auto-generated constructor stub
	}

	public PlayThread(Runnable target, String name) {
		super(target, name);
		// TODO Auto-generated constructor stub
	}

	public PlayThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		// TODO Auto-generated constructor stub
	}

	public PlayThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		// TODO Auto-generated constructor stub
	}

	private void init(File soundFile) {
		this.soundFile = soundFile;
	}

}
