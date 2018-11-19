package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Soundboard extends Application {

	public Pane mainMenu;
	public Pane entryMenu;
	public Pane settings;
	public Pane converter;

	public MenuController menuController;
	public SettingsController settingsController;
	public EntryController entryController;
	public ConverterController converterController;

	public static void main(String[] args) {
		launch(Soundboard.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
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

		Scene scene = new Scene(mainMenu);
		primaryStage.setScene(scene);
		primaryStage.show();

		menuController.initialize();
		settingsController.initialize();
		entryController.initialize();
		converterController.initialize();
	}

}
