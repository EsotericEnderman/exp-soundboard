package ca.exp.soundboard.model;

import ca.exp.soundboard.gui.SettingsController;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javafx.scene.control.TextField;

public class SettingsListener implements NativeKeyListener {

    private SettingsController settings;
    private TextField output;

    public SettingsListener() throws NativeHookException {
        if (!GlobalScreen.isNativeHookRegistered()) {
            throw new NativeHookException("Native hook has not been registered!");
        }
    }

    public void listenOn(SettingsController settings, TextField output){
        GlobalScreen.addNativeKeyListener(this);
        this.settings = settings;
        this.output = output;
    }

    public void stopListening() {
        GlobalScreen.removeNativeKeyListener(this);
        this.settings = null;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        settings.setHotkey(nativeEvent);
        output.setText(KeyCombination.asReadable(nativeEvent));
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
