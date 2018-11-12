package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

public class MenuController {

    @FXML
    private MenuItem newMenuButton;

    @FXML
    private MenuItem openMenuButton;

    @FXML
    private MenuItem closeMenuButton;

    @FXML
    private MenuItem saveMenuButton;

    @FXML
    private MenuItem saveAsMenuButton;

    @FXML
    private MenuItem webpageMenuButton;

    @FXML
    private MenuItem quitMenuButton;

    @FXML
    private MenuItem settingsMenuButton;

    @FXML
    private MenuItem levelsMenuButton;

    @FXML
    private MenuItem converterMenuButton;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button editButton;

    @FXML
    private Button playButton;

    @FXML
    private Button stopButton;

    @FXML
    private CheckBox secondarySpeakerCheck;

    @FXML
    private ComboBox<?> secondarySpeakerCombo;

    @FXML
    private ComboBox<?> primarySpeakerCombo;

    @FXML
    private CheckBox injectorCheck;

    @FXML
    private CheckBox pttHoldCheck;

    @FXML
    private TableView<?> EntryTable;

}
