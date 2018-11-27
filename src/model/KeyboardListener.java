package model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.istack.internal.logging.Logger;

import javafx.scene.control.TextInputControl;

public class KeyboardListener implements NativeKeyListener {
	
	public static String asReadable(NativeKeyEvent nativeEvent) {
		return nativeEvent.paramString();
	}

	private TextInputControl display;
	private List<NativeKeyEvent> keys;

	public KeyboardListener() throws NativeHookException {
		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(this);

		Logger nativeLogger = Logger.getLogger(GlobalScreen.class);
		nativeLogger.setLevel(Level.OFF);

		keys = new ArrayList<NativeKeyEvent>();
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
			keys.clear();
			keys.add(nativeEvent);
			display.setText(asReadable(nativeEvent));
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		// do nothing
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		GlobalScreen.unregisterNativeHook();
	}

}
