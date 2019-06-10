package model;

import java.io.File;

import org.jnativehook.keyboard.NativeKeyEvent;
import util.KeyUtil;

public class Entry {

	private File file;
	private KeyCombination combo;

	public Entry(File file, NativeKeyEvent nativeKey) {
		combo = new KeyCombination(nativeKey);
		this.file = file;
	}

	public Entry(File file, KeyCombination combo) {
		this.file = file;
		this.combo = combo;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File newFile) {
		file = newFile;
	}

	public KeyCombination getCombo() {
		return combo;
	}

	public void setCombo(KeyCombination newCombo) {
		combo = newCombo;
	}

	public boolean checkCombo(KeyCombination match) {
		return combo.equals(match);
	}

	@Override
	public String toString() {
		return combo.toString() + " = " + file.getAbsolutePath();
	}

	@Override
	protected Object clone() {
		return new Entry(file, combo);
	}

}
