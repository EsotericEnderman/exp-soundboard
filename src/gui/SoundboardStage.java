package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import model.AudioMaster;
import model.Entry;
import model.SoundboardModel;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.*;

public class SoundboardStage extends Application {

	/**
	 * Poll the system for all available audio devices, only keep ones who have a valid output (SourceLine)
	 * @return A list containing all devices with valid output
	 */
	public static List<Mixer.Info> getValidMixers() {

		// list all mixers
		Mixer device;
		Line.Info[] sourceInfos;
		Mixer.Info[] deviceInfos = AudioSystem.getMixerInfo();
		List<Mixer.Info> choices = new ArrayList<Mixer.Info>();
		for (int i = 0; i < deviceInfos.length; i++) {
			device = AudioSystem.getMixer(deviceInfos[i]);
			sourceInfos = device.getSourceLineInfo();

			// keep mixers with 2 or more source lines / output lines
			if (sourceInfos.length > 1) {
				choices.add(deviceInfos[i]);
			}
		}

		return choices;
	}

	private SoundboardModel model;
	private Logger logger;

	// --- GUI Fields --- //

	private Stage menuStage;
	private Stage settingsStage;
	private Stage entryStage;
	private Stage converterStage;
	private Stage levelStage;

	private Scene menuScene;
	private Scene settingsScene;
	private Scene entryScene;
	private Scene converterScene;
	private Scene levelScene;

	private Pane mainMenu;
	private Pane settingsMenu;
	private Pane entryMenu;
	private Pane converterMenu;
	private Pane levelMenu;

	// --- Controllers --- //

	private MenuController menuController;
	private EntryController entryController;
	private SettingsController settingsController;
	private ConverterController converterController;
	private LevelsController levelsController;

	public static void main(String[] args) {
		Application.launch(SoundboardStage.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		startNativeKey();
		// TODO look into calling getParameters() here

		model = new SoundboardModel(2);
		logger = Logger.getLogger(SoundboardStage.class.getName());
		FXMLLoader loader;

		// load fxml files and controllers, pass to fields
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/res/mainmenu_jfx.fxml"));
		mainMenu = loader.<VBox>load();
		menuController = loader.<MenuController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/res/settings_jfx.fxml"));
		settingsMenu = loader.<VBox>load();
		settingsController = loader.<SettingsController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/res/entrymenu_jfx.fxml"));
		entryMenu = loader.<VBox>load();
		entryController = loader.<EntryController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/res/converter_jfx.fxml"));
		converterMenu = loader.<Pane>load();
		converterController = loader.<ConverterController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/res/levels_jfx.fxml"));
		levelMenu = loader.<Pane>load();
		levelsController = loader.<LevelsController>getController();

		logger.log(Level.INFO, "GUI Controllers loaded");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// init JavaFX objects (can only be done in JavaFX thread), initialize with arguments
		menuScene = new Scene(mainMenu);
		menuStage = primaryStage;
		menuStage.setScene(menuScene);
		menuController.preload(this, menuStage, menuScene);
		model.getEntries().addListener(menuController);

		settingsScene = new Scene(settingsMenu);
		settingsStage = new Stage();
		settingsStage.setScene(settingsScene);
		settingsStage.initOwner(menuStage);
		settingsStage.initModality(Modality.WINDOW_MODAL);
		settingsController.preload(this,settingsStage, settingsScene);

		entryScene = new Scene(entryMenu);
		entryStage = new Stage();
		entryStage.setScene(entryScene);
		entryStage.initOwner(menuStage);
		entryStage.initModality(Modality.WINDOW_MODAL);
		entryController.preload(this, entryStage, entryScene);

		converterScene = new Scene(converterMenu);
		converterStage = new Stage();
		converterStage.setScene(converterScene);
		converterStage.initOwner(menuStage);
		converterStage.initModality(Modality.WINDOW_MODAL);
		converterController.preload(this, converterStage, converterScene);

		levelScene = new Scene(levelMenu);
		levelStage = new Stage();
		levelStage.setScene(levelScene);
		levelStage.initOwner(menuStage);
		levelStage.initModality(Modality.WINDOW_MODAL);
		levelsController.preload(this, levelStage, levelScene);

		logger.log(Level.INFO, "GUI Controllers initialized");
		menuController.start();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		menuController.stop(); // TODO: may need to implement a force close
		settingsController.stop();
		entryController.stop();
		converterController.stop();
		stopNativeKey();
	}

	public static void startNativeKey() throws NativeHookException {
		if (!GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.registerNativeHook();
			Logger.getLogger(GlobalScreen.class.getName()).setLevel(Level.OFF);
		} else {
			throw new NativeHookException("Native hook already started!");
		}
	}

	public static void stopNativeKey() throws NativeHookException {
		if (GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.unregisterNativeHook();
		} else {
			throw new NativeHookException("Native hook is not running!");
		}
	}

	public MenuController menuController() {
		return menuController;
	}

	public EntryController entryController() {
		return entryController;
	}

	public SettingsController settingsController() {
		return settingsController;
	}

	public ConverterController converterController() {
		return converterController;
	}

	public LevelsController levelsController() {
		return levelsController;
	}

	public SoundboardModel getModel() {
		return model;
	}

	public boolean playEntry(Entry entry, int[] indices) {
		try {
			AudioMaster master = getModel().getAudio();
			File target = entry.getFile();

			// If the secondary check box is checked, return indices 0 and 1, otherwise just 0
			master.play(target, indices);
			logger.log(Level.INFO, "Played audio file: " + entry.getFile().getName());
			return true;
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException | NullPointerException e) {
			logger.log(Level.WARNING, "Error playing audio file: " + entry.getFile().getName() , e);
			throwBlockingError("Error playing audio file: " + e.getMessage());
			return false;
		}
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
