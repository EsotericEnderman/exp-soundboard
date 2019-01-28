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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MenuController extends GuiController {

	private static final int primaryIndex = 0;
	private static final int secondaryIndex = 1;

	private static final int[] singleIndices = {primaryIndex};
	private static final int[] doubleIndices = {primaryIndex, secondaryIndex};

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
	}

	@FXML
	void onAddPressed(ActionEvent event) {
		parent.entry().start();
	}

	@FXML
	void onEditPressed(ActionEvent event) { // TODO potentially change this to outside function
		parent.entry().start(getSelectedEntry());
	}

	@FXML
	void onPlayPressed(ActionEvent event) {
		playSelected();
	}

	@FXML
	void onRemovePressed(ActionEvent event) {
		if (removeSelected()) {
			// Possibly update references to the list (shouldn't be necessary)
		} else {
            // TODO play error sound, user has no selected entry
		}
	}

	@FXML
	void onStopPressed(ActionEvent event) {
		parent.audio().stopAll();
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
		parent.settings().start();
	}

	@FXML
	void onLevelsMenuPressed(ActionEvent event) {
		// TODO open levels menu, start live updating values to audio master
	}

	@FXML
	void onConverterMenuPressed(ActionEvent event) {
		// TODO open converter menu
	}

	@Override
	void preload(Soundboard parent, Stage stage, Scene scene) {
		super.preload(parent, stage, scene);

		// Set up each column in the table to pull the appropriate data from an EntryModel within
		// the table's internal list.
		clipColumn.setCellValueFactory(new PropertyValueFactory<EntryModel, String>("clipName"));
		hotkeyColumn.setCellValueFactory(new PropertyValueFactory<EntryModel, String>("hotkey"));

		// Set up the table's internal list.
		tableList = FXCollections.observableArrayList();
		entryTable.setItems(tableList);

		// Poll the system for all available audio devices, only keep ones who have a valid
		// output (SourceLine)
		Mixer.Info[] audios = AudioSystem.getMixerInfo();
		List<AudioDeviceModel> devices = new ArrayList<AudioDeviceModel>();
		for (int i = 0; i < audios.length; i++) {
			if (AudioSystem.getMixer(audios[i]).getSourceLineInfo().length > 0)
				devices.add(new AudioDeviceModel(audios[i]));
		}

		// Construct a list for the output selectors, which grabs the name of an audio device to
		// display as an option.
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

		// Set up the combo boxes for selecting an audio device to play to.
		// Both combo boxes use the same list of items, as all the manipulation they do is
		// selecting one of the elements of the list.
		// The zero-th element is preselected to prevent the user from starting with a null audio device.
		primarySpeakerCombo.setItems(audioList);
		primarySpeakerCombo.setButtonCell(cellFactory.call(null));
		primarySpeakerCombo.setCellFactory(cellFactory);
		primarySpeakerCombo.valueProperty().addListener(new ChangeListener<AudioDeviceModel>() {
			@Override
			public void changed(ObservableValue<? extends AudioDeviceModel> observable, AudioDeviceModel oldValue, AudioDeviceModel newValue) {
				parent.audio().setOutput(primaryIndex, newValue.getInfo());
			}
		});
		primarySpeakerCombo.getSelectionModel().select(0);

		// Refer to comments above
		secondarySpeakerCombo.setItems(audioList);
		secondarySpeakerCombo.setButtonCell(cellFactory.call(null));
		secondarySpeakerCombo.setCellFactory(cellFactory);
		secondarySpeakerCombo.valueProperty().addListener(new ChangeListener<AudioDeviceModel>() {
			@Override
			public void changed(ObservableValue<? extends AudioDeviceModel> observable, AudioDeviceModel oldValue, AudioDeviceModel newValue) {
				parent.audio().setOutput(secondaryIndex, newValue.getInfo());
			}
		});
		secondarySpeakerCombo.getSelectionModel().select(0);
	}

	public boolean addEntry(EntryModel entry) {
		return tableList.add(entry);
	}

	public EntryModel removeEntry(int index) {
		return tableList.remove(index);
	}

	public boolean removeEntry(EntryModel entry) {
		 return tableList.remove(entry);
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

	public boolean play(EntryModel entry) {
		try {
			// If the secondary check box is checked, return indices 0 and 1, otherwise just 0
			parent.audio().play(entry.getEntry().getFile(), (secondaryChecked() ? doubleIndices : singleIndices));
			return true;
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
			parent.throwBlockingError("Error playing audio file: " + e.getMessage());
			return false;
		}
	}

	public boolean playSelected() {
		EntryModel selected = getSelectedEntry();
		if (selected != null) {
			return play(selected);
		} else {
			return false;
		}
	}

	public boolean removeSelected() {
		EntryModel selected = getSelectedEntry();
		if (selected != null) {
			return removeEntry(selected);
		} else {
			// TODO report attempt to remove null entry.
			return false;
		}
	}

	public void reset() {
	    tableList.clear();
    }

}
