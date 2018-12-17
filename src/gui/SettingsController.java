package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.SettingsListener;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

public class SettingsController extends GuiController {

	private static final String defaultPress = "Press any key or key Combo...";
	private static final String emptyHotkey = "";

	private SettingsListener listener;
	private Object selectedField;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	// --- GUI Objects --- //

	@FXML // fx:id="stopAllField"
	private TextField stopAllField; // Value injected by FXMLLoader

	@FXML // fx:id="playbackComboField"
	private TextField playbackComboField; // Value injected by FXMLLoader

	@FXML // fx:id="playbackMultField"
	private Spinner<?> playbackMultField; // Value injected by FXMLLoader

	@FXML // fx:id="speedIncField"
	private TextField speedIncField; // Value injected by FXMLLoader

	@FXML // fx:id="speedDecField"
	private TextField speedDecField; // Value injected by FXMLLoader

	@FXML // fx:id="pushToTalkField"
	private TextField pushToTalkField; // Value injected by FXMLLoader

	@FXML // fx:id="overlapSameCheck"
	private CheckBox overlapSameCheck; // Value injected by FXMLLoader

	@FXML // fx:id="overlapSameField"
	private TextField overlapSameField; // Value injected by FXMLLoader

	@FXML // fx:id="microphoneCombo"
	private ComboBox<?> microphoneCombo; // Value injected by FXMLLoader

	@FXML // fx:id="audioCableCombo"
	private ComboBox<?> audioCableCombo; // Value injected by FXMLLoader

	@FXML // fx:id="updateCheckBox"
	private CheckBox updateCheckBox; // Value injected by FXMLLoader

	@FXML // fx:id="websiteButton"
	private Button websiteButton; // Value injected by FXMLLoader

	@FXML // fx:id="updateCheckButton"
	private Button updateCheckButton; // Value injected by FXMLLoader

	@FXML // This method is called by the FXMLLoader when initialization is complete
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

		try {
			listener = new SettingsListener();
		} catch (NativeHookException nhe) {
			nhe.printStackTrace();
		}
	}

	@FXML
	void onDecFieldClicked(MouseEvent event) {
		if (event.isPrimaryButtonDown()) {
			listener.listenOn(this, speedDecField);
		} else if (event.isSecondaryButtonDown()) {
			listener.stopListening();
		}
	}

	@FXML
	void onIncFieldClicked(MouseEvent event) {

	}

	@FXML
	void onOverlapFieldClicked(MouseEvent event) {

	}

	@FXML
	void onPlaybackFieldClicked(MouseEvent event) {

	}

	@FXML
	void onPushFieldClicked(MouseEvent event) {

	}

	@FXML
	void onSpeedFieldClicked(MouseEvent event) {

	}

	@FXML
	void onStopFieldClicked(MouseEvent event) {

	}

	@FXML
	void onUpdateButtonPressed(ActionEvent event) {

	}

	@FXML
	void onWebsiteButtonPressed(ActionEvent event) {

	}

	public void setHotkey(NativeKeyEvent nativeEvent) {

	}

}
