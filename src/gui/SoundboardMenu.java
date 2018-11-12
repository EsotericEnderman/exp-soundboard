package gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SoundboardMenu extends Application {
	
	public Pane entryMenu;
	public Pane settings;
	public Pane converter;

	public static void main(String[] args) {
		launch(args);
	}

	public static Pane loadMainMenu() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(SoundboardMenu.class.getResource("mainmenu_jfx.fxml"));
		return loader.<VBox>load();
	}

	public static Pane loadSettingsMenu() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(SoundboardMenu.class.getResource("settings_jfx.fxml"));
		return loader.<Pane>load();
	}

	public static Pane loadEntryMenu() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(SoundboardMenu.class.getResource("entrymenu_jfx.fxml"));
		return loader.<Pane>load();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(loadSettingsMenu());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
