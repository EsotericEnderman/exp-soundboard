package model;

import gui.EntryController;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class EntryListener implements NativeKeyListener {

	private EntryController display;

	public EntryListener() throws NativeHookException {
		if (!GlobalScreen.isNativeHookRegistered()) {
			throw new NativeHookException("Native hook has not been registered!");
		}
	}

	public void listenOn(EntryController display) {
		GlobalScreen.addNativeKeyListener(this);
		this.display = display;
	}

	public void stopListening() {
		GlobalScreen.removeNativeKeyListener(this);
		this.display = null;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		display.setHotkeyText(KeyUtil.asReadable(nativeEvent));
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
