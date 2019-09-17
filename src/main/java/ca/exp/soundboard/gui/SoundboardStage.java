package ca.exp.soundboard.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import ca.exp.soundboard.util.ImmediateStreamHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import ca.exp.soundboard.model.AudioMaster;
import ca.exp.soundboard.model.Entry;
import ca.exp.soundboard.model.SoundboardModel;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ca.exp.soundboard.util.LogFormatter;

import javax.sound.sampled.*;

public class SoundboardStage extends Application {

	static Logger rootLogger = Logger.getLogger("");

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

		// remove default handler(s) from global logger, partially disables console output
		for (Handler handler : rootLogger.getHandlers()) {
			//handler.setLevel(Level.OFF);
			rootLogger.removeHandler(handler);
		}

		/// The following handlers split logs by Level, anything below warning goes to System.out, the rest to System.err

		// logging to System.out
		ImmediateStreamHandler outHand = new ImmediateStreamHandler(System.out, new LogFormatter());
		outHand.setFilter(new Filter() {
			@Override
			public boolean isLoggable(LogRecord record) {
				return record.getLevel().intValue() < Level.WARNING.intValue();
			}
		});
		rootLogger.addHandler(outHand);

		// logging to System.err
		ImmediateStreamHandler errHand = new ImmediateStreamHandler(System.err, new LogFormatter());
		errHand.setFilter(new Filter() {
			@Override
			public boolean isLoggable(LogRecord record) {
				return !(record.getLevel().intValue() < Level.WARNING.intValue());
			}
		});
		rootLogger.addHandler(errHand);

		// logging System.out to file
		try {
			// logging to file
			FileHandler fHand = new FileHandler("stdout.txt");
			fHand.setFormatter(new LogFormatter());
			fHand.setFilter(new Filter() {
				@Override
				public boolean isLoggable(LogRecord record) {
					return record.getLevel().intValue() < Level.WARNING.intValue();
				}
			});
			rootLogger.addHandler(fHand);
		} catch (IOException ioe) {
			rootLogger.log(Level.SEVERE, "Failed starting log to stdout.txt", ioe);
		}

		// logging System.err to file
		try {
			// logging to file
			FileHandler fHand = new FileHandler("stderr.txt");
			fHand.setFormatter(new LogFormatter());
			fHand.setLevel(Level.WARNING);
			rootLogger.addHandler(fHand);
		} catch (IOException ioe) {
			rootLogger.log(Level.SEVERE, "Failed starting log to stdout.txt", ioe);
		}

		// setting the jnativehook logger to only log what's important
		Logger nativeLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		nativeLogger.setLevel(Level.WARNING);

		// Adding a filter to chop off the extra line ending off of jnativehook logs
		nativeLogger.setFilter(new Filter() {
			@Override
			public boolean isLoggable(LogRecord record) {
				String in = record.getMessage();
				String out = in.substring(0, in.length() - 1);
				record.setMessage(out);
				return true;
			}
		});

		// Launch the GUI
		Application.launch(SoundboardStage.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		startNativeKey();

		// TODO look into calling getParameters() here

		model = new SoundboardModel(2);
		logger = Logger.getLogger(this.getClass().getName());
		FXMLLoader loader;

		logger.log(Level.INFO, "Loading GUI controllers");

		// load fxml files and controllers, pass to fields
		loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource("mainmenu_jfx.fxml"));
		mainMenu = loader.<VBox>load();
		menuController = loader.<MenuController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource(("settings_jfx.fxml")));
		settingsMenu = loader.<VBox>load();
		settingsController = loader.<SettingsController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource(("entrymenu_jfx.fxml")));
		entryMenu = loader.<VBox>load();
		entryController = loader.<EntryController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource(("converter_jfx.fxml")));
		converterMenu = loader.<Pane>load();
		converterController = loader.<ConverterController>getController();

		loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource(("levels_jfx.fxml")));
		levelMenu = loader.<Pane>load();
		levelsController = loader.<LevelsController>getController();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		logger.log(Level.INFO, "Initializing GUI controllers");

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

		menuController.start();
	}

	@Override
	public void stop() throws Exception { // TODO: each controller reports it's being force closed twice?
		super.stop();
		menuController.forceStop();
		entryController.forceStop();
		settingsController.forceStop();
		converterController.forceStop();
		stopNativeKey();
	}

	private static void startNativeKey() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException nhe) {
			rootLogger.log(Level.SEVERE, "Failed to register jnativehook!", nhe);
		}
	}

	private static void stopNativeKey() {
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException nhe) {
			rootLogger.log(Level.SEVERE, "Failed to unregister jnativehook!", nhe);
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

	// Convenience method, makes the code for playing audio from a controller smaller
	public boolean playEntry(Entry entry, int[] indices) {
		try {
			AudioMaster master = getModel().getAudio();
			File target = entry.getFile();

			// If the secondary check box is checked, return indices 0 and 1, otherwise just 0
			master.play(target, indices);
			logger.log(Level.INFO, "Played audio file: \"" + entry.getFile().getName() + "\"");
			return true;
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException | NullPointerException e) {
			logger.log(Level.WARNING, "Error playing audio file: " + entry.getFile().getName() , e);
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
