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

	public void setFile(File newFile) {
		file = newFile;
	}

	public NativeKeyEvent getCombo() {
		return combo;
	}

	public void setCombo(NativeKeyEvent combo) {
		this.combo = combo;
	}

	public boolean checkCombo(NativeKeyEvent match) {
		return combo.paramString() == match.paramString();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
