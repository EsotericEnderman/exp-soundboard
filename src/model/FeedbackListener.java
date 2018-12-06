package model;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import gui.EntryController;

public class FeedbackListener implements NativeKeyListener {

	private EntryController display;

	public FeedbackListener() throws NativeHookException {
		if (GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.addNativeKeyListener(this);
		} else {
			throw new NativeHookException("Native hook has not been registered!");
		}
	}

	public void listenOn(EntryController output) {
		display = output;
	}

	public void stopListening() {
		display = null;
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
		// do nothing
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		if (display != null) {
			display.setHotkeyText(JNativeUtil.asReadable(nativeEvent));
			display.setCombo(nativeEvent);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		// do nothing
	}

}
