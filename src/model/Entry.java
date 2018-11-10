package model;

public class Entry {

	private SoundFile soundFile;
	private int[] keyNumbers;

	public Entry(SoundFile file, int[] keys) {
		soundFile = file;
		keyNumbers = keys;
	}

	public SoundFile getFile() {
		return soundFile;
	}

	public boolean checkKeys(int[] matches) {
		for (int i = 0; i < matches.length; i++) {
			
		}
		return false;
	}

}
