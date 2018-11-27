package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.KeyboardListener;

public class Soundboard extends Application {
	
	public KeyboardListener listener;
	
	// --- GUI Fields --- //

	private Stage menuStage;
	private Stage settingsStage;
	private Stage entryStage;
	private Stage converterStage;

	private Scene mainScene;
	private Scene settingsScene;
	private Scene entryScene;
	private Scene converterScene;

	private Pane mainMenu;
	private Pane settings;
	private Pane entryMenu;
	private Pane converter;

	public MenuController menuController;
	public SettingsController settingsController;
	public EntryController entryController;
	public ConverterController converterController;

	public static void main(String[] args) {
		Application.launch(Soundboard.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("mainmenu_jfx.fxml"));
		mainMenu = loader.<VBox>load();
		menuController = loader.<MenuController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("settings_jfx.fxml"));
		settings = loader.<VBox>load();
		settingsController = loader.<SettingsController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("entrymenu_jfx.fxml"));
		entryMenu = loader.<VBox>load();
		entryController = loader.<EntryController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("converter_jfx.fxml"));
		converter = loader.<Pane>load();
		converterController = loader.<ConverterController>getController();
		
		listener = new KeyboardListener();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainScene = new Scene(mainMenu);
		settingsScene = new Scene(settings);
		entryScene = new Scene(entryMenu);
		converterScene = new Scene(converter);

		menuStage = primaryStage;
		menuStage.setScene(mainScene);
		menuStage.show();

		settingsStage = new Stage();
		settingsStage.setScene(settingsScene);

		entryStage = new Stage();
		entryStage.setScene(entryScene);

		converterStage = new Stage();
		converterStage.setScene(converterScene);

		menuController.initialize(this);
		settingsController.initialize();
		entryController.initialize(this, entryScene, entryStage);
		converterController.initialize();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

}
