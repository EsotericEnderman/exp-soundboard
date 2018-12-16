package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class MenuController extends GuiController {

	private static final int[] singleIndices = {0};
	private static final int[] doubleIndices = {0, 1};

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

	// --- Entry/Audio Control Buttons --- //

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
	void initialize() {
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

		clipColumn.setCellValueFactory(new PropertyValueFactory<EntryModel, String>("clipName"));
		hotkeyColumn.setCellValueFactory(new PropertyValueFactory<EntryModel, String>("hotkey"));

		tableList = FXCollections.observableArrayList();
		entryTable.setItems(tableList);

		Mixer.Info[] audios = AudioSystem.getMixerInfo();
		List<AudioDeviceModel> devices = new ArrayList<AudioDeviceModel>();
		for (int i = 0; i < audios.length; i++) {
			if (AudioSystem.getMixer(audios[i]).getSourceLineInfo().length > 0)
				devices.add(new AudioDeviceModel(audios[i]));
		}

		audioList = FXCollections.observableArrayList(devices);
		Callback<ListView<AudioDeviceModel>, ListCell<AudioDeviceModel>> cellFactory = new Callback<ListView<AudioDeviceModel>, ListCell<AudioDeviceModel>>() {
			@Override
			public ListCell<AudioDeviceModel> call(ListView<AudioDeviceModel> param) {
				return new ListCell<AudioDeviceModel>() {
					@Override
					protected void updateItem(AudioDeviceModel item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(item.getName());
						}
					}

				};
			}
		};

		primarySpeakerCombo.setItems(audioList);
		primarySpeakerCombo.setButtonCell(cellFactory.call(null));
		primarySpeakerCombo.setCellFactory(cellFactory);
		primarySpeakerCombo.getSelectionModel().select(0); // select the first option, as to prevent the program starting with null devices selected
		primarySpeakerCombo.valueProperty().addListener(new ChangeListener<AudioDeviceModel>() {
			@Override
			public void changed(ObservableValue<? extends AudioDeviceModel> observable, AudioDeviceModel oldValue, AudioDeviceModel newValue) {
				parent.audio.setOutput(0, getPrimarySelect().getInfo());
			}
		});

		secondarySpeakerCombo.setItems(audioList);
		secondarySpeakerCombo.setButtonCell(cellFactory.call(null));
		secondarySpeakerCombo.setCellFactory(cellFactory);
		secondarySpeakerCombo.getSelectionModel().select(0); // same as other select, this must be done before the listener to prevent race condition erros
		secondarySpeakerCombo.valueProperty().addListener(new ChangeListener<AudioDeviceModel>() {
			@Override
			public void changed(ObservableValue<? extends AudioDeviceModel> observable, AudioDeviceModel oldValue, AudioDeviceModel newValue) {
				parent.audio.setOutput(1, getSecondarySelect().getInfo());
			}
		});



	}

	@FXML
	void onAddPressed(ActionEvent event) {
		parent.entryController.start();
	}

	@FXML
	void onEditPressed(ActionEvent event) { // TODO potentially change this to outside function
		parent.entryController.start(getSelectedEntry());
	}

	@FXML
	void onPlayPressed(ActionEvent event) {
		playSelected();
	}

	@FXML
	void onRemovePressed(ActionEvent event) {
		if (!removeSelected()) {
			// TODO play error sound, user has no selected entry
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
		// TODO send saved destination to file parser
	}

	@FXML
	void onSaveAsMenuPressed(ActionEvent event) {
		// TODO open file picker, send destination to file parser
	}

	@FXML
	void onWebpageMenuPressed(ActionEvent event) {
		// TODO ask system to open browser on github page link
	}

	@FXML
	void onQuitMenuPressed(ActionEvent event) {
		// TODO check if setup changed, ask for user confirmation if changed, otherwise close
	}

	@FXML
	void onSettingsMenuPressed(ActionEvent event) {
		// TODO open settings menu
	}

	@FXML
	void onLevelsMenuPressed(ActionEvent event) {
		// TODO open levels menu, start live updating values to audio master
	}

	@FXML
	void onConverterMenuPressed(ActionEvent event) {
		// TODO open converter menu
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

	public AudioDeviceModel getPrimarySelect() {
		return primarySpeakerCombo.getValue();
	}

	public AudioDeviceModel getSecondarySelect() {
		return secondarySpeakerCombo.getValue();
	}

	public boolean secondaryChecked() {
		return secondarySpeakerCheck.isSelected();
	}

	public void play(EntryModel entry) {
		try {
			// If the secondary check box is checked, return indices 0 and 1, otherwise just 0
			parent.audio.play(entry.getEntry().getFile(), (secondaryChecked() ? new int[]{0, 1} : new int[]{0}));
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Error playing audio file: " + e.getMessage(), ButtonType.OK).showAndWait();
		}
	}

	public boolean playSelected() {
		EntryModel selected = getSelectedEntry();
		if (selected != null) {
			play(selected);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeSelected() {
		EntryModel selected = getSelectedEntry();
		if (selected != null) {
			removeEntry(selected);
			return true;
		} else {
			return false;
		}
	}

}
