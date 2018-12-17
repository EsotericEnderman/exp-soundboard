package gui;

import javafx.beans.property.SimpleStringProperty;
import model.Entry;
import model.KeyUtil;

public class EntryModel {

	private final SimpleStringProperty clipName;
	private final SimpleStringProperty hotkey;
	private final Entry entry;

	public EntryModel(Entry model) {
		this.clipName = new SimpleStringProperty(model.getFile().getName());
		this.hotkey = new SimpleStringProperty(KeyUtil.asReadable(model.getCombo()));
		this.entry = model;
	}

	public String getClipName() {
		return clipName.get();
	}

	public String getHotkey() {
		return hotkey.get();
	}

	public Entry getEntry() {
		return entry;
	}

}
