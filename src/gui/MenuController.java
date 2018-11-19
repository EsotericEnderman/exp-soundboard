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
	private TableView<?> entryTable;

	public MenuController() {
		// TODO Auto-generated constructor stub
	}

	@FXML
	public void initialize() {
		assert newMenuButton != null : "fx:id=\"newMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert openMenuButton != null : "fx:id=\"openMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert closeMenuButton != null : "fx:id=\"closeMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert saveMenuButton != null : "fx:id=\"saveMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert saveAsMenuButton != null : "fx:id=\"saveAsMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert webpageMenuButton != null : "fx:id=\"webpageMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert quitMenuButton != null : "fx:id=\"quitMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert settingsMenuButton != null : "fx:id=\"settingsMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert levelsMenuButton != null : "fx:id=\"levelsMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert converterMenuButton != null : "fx:id=\"converterMenuButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert removeButton != null : "fx:id=\"removeButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert playButton != null : "fx:id=\"playButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert stopButton != null : "fx:id=\"stopButton\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert secondarySpeakerCheck != null : "fx:id=\"secondarySpeakerCheck\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert secondarySpeakerCombo != null : "fx:id=\"secondarySpeakerCombo\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert primarySpeakerCombo != null : "fx:id=\"primarySpeakerCombo\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert injectorCheck != null : "fx:id=\"injectorCheck\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert pttHoldCheck != null : "fx:id=\"pttHoldCheck\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";
		assert entryTable != null : "fx:id=\"EntryTable\" was not injected: check your FXML file 'mainmenu_jfx.fxml'.";

	}
	
	public void onAddPressed() {
		// TODO open add entry menu
	}
	
	public void onRemovePressed() {
		// TODO poll for table selected, notify model if so
	}
	
	public void onEditPressed() {
		// TODO open edit entry menu
	}
	
	public void onPlayPressed() {
		// TODO poll for selected, tell audio man to play
	}

	public void onStopPressed() {
		// TODO tell audio man to stop, regardless of playing audio
	}
	
	public void onNewMenuPressed() {
		// TODO check for unsaved, reset model, 
	}
	
	public void onOpenMenuPressed() {
		// TODO open file picker, send to file parser
	}
	
	public void onSaveMenuPressed() {
		// TODO open file picker, send destination to file parser
	}
	
	public void onSaveAsMenuPressed() {
		
	}
	
	public void onWebpageMenuPressed() {
		
	}
	
	public void onQuitMenuPressed() {
		
	}
	
	public void onSettingsMenuPressed() {
		
	}
	
	public void onLevelsMenuPressed() {
		
	}
	
	public void onConverterMenuPressed() {
		
	}
	
}
