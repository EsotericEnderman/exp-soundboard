package ca.exp.soundboard.remake.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ca.exp.soundboard.remake.util.FileIO;

public class ConverterController extends GuiController {

	private static final String defaultSelect = "None Selected";
	private static final String defaultEncoding = "";
	private static final String defaultProgress = "N/A";

	private FileChooser chooser;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	// --- GUI Objects --- //

	@FXML // fx:id="inputSelectButton"
	private Button inputSelectButton; // Value injected by FXMLLoader

	@FXML // fx:id="outputText"
	private Label outputText; // Value injected by FXMLLoader

	@FXML // fx:id="outputChangeButton"
	private Button outputChangeButton; // Value injected by FXMLLoader

	@FXML // fx:id="convertButton"
	private Button convertButton; // Value injected by FXMLLoader

	@FXML // fx:id="mp3Check"
	private CheckBox mp3Check; // Value injected by FXMLLoader

	@FXML // fx:id="wavCheck"
	private CheckBox wavCheck; // Value injected by FXMLLoader

	@FXML // fx:id="encodingProgressText"
	private Label encodingProgressText; // Value injected by FXMLLoader

	@FXML // fx:id="encodingMessageText"
	private Label encodingMessageText; // Value injected by FXMLLoader

	@FXML // fx:id="inputText"
	private Label inputText; // Value injected by FXMLLoader

	// --- GUI Methods --- //

	@FXML // This method is called by the FXMLLoader when initialization is complete
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

	@FXML
	void onConvertPressed(ActionEvent event) {
		// TODO: implement audio format conversion
	}

	@FXML
	void onMP3Pressed(ActionEvent event) {
		wavCheck.setSelected(!mp3Check.isSelected());
	}

	@FXML
	void onWAVPressed(ActionEvent event) {
		mp3Check.setSelected(!wavCheck.isSelected());
	}

	@FXML
	void onInputPressed(ActionEvent event) {
		grabInput();
	}

	@FXML
	void onOutputPressed(ActionEvent event) {
		grabOutput();
	}

	// --- Interaction Methods --- //

	private void grabOutput() {
		chooser.setTitle("Choose Output File");
		File selectedFile = chooser.showSaveDialog(stage);
		if (selectedFile != null) {
			outputText.setText(selectedFile.getAbsolutePath());
		}
	}

	private void grabInput() {
		chooser.setTitle("Choose Input File");
		File selectedFile = chooser.showOpenDialog(stage);
		if (selectedFile != null) {
			inputText.setText(selectedFile.getAbsolutePath());
		}
	}

	// --- General Methods --- //

	private String getProgress(int value) {
		if (value < 0) return defaultProgress;
		return value + "%";
	}

	@Override
	void preload(SoundboardStage parent, Stage stage, Scene scene) {
		super.preload(parent, stage, scene);
		logger.info( "Initializing converter controller");

		chooser = new FileChooser();
		chooser.getExtensionFilters().addAll(FileIO.standard_audio, FileIO.all_files);
	}

	@Override
	public void reset() {
		init(defaultSelect, defaultSelect, true, -1, defaultEncoding);
	}

	private void init(String input, String output, boolean format, int progress, String encoding) {
		inputText.setText(input);
		outputText.setText(output);
		mp3Check.setSelected(format);
		wavCheck.setSelected(!format);
		encodingProgressText.setText(getProgress(progress));
		encodingMessageText.setText(encoding);
	}

	@Override
	public void start() {
		reset();
		stage.show();
	}

	@Override
	public void stop() {
		stage.close();
	}

}