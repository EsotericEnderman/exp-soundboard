package gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController extends GuiController {


	// --- GUI Objects --- //

	@FXML
	private TextField stopAllField;

	@FXML
	private TextField playbackComboField;

	@FXML
	private Spinner<?> playbackMultField;

	@FXML
	private TextField speedIncField;

	@FXML
	private TextField speedDecField;

	@FXML
	private TextField pushToTalkField;

	@FXML
	private CheckBox overlapSameCheck;

	@FXML
	private TextField overlapSameField;

	@FXML
	private ComboBox<?> microphoneCombo;

	@FXML
	private ComboBox<?> audioCableCombo;

	@FXML
	private CheckBox updateCheckBox;

	@FXML
	private Button websiteButton;

	@FXML
	private Button updateCheckButton;

	@FXML
	void initialize() {
		assert stopAllField != null : "fx:id=\"stopAllField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert playbackComboField != null : "fx:id=\"playbackComboField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert playbackMultField != null : "fx:id=\"playbackMultField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert speedIncField != null : "fx:id=\"speedIncField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert speedDecField != null : "fx:id=\"speedDecField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert pushToTalkField != null : "fx:id=\"pushToTalkField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert overlapSameCheck != null : "fx:id=\"overlapSameCheck\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert overlapSameField != null : "fx:id=\"overlapSameField\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert microphoneCombo != null : "fx:id=\"microphoneCombo\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert audioCableCombo != null : "fx:id=\"audioCableCombo\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert updateCheckBox != null : "fx:id=\"updateCheckBox\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert websiteButton != null : "fx:id=\"websiteButton\" was not injected: check your FXML file 'settings_jfx.fxml'.";
		assert updateCheckButton != null : "fx:id=\"updateCheckButton\" was not injected: check your FXML file 'settings_jfx.fxml'.";
	}

}
