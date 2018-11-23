package model;

import java.io.File;

public class Entry {

	private File file;
	private KeyCombination keys;

	public Entry(File file, KeyCombination keys) {
		this.file = file;
		this.keys = keys;
	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File newFile) {
		file = newFile;
	}
	
	public KeyCombination getKeys() {
		return keys;
	}
	
	public void setKets(KeyCombination newKeys) {
		keys = newKeys;
	}

	public boolean checkKeys(KeyCombination matches) {
		// check all of the required keys are down
		return false;
	}

}
