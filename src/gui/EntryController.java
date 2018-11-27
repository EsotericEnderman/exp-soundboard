package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.jnativehook.keyboard.NativeKeyEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Entry;
import model.KeyboardListener;

public class EntryController {
	
	private static final String defaultSelect = "None Selected";
	private static final String defaultPress = "Press any key or key Combo...";
	private static final String emptyHotkey = "";

	private Stage window;
	private Soundboard parent;
	private FileChooser chooser;
	
	private File workFile;
	private NativeKeyEvent nativeEvent;
	
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="selectButton"
	private Button selectButton; // Value injected by FXMLLoader

	@FXML // fx:id="selectionText"
	private Label selectionText; // Value injected by FXMLLoader

	@FXML // fx:id="hotkeyField"
	private TextField hotkeyField; // Value injected by FXMLLoader

	@FXML // fx:id="doneButton"
	private Button doneButton; // Value injected by FXMLLoader

	public void start(EntryModel starter) {
		if (starter != null) {
			selectionText.setText(starter.getEntry().getFile().getAbsolutePath());
			hotkeyField.setText(starter.getHotkey());
			workFile = starter.getEntry().getFile();
			nativeEvent = starter.getEntry().getCombo();
			window.show();
		}
	}
	
	public void start() {
		selectionText.setText(defaultSelect);
		hotkeyField.setText(emptyHotkey);
		workFile = null;
		nativeEvent = null;
		window.show();
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize(Soundboard parent, Stage window) {
		assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert selectionText != null : "fx:id=\"selectionText\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert hotkeyField != null : "fx:id=\"hotkeyField\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";

		this.parent = parent;
		this.window = window;
		chooser = new FileChooser();
		chooser.setTitle("Choose Audio File");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
				new ExtensionFilter("All Files", "*.*"));
	}

	@FXML
	void onDonePressed(ActionEvent event) { // TODO make flags to signify the user set file and keycombo correctly
		if (workFile != null && nativeEvent != null) {
			parent.menuController.addEntry(new EntryModel(new Entry(workFile, nativeEvent)));
			window.close();
		}
	}

	@FXML
	void onFieldClicked(MouseEvent event) {
		if (event.isPrimaryButtonDown()) {
			hotkeyField.setStyle("-fx-control-inner-background: cyan;");
			hotkeyField.setText(defaultPress);
			
			parent.listener.listenOn(hotkeyField);
		} else if (event.isSecondaryButtonDown()) {
			parent.listener.stopListening();
			hotkeyField.setStyle("-fx-control-inner-background: white;");
			
			if (hotkeyField.getText() == defaultPress) {
				hotkeyField.setText(emptyHotkey);
			} else {
				nativeEvent = parent.listener.getCombo();
			}
		} else {
			
		}
	}

	@FXML
	void onSelectClicked(ActionEvent event) {
		File selectedFile = chooser.showOpenDialog(window);
		if (selectedFile != null) {
			selectionText.setText(selectedFile.getAbsolutePath());
			workFile = selectedFile;
		}
	}
	
}
