package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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

public class EntryController {
	
	private static final String defaultSelect = "None Selected";
	private static final String defaultPress = "Press any key or key Combo...";

	private Stage window;
	private Scene self;
	private Soundboard parent;
	private FileChooser chooser;
	
	private File newFile;

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

	public EntryController() {
		// TODO Auto-generated constructor stub
	}

	public void start(EntryModel starter) {
		if (starter != null) {
			// grab values from the entry, put them into the gui components
			Entry data = starter.getEntry();
			selectionText.setText(data.getFile().getAbsolutePath());
			hotkeyField.setText(data.toString());
		} else {
			selectionText.setText(defaultSelect);
			hotkeyField.setText("");
		}
		window.show();
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize(Soundboard parent, Scene self, Stage window) {
		assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert selectionText != null : "fx:id=\"selectionText\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert hotkeyField != null : "fx:id=\"hotkeyField\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		hotkeyField.setContextMenu(null);

		this.parent = parent;
		this.self = self;
		this.window = window;
	}

	@FXML
	void onDonePressed(ActionEvent event) {
		// package fields into new Entry
		// based on new or edit flag, add or replace
		window.hide();
	}

	@FXML
	void onFieldClicked(MouseEvent event) {
		// color field to indicate listening, start listening for mouse input
		if (event.isPrimaryButtonDown()) {
			hotkeyField.setStyle("-fx-control-inner-background: cyan;");
			hotkeyField.setText(defaultPress);
			parent.listener.listenOn(hotkeyField);
			// TODO give output for key listener
		} else if (event.isSecondaryButtonDown()) {
			parent.listener.stopListening();
			hotkeyField.setStyle("-fx-control-inner-background: white;");
			if (hotkeyField.getText() == defaultPress) {
				hotkeyField.setText("");
			}
			// TODO remove output from key listener
		} else {
			
		}
	}

	@FXML
	void onSelectClicked(ActionEvent event) {
		chooser = new FileChooser();
		chooser.setTitle("Choose Audio File");
		chooser.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
				new ExtensionFilter("All Files", "*.*"));
		File selectedFile = chooser.showOpenDialog(window);
		if (selectedFile != null) {
			selectionText.setText(selectedFile.getAbsolutePath());
			newFile = selectedFile;
			System.out.println(selectedFile.getAbsolutePath());
		}
	}
}
