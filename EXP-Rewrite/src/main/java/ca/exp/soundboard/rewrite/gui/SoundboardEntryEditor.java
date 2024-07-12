package ca.exp.soundboard.rewrite.gui;

import ca.exp.soundboard.rewrite.soundboard.Soundboard;
import ca.exp.soundboard.rewrite.soundboard.SoundboardEntry;
import ca.exp.soundboard.rewrite.soundboard.Utils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class SoundboardEntryEditor extends JFrame {

    private static final long serialVersionUID = -8420285054567246768L;

    private static final String TITLE = "SoundboardStage Entry Editor";

    private static final int DEFAULT_CLOSE_OPERATION = 2;

    private static final String SOUND_CLIP_LABEL = "Sound clip:";
    private static final String NONE_SELECTED_LABEL = "None selected";
    private static final String SELECT_BUTTON = "Select";

    private static final String UNSUPPORTED_FORMAT = "Unsupported Format";

    private static final String HOTKEYS_LABEL = "HotKeys:";

    private static final String NONE_TEXT = "none";

    private static final String DONE_BUTTON = "Done";

    private static final String CLEAR_HOTKEYS_LABEL = "* Right-click to clear hotkeys";

    private static final String MP3_FILE_ENDING = ".mp3";
    private static final String WAV_FILE_ENDING = ".wav";

    public int[] keyNums;
    SoundboardFrame soundboardFrame;
    Soundboard soundboard;
    SoundboardEntry soundboardEntry = null;
    File soundfile;
    private JTextField keysTextField;
    private NativeKeyInputGetter inputGetter;
    private JLabel selectedSoundClipLabel;

    public SoundboardEntryEditor(SoundboardFrame soundboardFrame) {
        this.soundboardFrame = soundboardFrame;

        soundboard = SoundboardFrame.soundboard;
        inputGetter = new NativeKeyInputGetter();

        setDefaultCloseOperation(DEFAULT_CLOSE_OPERATION);
        setTitle(TITLE);
        setIconImage(SoundboardFrame.icon);

        JLabel soundClipLabel = new JLabel(SOUND_CLIP_LABEL);
        selectedSoundClipLabel = new JLabel(NONE_SELECTED_LABEL);
        
        JButton selectButton = new JButton(SELECT_BUTTON);
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser filechooser = Utils.getFileChooser();
                filechooser.setMultiSelectionEnabled(true);
                filechooser.setFileFilter(new SoundboardEntryEditor.AudioClipFileFilter());

                int session = filechooser.showDialog(null, SELECT_BUTTON);
                if (session == 0) {
                    File[] selected = filechooser.getSelectedFiles();
                    if (selected.length > 1) {
                        multiAdd(selected);
                    } else {
                        soundfile = selected[0];
                    }
                    filechooser.setMultiSelectionEnabled(false);
                    if (Utils.isFileSupported(soundfile)) {
                        selectedSoundClipLabel
                                .setText(soundfile.getAbsolutePath());
                    } else {
                        soundfile = null;
                        JOptionPane.showMessageDialog(null, soundfile.getName() + " uses an unsupported codec format.", UNSUPPORTED_FORMAT, 0);
                    }
                }
                filechooser.setMultiSelectionEnabled(false);
                pack();
            }
        });

        JSeparator separator = new JSeparator();

        JLabel lblMacroKeys = new JLabel(HOTKEYS_LABEL);

        keysTextField = new JTextField();
        keysTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (event.getButton() == 1) {
                    keysTextField.setBackground(Color.CYAN);
                    GlobalScreen.addNativeKeyListener(inputGetter);
                    inputGetter.clearPressedKeys();
                } else if (event.getButton() == 3) {
                    keysTextField.setBackground(Color.WHITE);
                    GlobalScreen.removeNativeKeyListener(inputGetter);
                    inputGetter.clearPressedKeys();
                    keyNums = new int[0];
                    keysTextField.setText(NONE_TEXT);
                }
            }
        });

        keysTextField.setText(NONE_TEXT);
        keysTextField.setEditable(false);
        keysTextField.setColumns(10);

        JButton doneButton = new JButton(DONE_BUTTON);
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                submit();
            }
        });

        JLabel clearHotkeysLabel = new JLabel(CLEAR_HOTKEYS_LABEL);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(separator, -1, 414, 32767)
                        .addComponent(selectedSoundClipLabel, -1, 414, 32767)
                        .addGroup(groupLayout.createSequentialGroup().addComponent(soundClipLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(selectButton))
                        .addComponent(lblMacroKeys).addComponent(keysTextField, -1, 414, 32767)
                        .addGroup(GroupLayout.Alignment.TRAILING,
                                groupLayout.createSequentialGroup().addComponent(clearHotkeysLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 311, 32767)
                                        .addComponent(doneButton)))
                        .addContainerGap()));

        groupLayout
                .setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(soundClipLabel).addComponent(selectButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectedSoundClipLabel).addGap(13)
                                .addComponent(separator, -2, -1, -2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(lblMacroKeys)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(keysTextField, -2, -1, -2).addGap(19)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(doneButton).addComponent(clearHotkeysLabel))
                                .addContainerGap(-1, 32767)));

        getContentPane().setLayout(groupLayout);
        getContentPane().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                keysTextField.setBackground(Color.WHITE);
                GlobalScreen.removeNativeKeyListener(inputGetter);
            }
        });

        pack();
        setLocationRelativeTo(soundboardFrame);
        setVisible(true);
    }

    public SoundboardEntryEditor(SoundboardFrame soundboardframe, SoundboardEntry entry) {
        this(soundboardframe);

        soundboardEntry = entry;
        soundfile = new File(entry.getFilePath());
        keyNums = entry.activationKeysNumbers;
        selectedSoundClipLabel.setText(entry.getFilePath());
        keysTextField.setText(entry.getActivationKeysAsReadableString());

        pack();
    }

    private void submit() {
        if (soundfile != null) {
            if (soundboardEntry == null) {
                soundboard.addEntry(soundfile, keyNums);
                soundboardFrame.updateSoundboardTable();
            } else {
                soundboardEntry.setFile(soundfile);
                soundboardEntry.setActivationKeys(keyNums);
                soundboardFrame.updateSoundboardTable();
            }
        }

        dispose();
    }

    private void multiAdd(File[] files) {
        for (File file : files) {
            soundboard.addEntry(file, null);
        }

        soundboardFrame.updateSoundboardTable();
        dispose();
    }

    public void dispose() {
        super.dispose();
        GlobalScreen.removeNativeKeyListener(inputGetter);
    }

    private class NativeKeyInputGetter implements NativeKeyListener {
        int pressedKeys = 0;
        ArrayList<Integer> pressedKeyNums = new ArrayList<>();
        ArrayList<String> pressedKeyNames = new ArrayList<>();

        private NativeKeyInputGetter() {
        }

        public void nativeKeyPressed(NativeKeyEvent event) {
            if (pressedKeys <= 0) {
                pressedKeyNames.clear();
                pressedKeyNums.clear();
            }

            pressedKeys += 1;

            int key = event.getKeyCode();
            String keyname = NativeKeyEvent.getKeyText(key);
            System.out.println("key pressed: " + key + " " + keyname);

            for (int i : pressedKeyNums) {
                if (i == key) {
                    return;
                }
            }

            pressedKeyNums.add(key);
            pressedKeyNames.add(keyname);

            updateTextField();
            int[] macroKeys = new int[pressedKeyNums.size()];

            for (int i = 0; i < macroKeys.length; i++) {
                macroKeys[i] = pressedKeyNums.get(i);
            }

            keyNums = macroKeys;
        }

        public void nativeKeyReleased(NativeKeyEvent event) {
            pressedKeys -= 1;
            if (pressedKeys < 0) {
                pressedKeys = 0;
            }

            int key = event.getKeyCode();
            pressedKeyNums.remove(key);
            pressedKeyNames.remove(NativeKeyEvent.getKeyText(key));
        }

        public void nativeKeyTyped(NativeKeyEvent event) {
        }

        public void clearPressedKeys() {
            pressedKeys = 0;
            pressedKeyNames.clear();
            pressedKeyNums.clear();
        }

        private synchronized void updateTextField() {
            String allKeys = "";
            for (String key : pressedKeyNames) {
                allKeys = allKeys.concat(key + "+");
            }
            allKeys = allKeys.substring(0, allKeys.length() - 1);
            keysTextField.setText(allKeys);
        }
    }

    private class AudioClipFileFilter extends FileFilter {

        private static final String DESCRIPTION = ".mp3 or uncompressed .wav";

        private AudioClipFileFilter() {
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }

            String fileName = file.getName().toLowerCase();
            return fileName.endsWith(MP3_FILE_ENDING) || fileName.endsWith(WAV_FILE_ENDING);
        }

        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
