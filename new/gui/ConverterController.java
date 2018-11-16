package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class ConverterController {

	@FXML
	private Button inputSelectButton;

	@FXML
	private Label outputText;

	@FXML
	private Button outputChangeButton;

	@FXML
	private Button convertButton;

	@FXML
	private CheckBox mp3Check;

	@FXML
	private CheckBox wavCheck;

	@FXML
	private Label encodingProgressText;

	@FXML
	private Label encodingMessageText;

	@FXML
	private Label inputText;

	public ConverterController() {
		// TODO Auto-generated constructor stub
	}

	@FXML
	void initialize() {
		assert inputSelectButton != null : "fx:id=\"inputSelectButton\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert outputText != null : "fx:id=\"outputText\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert outputChangeButton != null : "fx:id=\"outputChangeButton\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert convertButton != null : "fx:id=\"convertButton\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert mp3Check != null : "fx:id=\"mp3Check\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert wavCheck != null : "fx:id=\"wavCheck\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert encodingProgressText != null : "fx:id=\"encodingProgressText\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert encodingMessageText != null : "fx:id=\"encodingMessageText\" was not injected: check your FXML file 'converter_jfx.fxml'.";
		assert inputText != null : "fx:id=\"inputText\" was not injected: check your FXML file 'converter_jfx.fxml'.";
	}

}
