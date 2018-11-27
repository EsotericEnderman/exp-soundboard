package model;

import java.io.IOException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioMaster {
	
	List<SoundFile> files;

	public AudioMaster() {
		// TODO Auto-generated constructor stub
	}
	
	public void play(Entry entry) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundFile newFile = new SoundFile(entry.getFile());
		newFile.play();
		files.add(newFile);
	}

	public void stopAll() {
		for (SoundFile sound : files) {
			sound.stop();
		}
	}

}
