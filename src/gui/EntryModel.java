package gui;

import javafx.beans.property.SimpleStringProperty;
import model.Entry;

public class EntryModel {

	private final SimpleStringProperty clipName;
	private final SimpleStringProperty hotkey;
	private final Entry entry;

	public EntryModel(String clipName, String hotkey) {
		this.clipName = new SimpleStringProperty(clipName);
		this.hotkey = new SimpleStringProperty(hotkey);
		entry = null; // TODO find an entry with the given specs
	}

	public EntryModel(Entry model) {
		this.clipName = new SimpleStringProperty(model.getFile().getName());
		this.hotkey = new SimpleStringProperty(); // TODO add a util that translates a keycode into usable text
		this.entry = model;
	}

	public String getClipName() {
		return clipName.get();
	}

	public void setClipName(String newValue) {
		clipName.set(newValue);
	}

	public String getHotkey() {
		return hotkey.get();
	}

	public void setHotKey(String newValue) {
		hotkey.set(newValue);
	}

	public Entry getEntry() {
		return entry;
	}

}
