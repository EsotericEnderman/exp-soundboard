package gui;

import javafx.beans.property.SimpleStringProperty;
import model.Entry;

public class EntryModel {
	
	private final SimpleStringProperty clipName;
	private final SimpleStringProperty hotkey;

	public EntryModel(String clipName, String hotkey) {
		this.clipName = new SimpleStringProperty(clipName);
		this.hotkey = new SimpleStringProperty(clipName);
	}
	
	public EntryModel(Entry model) {
		this.clipName = new SimpleStringProperty(model.getFile().getFile().getName());
		this.hotkey = new SimpleStringProperty(); // TODO add a util that translates a keycode into usable text
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

}
