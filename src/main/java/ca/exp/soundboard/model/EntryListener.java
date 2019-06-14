package ca.exp.soundboard.model;

import ca.exp.soundboard.gui.EntryController;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntryListener implements NativeKeyListener {

	private EntryController display;
	private Logger logger;

	public EntryListener() throws NativeHookException {
		if (!GlobalScreen.isNativeHookRegistered()) {
			throw new NativeHookException("Native hook has not been registered!");
		}

		logger = Logger.getLogger(this.getClass().getName());
		logger.log(Level.INFO, "Entry Listener initialized");
	}

	public void listenOn(EntryController display) {
		logger.log(Level.INFO, "Listening for new hotkey");
		GlobalScreen.addNativeKeyListener(this);
		this.display = display;
	}

	public void stopListening() {
		logger.log(Level.INFO, "Stopping hotkey listening");
		GlobalScreen.removeNativeKeyListener(this);
		this.display = null;
	}

	public boolean isListening() {
		return Objects.nonNull(display);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		display.setHotkeyText(KeyCombination.asReadable(nativeEvent));
		display.setCombo(nativeEvent);
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		// do nothing
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
		// do nothing
	}

}
