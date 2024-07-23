package ca.exp.soundboard.rewrite.soundboard;

import ca.exp.soundboard.rewrite.gui.SoundboardFrame;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.ArrayList;
import java.util.Iterator;

public class GlobalKeyMacroListener implements NativeKeyListener {

    SoundboardFrame soundboardFrame;
    ArrayList<Integer> pressedKeys;

    public GlobalKeyMacroListener(SoundboardFrame soundboardFrame) {
        this.soundboardFrame = soundboardFrame;

        pressedKeys = new ArrayList<Integer>();
    }

    public void nativeKeyPressed(NativeKeyEvent event) {
        int pressed = event.getKeyCode();
        Utils.submitNativeKeyPressTime(NativeKeyEvent.getKeyText(pressed), event.getWhen());
        boolean alreadyPressed = false;
        for (Iterator<Integer> localIterator = pressedKeys.iterator(); localIterator.hasNext();) {
            int i = localIterator.next();
            if (pressed == i) {
                alreadyPressed = true;
                break;
            }
        }

        if (!alreadyPressed) {
            pressedKeys.add(pressed);
        }

        if (pressed == Utils.stopKey) {
            Utils.stopAllClips();
        } else if (pressed == Utils.modspeedupKey) {
            Utils.incrementModSpeedUp();
        } else if (pressed == Utils.modspeeddownKey) {
            Utils.decrementModSpeedDown();
        } else if (pressed == Utils.getOverlapSwitchKey()) {
            boolean overlap = Utils.isOverlapSameClipWhilePlaying();
            Utils.setOverlapSameClipWhilePlaying(!overlap);
        }

        checkMacros();
    }

    public void nativeKeyReleased(NativeKeyEvent event) {
        int released = event.getKeyCode();
        for (int i = 0; i < pressedKeys.size(); i++) {
            if (released == pressedKeys.get(i)) {
                pressedKeys.remove(i);
            }
        }
    }

    public void nativeKeyTyped(NativeKeyEvent event) {
    }

    public boolean isSpeedModKeyHeld() {
        for (Iterator<Integer> localIterator = pressedKeys.iterator(); localIterator.hasNext();) {
            int key = localIterator.next();
            if (key == Utils.slowKey) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Integer> getPressedNativeKeys() {
        return (ArrayList<Integer>) pressedKeys.clone();
    }

    private void checkMacros() {
        boolean modSpeed = isSpeedModKeyHeld();

        ArrayList<SoundboardEntry> potentialCopies = new ArrayList<>();

        for (SoundboardEntry entry : SoundboardFrame.soundboard.getSoundboardEntries()) {
            int[] actKeys = entry.getActivationKeys();

            if (actKeys.length > 0 && entry.matchesPressed(pressedKeys)) {
                potentialCopies.add(entry);
            }
        }

        if (potentialCopies.size() == 1) {
            potentialCopies.get(0).play(soundboardFrame.audioManager, modSpeed);
        } else {
            int highest = 0;

            ArrayList<SoundboardEntry> potentialCopiesList = new ArrayList<SoundboardEntry>(potentialCopies);

            for (SoundboardEntry soundBoardEntry : potentialCopiesList) {
                int matches = soundBoardEntry.matchesHowManyPressed(pressedKeys);

                if (matches > highest) {
                    highest = matches;
                } else if (matches < highest) {
                    potentialCopies.remove(soundBoardEntry);
                }
            }
            potentialCopiesList = new ArrayList<SoundboardEntry>(potentialCopies);

            for (SoundboardEntry potentialCopy : potentialCopiesList) {
                int matches = potentialCopy.matchesHowManyPressed(pressedKeys);

                if (matches < highest) {
                    potentialCopies.remove(potentialCopy);
                }
            }

            for (SoundboardEntry potentialCopy : potentialCopies) {
                potentialCopy.play(soundboardFrame.audioManager, modSpeed);
            }
        }
    }
}
