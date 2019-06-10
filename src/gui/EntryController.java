package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import util.KeyUtil;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import model.Entry;
import model.EntryListener;
import util.FileIO;

public class EntryController extends GuiController {

	private static final String defaultSelect = "None Selected";
	private static final String defaultPress = "Press any key or key Combo...";
    private static final String emptyHotkey = "";

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
		System.out.println(KeyUtil.asReadable(event));
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
		hotkeyField.setStyle("-fx-control-inner-background: cyan;");
		hotkeyField.setText(defaultPress);
		listener.listenOn(this);
	}

	public void stopListening() {
		hotkeyField.setStyle("-fx-control-inner-background: white;");
		if (hotkeyField.getText() == defaultPress) {
			hotkeyField.setText(emptyHotkey);
		}
		listener.stopListening();
	}

	private void grabFile() {
		File selectedFile = chooser.showOpenDialog(stage);
		if (selectedFile != null) {
			selectionText.setText(selectedFile.getAbsolutePath());
			workFile = selectedFile;
		}
	}

	// --- General Methods --- //

    @Override
    void preload(SoundboardStage parent, Stage stage, Scene scene) {
        super.preload(parent, stage, scene);

        chooser = new FileChooser();
        chooser.setTitle("Choose Audio File");
        chooser.getExtensionFilters().addAll(FileIO.standard_audio, FileIO.all_files);

        try {
            listener = new EntryListener();
        } catch (NativeHookException nhe) {
            nhe.printStackTrace(); // TODO consider merging the error dialog method with printing an exception
            parent.throwBlockingError(nhe.getMessage());
        }

		logger.log(Level.INFO, "Entry GUI controller initialized");
    }

	public void start(Entry starter) {
		if (starter != null) {
			hotkeyField.setStyle("-fx-control-inner-background: white;");
			selectionText.setText(starter.getFile().getAbsolutePath());
			hotkeyField.setText(starter.getCombo().toString());
			nativeEvent = starter.getCombo().getNative();
			workFile = starter.getFile();
			logger.log(Level.INFO, "Started editing entry" + starter.toString());
			stage.show();
		} else {
			start();
		}
	}

	public void start() {
		hotkeyField.setStyle("-fx-control-inner-background: white;");
		selectionText.setText(defaultSelect);
		hotkeyField.setText(emptyHotkey);
		nativeEvent = null;
		workFile = null;
		logger.log(Level.INFO, "Starting new entry");
		stage.show();
	}

	@Deprecated
	public void stop(Entry ender) {
		if (ender != null) {
			stopListening();
			parent.getModel().getEntries().add(ender);
			logger.log(Level.INFO, "Finished and returned entry to soundboard");
			stage.close();
		} else {
            //parent.throwBlockingError("Model argument is null!");
            logger.log(Level.WARNING, "Model argument is null!");
		}
	}

	public void stop() {
		if (workFile != null && nativeEvent != null) {
			stopListening();
			parent.getModel().getEntries().add(new Entry(workFile, nativeEvent));
			logger.log(Level.INFO, "Finished and added new entry to soundboard");
			stage.close();
		} else {
		    //parent.throwBlockingError("Not all fields have been filled!");
			logger.log(Level.WARNING, "Not all fields have been filled!");
		}
	}

}
