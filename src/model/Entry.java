package model;

import java.io.File;

import org.jnativehook.keyboard.NativeKeyEvent;

public class Entry {

	private File file;
	private NativeKeyEvent combo;

	public Entry(File file, NativeKeyEvent combo) {
		this.file = file;
		this.combo = combo;
	}

	public File getFile() {
		return file;
	}

	public NativeKeyEvent getCombo() {
		return combo;
	}

	public boolean checkCombo(NativeKeyEvent match) {
		return combo.paramString() == match.paramString();
	}

	@Override
	public String toString() {
		return KeyUtil.asReadable(combo) + " = " + file.getAbsolutePath();
	}

	@Override
	public boolean equals(Object obj) {
		return (this.file == ((Entry) obj).file) && (this.combo.paramString() == ((Entry) obj).combo.paramString());
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Entry(file, combo);
	}

}
