package ca.exp.soundboard.rewrite.soundboard;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class KeyEventIntConverter {
    public static String getKeyEventText(int keyCode) {
        if (keyCode >= 96 && keyCode <= 105) {
            String numpad = Toolkit.getProperty("AWT.numpad", "NumPad");
            char character = (char) (keyCode - 96 + 48);
            return numpad + " " + character;
        }

        return KeyEvent.getKeyText(keyCode);
    }
}
