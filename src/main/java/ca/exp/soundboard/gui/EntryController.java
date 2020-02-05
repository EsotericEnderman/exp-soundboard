package ca.exp.soundboard.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import ca.exp.soundboard.model.Entry;
import ca.exp.soundboard.model.EntryListener;
import ca.exp.soundboard.util.FileIO;

public class EntryController extends GuiController {

	// --- Text Field Properties --- //

	private static final String defaultTextStyle = "-fx-control-inner-background: white;";
	private static final String activeTextStyle = "-fx-control-inner-background: cyan;";

	private static final String defaultSelect = "None Selected";
	private static final String defaultPress = "Press any key or key Combo...";
    private static final String emptyHotkey = "";

    // --- Working Data --- //

	private EntryListener listener;
	private NativeKeyEvent nativeEvent;
	private FileChooser chooser;
	private File workFile;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	// --- GUI Objects --- //

	@FXML // fx:id="selectButton"
	private Button selectButton; // Value injected by FXMLLoader

	@FXML // fx:id="selectionText"
	private Label selectionText; // Value injected by FXMLLoader

	@FXML // fx:id="hotkeyField"
	private TextField hotkeyField; // Value injected by FXMLLoader

	@FXML // fx:id="doneButton"
	private Button doneButton; // Value injected by FXMLLoader

	// --- GUI Methods --- //

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert selectionText != null : "fx:id=\"selectionText\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert hotkeyField != null : "fx:id=\"hotkeyField\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
	}

	@FXML
	void onDonePressed(ActionEvent event) { // TODO make flags to signify the user set file and keycombo correctly
		stop();
	}

	@FXML
	void onFieldClicked(MouseEvent event) {
		if (event.isPrimaryButtonDown()) {
			startListening();
		} else if (event.isSecondaryButtonDown()) {
			stopListening();
		}
	}

	@FXML
	void onFieldPressed(KeyEvent event) {
		logger.log(Level.INFO, "\"" + event.getText() + "\" pressed");
		stopListening();
	}

	@FXML
	void onSelectClicked(ActionEvent event) {
		grabFile();
	}

	// --- Interaction Methods --- //

	public String getHotkeyText() {
		return hotkeyField.getText();
	}

	public void setHotkeyText(String text) {
		hotkeyField.setText(text);
	}

	public NativeKeyEvent getCombo() {
		return nativeEvent;
	}

	public void setCombo(NativeKeyEvent nativeEvent) {
		this.nativeEvent = nativeEvent;
	}

	public void startListening() {
		hotkeyField.setStyle(activeTextStyle);
		hotkeyField.setText(defaultPress);
		listener.listenOn(this);
	}

	public void stopListening() {
		hotkeyField.setStyle(defaultTextStyle);
		if (hotkeyField.getText() == defaultPress) {
			hotkeyField.setText(emptyHotkey);
		}
		listener.stopListening();
	}

	private void grabFile() {
		logger.log(Level.INFO, "Picking sound file to bind");
		File selectedFile = chooser.showOpenDialog(stage);
		if (selectedFile != null) {
			logger.log(Level.INFO, "Picked: \"" + selectedFile.getName() + "\"");
			selectionText.setText(selectedFile.getAbsolutePath());
			workFile = selectedFile;
		}
	}

	// --- General Methods --- //

    @Override
    void preload(SoundboardStage parent, Stage stage, Scene scene) {
        super.preload(parent, stage, scene);
		logger.log(Level.INFO, "Initializing entry controller");

		// setup file chooser, add filter for known audio files
        chooser = new FileChooser();
        chooser.setTitle("Choose Audio File");
        chooser.getExtensionFilters().addAll(FileIO.standard_audio, FileIO.all_files);

        // setup listener for catching a hotkey
        try {
            listener = new EntryListener();
        } catch (NativeHookException nhe) {
            logger.log(Level.WARNING, "Failed to create entry assembler: " + nhe);
        }
    }

	@Override
	public void reset() {
		logger.log(Level.INFO, "Resetting GUI elements");
		init(defaultTextStyle, defaultSelect, emptyHotkey, null, null);
	}

	private void init(String hotkeyStyle, String selection, String hotkeyText, NativeKeyEvent key, File file) {
		hotkeyField.setStyle(hotkeyStyle);
		selectionText.setText(selection);
		hotkeyField.setText(hotkeyText);
		nativeEvent = key;
		workFile = file;
	}

	public void start(Entry starter) {
		if (starter != null) {
			logger.log(Level.INFO, "Started editing entry" + starter.toString());
			init(defaultTextStyle,
					starter.getFile().getAbsolutePath(),
					starter.getCombo().toString(),
					starter.getCombo().getNative(),
					starter.getFile());
			stage.show();
			active = true;
		} else {
			start();
		}
	}

	public void start() {
		logger.log(Level.INFO, "Starting new entry");
		init(defaultTextStyle, defaultSelect, emptyHotkey, null, null);
		stage.show();
		active = true;
	}

	public void stop() {
		if (listener.isListening()) stopListening();
		if (workFile != null && nativeEvent != null) {
			logger.log(Level.INFO, "Finished and adding new entry to soundboard");
			parent.getModel().getEntries().add(new Entry(workFile, nativeEvent));
			stage.close();
			active = false;
		} else {
			logger.log(Level.WARNING, "Not all fields have been filled!");
		}
	}

	@Override
	public void forceStop() {
		super.forceStop();
		nativeEvent = null;
		workFile = null;
	}
}
