package ca.exp.soundboard.remake.model;

import java.io.File;

import org.jnativehook.keyboard.NativeKeyEvent;

public class Entry {

	public static long invalidId = 0;
	private static long idCounter = 0;

	private static long createId() {
		return idCounter++;
	}

	private File file;
	private KeyCombination combo;
	private final Long localId;

	public Entry(File file, NativeKeyEvent nativeKey) {
		combo = new KeyCombination(nativeKey);
		this.file = file;
		localId = createId();
	}

	public Entry(File file, KeyCombination combo) {
		this.file = file;
		this.combo = combo;
		localId = createId();
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

	public Long getLocalId() {
		return localId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Entry entry = (Entry) o;

		if (!file.equals(entry.file)) return false;
		return combo.equals(entry.combo);
	}

	@Override
	public int hashCode() {
		int result = file.hashCode();
		result = 31 * result + combo.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return combo.toString() + " = " + file.getName();
	}

	@Override
	protected Object clone() {
		return new Entry(file, combo);
	}

}
