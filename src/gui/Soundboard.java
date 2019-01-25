package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.sun.istack.internal.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AudioMaster;
import model.Entry;

public class Soundboard extends Application {

	private AudioMaster audio;

	// --- GUI Fields --- //

	private Stage menuStage;
	private Stage settingsStage;
	private Stage entryStage;
	private Stage converterStage;

	private Scene menuScene;
	private Scene settingsScene;
	private Scene entryScene;
	private Scene converterScene;

	private Pane mainMenu;
	private Pane settings;
	private Pane entryMenu;
	private Pane converter;

	// --- Controllers --- //

	private MenuController menuController;
	private EntryController entryController;
	private SettingsController settingsController;
	private ConverterController converterController;

	public static void main(String[] args) {
		Application.launch(Soundboard.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		startNativeKey();
		// TODO look into calling getParameters() here

		audio = new AudioMaster(2);

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
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		menuScene = new Scene(mainMenu);
		settingsScene = new Scene(settings);
		entryScene = new Scene(entryMenu);
		converterScene = new Scene(converter);

		menuStage = primaryStage;
		menuStage.setScene(menuScene);
		menuController.preload(this, menuStage, menuScene);

		settingsStage = new Stage();
		settingsStage.setScene(settingsScene);
		settingsStage.initOwner(menuStage);
		settingsStage.initModality(Modality.WINDOW_MODAL);
		settingsController.preload(this,settingsStage, settingsScene);

		entryStage = new Stage();
		entryStage.setScene(entryScene);
		entryStage.initOwner(menuStage);
		entryStage.initModality(Modality.WINDOW_MODAL);
		entryController.preload(this, entryStage, entryScene);

		converterStage = new Stage();
		converterStage.setScene(converterScene);
		converterStage.initOwner(menuStage);
		converterStage.initModality(Modality.WINDOW_MODAL);
		converterController.preload(this, converterStage, converterScene);

		menuStage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		stopNativeKey();
	}

	public static void startNativeKey() throws NativeHookException {
		if (!GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.registerNativeHook();
			Logger.getLogger(GlobalScreen.class).setLevel(Level.OFF);
		} else {
			throw new NativeHookException("Native hook already started!");
		}
	}

	public static void stopNativeKey() throws NativeHookException {
		if (GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.unregisterNativeHook();
		} else {
			throw new NativeHookException("Native hook already started!");
		}
	}

	public MenuController menu() {
		return menuController;
	}

	public EntryController entry() {
		return entryController;
	}

	public SettingsController settings() {
		return settingsController;
	}

	public ConverterController converter() {
		return converterController;
	}

	public AudioMaster audio() {
		return audio;
	}

	/**
	 * Opens a simple error dialog with an OK button, must be called on the JavaFX Thread.
	 * This method is blocking and will wait for the user to close it.
	 * @param message Text to be shown to the user.
	 */
	public void throwBlockingError(String message) {
		new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
	}

	/**
	 * Opens a simple error dialog with an Ok button, must be called on the JavaFX Thread.
	 * @param message Text to be shown to the user.
	 */
	public void throwError(String message) {
		new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).show();
	}

}
