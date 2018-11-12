package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class SettingsController {
	
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

}
