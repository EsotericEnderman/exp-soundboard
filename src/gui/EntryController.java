package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Entry;
import model.FeedbackListener;

public class EntryController extends GuiController {

	private static final String defaultSelect = "None Selected";
	private static final String defaultPress = "Press any key or key Combo...";
	private static final String emptyHotkey = "";

	private static final ExtensionFilter standard_audio = new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac");
	private static final ExtensionFilter all_files = new ExtensionFilter("All Files", "*.*");

	private FeedbackListener listener;
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

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert selectionText != null : "fx:id=\"selectionText\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert hotkeyField != null : "fx:id=\"hotkeyField\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";

		chooser = new FileChooser();
		chooser.setTitle("Choose Audio File");
		chooser.getExtensionFilters().addAll(standard_audio, all_files);

		try {
			listener = new FeedbackListener();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
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
	void onSelectClicked(ActionEvent event) {
		grabFile();
	}

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

	public void start(EntryModel starter) {
		if (starter != null) {
			hotkeyField.setStyle("-fx-control-inner-background: white;");
			selectionText.setText(starter.getEntry().getFile().getAbsolutePath());
			hotkeyField.setText(starter.getHotkey());
			nativeEvent = starter.getEntry().getCombo();
			workFile = starter.getEntry().getFile();
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
		stage.show();
	}

	public void stop(EntryModel ender) {
		if (ender != null) {
			stopListening();
			parent.menuController.addEntry(ender);
			stage.close();
		} else {
			new Alert(AlertType.ERROR, "Model argument is null!", ButtonType.OK).showAndWait();
		}
	}

	public void stop() {
		if (workFile != null && nativeEvent != null) {
			stopListening();
			parent.menuController.addEntry(new EntryModel(new Entry(workFile, nativeEvent)));
			stage.close();
		} else {
			new Alert(AlertType.ERROR, "Not all fields have been filled!", ButtonType.OK).showAndWait();
		}
	}

}
