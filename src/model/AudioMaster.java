package model;

import java.io.File;
import java.io.IOException;
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
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioMaster {

	private static final int standardBufferSize = 2048;

	private Mixer primaryOutput;
	private Mixer secondaryOutput;

	private List<Entry> registeredEntries;

	private ThreadGroup audioThreadGroup;
	private List<Thread> audioThreads;

	public AudioMaster() {
		registeredEntries = new ArrayList<Entry>();
		// populate combo boxes with 
	}

	public void play(Entry entry) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundFile file = new SoundFile(entry.getFile());
	}

	public void stopAll() {
		// TODO Auto-generated method stub
		
	}
	
	

}
