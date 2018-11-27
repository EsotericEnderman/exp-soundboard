package model;

import java.util.logging.Level;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.istack.internal.logging.Logger;

import javafx.scene.control.TextInputControl;

public class KeyboardListener implements NativeKeyListener {

	private TextInputControl display;
	private NativeKeyEvent combo;
	
	public static void start() throws NativeHookException {
		GlobalScreen.registerNativeHook();
		Logger.getLogger(GlobalScreen.class).setLevel(Level.OFF);
	}
	
	public static void stop() throws NativeHookException {
		GlobalScreen.unregisterNativeHook();
	}

	public KeyboardListener() throws NativeHookException {
		if (GlobalScreen.isNativeHookRegistered()) {
		GlobalScreen.addNativeKeyListener(this);
		} else {
			throw new NativeHookException("Native hook has not been registered!");
		}
	}

	public void listenOn(TextInputControl output) {
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
			combo = null;
			combo = nativeEvent;
			display.setText(JNativeUtil.asReadable(nativeEvent));
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		// do nothing
	}
	
	public NativeKeyEvent getCombo() {
		return combo;
	}

}
