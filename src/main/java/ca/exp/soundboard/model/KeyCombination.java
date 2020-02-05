package ca.exp.soundboard.model;

import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class KeyCombination {

    // --- toString methods --- //

    public static final String spacer = " + ";

    public static List<String> modifiersAsReadable(int modifierFlags) {
        // adds all flagged modifiers into a list, for easy formatting downstream
        List<String> mods = new ArrayList<String>();

        if ((modifierFlags & NativeInputEvent.SHIFT_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.shift", "Shift"));
        }

        if ((modifierFlags & NativeInputEvent.CTRL_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.control", "Ctrl"));
        }

        if ((modifierFlags & NativeInputEvent.META_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.meta", "Meta"));
        }

        if ((modifierFlags & NativeInputEvent.ALT_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.alt", "Alt"));
        }

        if ((modifierFlags & NativeInputEvent.BUTTON1_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.button1", "Button1"));
        }

        if ((modifierFlags & NativeInputEvent.BUTTON2_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.button2", "Button2"));
        }

        if ((modifierFlags & NativeInputEvent.BUTTON3_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.button3", "Button3"));
        }

        if ((modifierFlags & NativeInputEvent.BUTTON4_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.button4", "Button4"));
        }

        if ((modifierFlags & NativeInputEvent.BUTTON5_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.button5", "Button5"));
        }

        if ((modifierFlags & NativeInputEvent.NUM_LOCK_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.numLock", "Num Lock"));
        }

        if ((modifierFlags & NativeInputEvent.CAPS_LOCK_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.capsLock", "Caps Lock"));
        }

        if ((modifierFlags & NativeInputEvent.SCROLL_LOCK_MASK) != 0) {
            mods.add(Toolkit.getProperty("AWT.scrollLock", "Scroll Lock"));
        }

        return mods;
    }

    public static String asReadable(NativeKeyEvent nativeEvent) {
        // get the list of modifiers, add on the pressed key and format into 'sum-style' string
        List<String> keys = modifiersAsReadable(nativeEvent.getModifiers());
        keys.add(NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));
        return String.join(spacer, keys);
    }

    // --- Serialization --- //

    static final int netiveKeySize = Integer.BYTES * 5 + Character.BYTES; // size of all relevant nativeKey fields in bytes

    public static KeyCombination fromData(byte[] data) {
        // reads from a byte array each nativeKey field, in reverse order to the other method
        ByteBuffer buffer = ByteBuffer.allocate(netiveKeySize);
        buffer.put(data);

        int raw = buffer.getInt();
        int code = buffer.getInt();
        char key = buffer.getChar();
        int loc = buffer.getInt();
        int id = buffer.getInt();
        int mod = buffer.getInt();

        NativeKeyEvent keyEvent = new NativeKeyEvent(id, mod, raw, code, key, loc);
        return new KeyCombination(keyEvent);
    }

    public static byte[] toData(KeyCombination combo) {
        // assembles all relevant nativeKey fields into a byte array
        NativeKeyEvent key = combo.getNative();
        ByteBuffer buffer = ByteBuffer.allocate(netiveKeySize);

        buffer.putInt(key.getRawCode());
        buffer.putInt(key.getKeyCode());
        buffer.putChar(key.getKeyChar());
        buffer.putInt(key.getKeyLocation());
        buffer.putInt(key.getID());
        buffer.putInt(key.getModifiers());

        buffer.rewind();
        return buffer.array();
    }

    // --- Object Implementation --- //

    private NativeKeyEvent nativeKeyEvent;

    public KeyCombination(NativeKeyEvent nativeKeyEvent) {
        this.nativeKeyEvent = nativeKeyEvent;
    }

    public NativeKeyEvent getNative() {
        return nativeKeyEvent;
    }

    public void setNative(NativeKeyEvent nativeKeyEvent) {
        this.nativeKeyEvent = nativeKeyEvent;
    }

    public boolean equals(KeyCombination obj) {
        NativeKeyEvent compare = obj.getNative();
        if (nativeKeyEvent.getRawCode() != compare.getRawCode()) return false;
        if (nativeKeyEvent.getKeyCode() != compare.getKeyCode()) return false;
        if (nativeKeyEvent.getKeyChar() != compare.getKeyChar()) return false;
        if (nativeKeyEvent.getKeyLocation() != compare.getKeyLocation()) return false;
        if (nativeKeyEvent.getID() != compare.getID()) return false;
        if (nativeKeyEvent.getModifiers() != compare.getModifiers()) return false;
        return true;
    }

    @Override
    public String toString() {
        return asReadable(nativeKeyEvent);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new KeyCombination(nativeKeyEvent);
    }

    public byte[] asData() {
        return KeyCombination.toData(this);
    }
}
