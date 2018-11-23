package test;

import java.util.logging.Level;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.istack.internal.logging.Logger;

public class GlobalKeyListenerTest implements NativeKeyListener {
	
	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
			Logger nativeLogger = Logger.getLogger(GlobalScreen.class);
			nativeLogger.setLevel(Level.OFF);
		} catch (NativeHookException nhe) {
			nhe.printStackTrace();
		}
		
		GlobalScreen.addNativeKeyListener(new GlobalKeyListenerTest());
	}
	
	public String keyToReadable(NativeKeyEvent nativeEvent) {
		String param = nativeEvent.paramString();
		String[] params = param.split(",");
		return null;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		System.out.println(NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));
		System.out.println(NativeKeyEvent.getModifiersText(nativeEvent.getModifiers()));
		System.out.println();
	}
	
	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		//System.out.println("Key Released: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));
	}
	
	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
		//System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));
	}

}
