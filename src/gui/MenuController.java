package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.Entry;

public class MenuController { // TODO make abstract controller superclass
	// TODO close entire program when this window is closed

	private Soundboard parent;
	
	private ObservableList<EntryModel> tableList;
	private ObservableList<AudioDeviceModel> audioList;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	// --- 1st Menu Items --- //

	@FXML // fx:id="newMenuButton"
	private MenuItem newMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="openMenuButton"
	private MenuItem openMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="closeMenuButton"
	private MenuItem closeMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="saveMenuButton"
	private MenuItem saveMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="saveAsMenuButton"
	private MenuItem saveAsMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="webpageMenuButton"
	private MenuItem webpageMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="quitMenuButton"
	private MenuItem quitMenuButton; // Value injected by FXMLLoader

	// --- 2nd Menu Items --- //

	@FXML // fx:id="settingsMenuButton"
	private MenuItem settingsMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="levelsMenuButton"
	private MenuItem levelsMenuButton; // Value injected by FXMLLoader

	@FXML // fx:id="converterMenuButton"
	private MenuItem converterMenuButton; // Value injected by FXMLLoader

	// --- Control Buttons --- //

	@FXML // fx:id="addButton"
	private Button addButton; // Value injected by FXMLLoader

	@FXML // fx:id="removeButton"
	private Button removeButton; // Value injected by FXMLLoader

	@FXML // fx:id="editButton"
	private Button editButton; // Value injected by FXMLLoader

	@FXML // fx:id="playButton"
	private Button playButton; // Value injected by FXMLLoader

	@FXML // fx:id="stopButton"
	private Button stopButton; // Value injected by FXMLLoader

	// --- Other items --- //

	@FXML // fx:id="secondarySpeakerCheck"
	private CheckBox secondarySpeakerCheck; // Value injected by FXMLLoader

	@FXML // fx:id="secondarySpeakerCombo"
	private ComboBox<AudioDeviceModel> secondarySpeakerCombo; // Value injected by FXMLLoader

	@FXML // fx:id="primarySpeakerCombo"
	private ComboBox<AudioDeviceModel> primarySpeakerCombo; // Value injected by FXMLLoader

	@FXML // fx:id="injectorCheck"
	private CheckBox injectorCheck; // Value injected by FXMLLoader

	@FXML // fx:id="pttHoldCheck"
	private CheckBox pttHoldCheck; // Value injected by FXMLLoader

	@FXML // fx:id="entryTable"
	private TableView<EntryModel> entryTable; // Value injected by FXMLLoader

	@FXML // fx:id="clipColumn"
	private TableColumn<EntryModel, String> clipColumn; // Value injected by FXMLLoader

	@FXML // fx:id="hotkeyColumn"
	private TableColumn<EntryModel, String> hotkeyColumn; // Value injected by FXMLLoader

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize(Soundboard parent) {
		assert newMenuButton != null : "fx:id=\"newMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert openMenuButton != null : "fx:id=\"openMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert closeMenuButton != null : "fx:id=\"closeMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert saveMenuButton != null : "fx:id=\"saveMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert saveAsMenuButton != null : "fx:id=\"saveAsMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert webpageMenuButton != null : "fx:id=\"webpageMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert quitMenuButton != null : "fx:id=\"quitMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert settingsMenuButton != null : "fx:id=\"settingsMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert levelsMenuButton != null : "fx:id=\"levelsMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert converterMenuButton != null : "fx:id=\"converterMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert removeButton != null : "fx:id=\"removeButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert playButton != null : "fx:id=\"playButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert stopButton != null : "fx:id=\"stopButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert secondarySpeakerCheck != null : "fx:id=\"secondarySpeakerCheck\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert secondarySpeakerCombo != null : "fx:id=\"secondarySpeakerCombo\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert primarySpeakerCombo != null : "fx:id=\"primarySpeakerCombo\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert injectorCheck != null : "fx:id=\"injectorCheck\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert pttHoldCheck != null : "fx:id=\"pttHoldCheck\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert entryTable != null : "fx:id=\"EntryTable\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";

		this.parent = parent;
		clipColumn.setCellValueFactory(new PropertyValueFactory<EntryModel, String>("clipName"));
		hotkeyColumn.setCellValueFactory(new PropertyValueFactory<EntryModel, String>("hotkey"));
		
		tableList = FXCollections.observableArrayList();
		entryTable.setItems(tableList);
		
		Mixer.Info[] audios = AudioSystem.getMixerInfo();
		List<AudioDeviceModel> devices = new ArrayList<AudioDeviceModel>();
		for (int i = 0; i < audios.length; i++) {
			devices.add(new AudioDeviceModel(audios[i]));
		}
		
		audioList = FXCollections.observableArrayList(devices);
		primarySpeakerCombo.setItems(audioList);
		secondarySpeakerCombo.setItems(audioList);
		
	}

	@FXML
	void onAddPressed(ActionEvent event) {
		parent.entryController.start();
	}

	@FXML
	void onEditPressed(ActionEvent event) {
		parent.entryController.start(getSelectedEntry());
	}

	@FXML
	void onPlayPressed(ActionEvent event) {
		EntryModel selected = getSelectedEntry();
		if (selected != null) {
			try {
				parent.audio.play(selected.getEntry());
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	void onRemovePressed(ActionEvent event) {
		EntryModel selected = getSelectedEntry();
		if (selected != null) {
			removeEntry(selected);
		}
	}

	@FXML
	void onStopPressed(ActionEvent event) {
		parent.audio.stopAll();
	}

	@FXML
	void onNewMenuPressed(ActionEvent event) {
		// TODO check for unsaved, reset model,
	}

	@FXML
	void onOpenMenuPressed(ActionEvent event) {
		// TODO open file picker, send to file parser
	}

	@FXML
	void onCloseMenuPressed(ActionEvent event) {
		// TODO poll if working file has been saved, if so ask user before, then close
		// everything
	}

	@FXML
	void onSaveMenuPressed(ActionEvent event) {
		// TODO open file picker, send destination to file parser
	}

	@FXML
	void onSaveAsMenuPressed(ActionEvent event) {

	}

	@FXML
	void onWebpageMenuPressed(ActionEvent event) {

	}

	@FXML
	void onQuitMenuPressed(ActionEvent event) {

	}

	@FXML
	void onSettingsMenuPressed(ActionEvent event) {

	}

	@FXML
	void onLevelsMenuPressed(ActionEvent event) {

	}

	@FXML
	void onConverterMenuPressed(ActionEvent event) {

	}
	
	public void addEntry(EntryModel entry) {
		tableList.add(entry);
	}
	
	public void removeEntry(int index) {
		tableList.remove(index);
	}
	
	public void removeEntry(EntryModel entry) {
		tableList.remove(entry);
	}
	
	public EntryModel getSelectedEntry() {
		return entryTable.getSelectionModel().getSelectedItem();
	}

}
