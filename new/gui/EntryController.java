package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EntryController {

	@FXML
	private Button selectButton;

	@FXML
	private Label selectionText;

	@FXML
	private TextField hotkeyField;

	@FXML
	private Button doneButton;

	public EntryController() {
		// TODO Auto-generated constructor stub
	}

	@FXML
	void initialize() {
		assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert selectionText != null : "fx:id=\"selectionText\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert hotkeyField != null : "fx:id=\"hotkeyField\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
		assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'entrymenu_jfx.fxml'.";
	}

}
