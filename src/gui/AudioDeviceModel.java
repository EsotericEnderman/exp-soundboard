package gui;

import javax.sound.sampled.Mixer;

import javafx.beans.property.SimpleStringProperty;

public class AudioDeviceModel {
	
	private final SimpleStringProperty name;
	private final Mixer.Info info;

	public AudioDeviceModel(Mixer.Info info) {
		name = new SimpleStringProperty(info.getName());
		this.info = info;
	}
	
	public String getName() {
		return name.get();
	}
	
	public Mixer.Info getInfo() {
		return info;
	}
	
	@Override
	public String toString() {
		return info.getName();
	}

}
