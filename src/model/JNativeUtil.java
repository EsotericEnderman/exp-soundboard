package model;

import java.awt.Toolkit;

import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;

public class JNativeUtil {

	public static final String spacer = " + ";

	public static String modifiersAsReadable(int modifierFlags) {
		StringBuilder param = new StringBuilder(255);

		if ((modifierFlags & NativeInputEvent.SHIFT_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.shift", "Shift"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.CTRL_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.control", "Ctrl"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.META_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.meta", "Meta"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.ALT_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.alt", "Alt"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.BUTTON1_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.button1", "Button1"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.BUTTON2_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.button2", "Button2"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.BUTTON3_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.button3", "Button3"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.BUTTON4_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.button4", "Button4"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.BUTTON5_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.button5", "Button5"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.NUM_LOCK_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.numLock", "Num Lock"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.CAPS_LOCK_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.capsLock", "Caps Lock"));
			param.append(spacer);
		}

		if ((modifierFlags & NativeInputEvent.SCROLL_LOCK_MASK) != 0) {
			param.append(Toolkit.getProperty("AWT.scrollLock", "Scroll Lock"));
			param.append(spacer);
		}

		if (param.length() > 0) {
			// Remove the trailing '+'.
			for (int i = 0; i < spacer.length(); i++) {
				param.deleteCharAt(param.length() - 1);
			}
		}

		return param.toString();
	}
	
	public static String asReadable(NativeKeyEvent nativeEvent) {
		String modifier = modifiersAsReadable(nativeEvent.getModifiers());
		String keyCode = NativeKeyEvent.getKeyText(nativeEvent.getKeyCode());
		
		// TODO clean/change ifs to be more readable and make more sense
		if (modifier.length() > 1) {
			if (!modifier.equals(keyCode)) {
				return modifier + " + " + keyCode;
			} else {
				return keyCode;
			}
		} else {
			return keyCode;
		}
	}
}
