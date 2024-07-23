package ca.exp.soundboard.rewrite.gui;

import ca.exp.soundboard.rewrite.soundboard.MicInjector;
import ca.exp.soundboard.rewrite.soundboard.UpdateChecker;
import ca.exp.soundboard.rewrite.soundboard.Utils;
import net.miginfocom.swing.MigLayout;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

public class SettingsFrame extends JFrame {

    private static final long serialVersionUID = -4758092886690912749L;

    private static final String TITLE = "Settings";

    private static final int DEFAULT_CLOSE_OPERATION = 2;

    private static final String STOP_ALL_SOUNDS_LABEL = "'Stop All Sounds' hotkey:";

    private static final String FONT_NAME = "Tahoma";
    private static final int FONT_STYLE = 1;
    private static final int FONT_SIZE = 11;

    private static final String CHECK_FOR_UPDATE_ON_LAUNCH_CHECKBOX = "Check for update on launch.";
    private static final String CHECK_FOR_UPDATE_BUTTON = "Check for Update";
    private static final String NO_UPDATES_TEXT = "No Updates";
    private static final String PROJECT_WEBSITE_BUTTON = "Project Website";
    private static final String PROJECT_WEBSITE_URL = "https://sourceforge.net/projects/expsoundboard/";

    private static final String COPYRIGHT = " © Expenosa. 2014.";

    private static final String MIC_INJECTOR_SETTINGS_LABEL = "Mic Injector settings:";
    private static final String MICROPHONE_LABEL = "Microphone:";
    private static final String VIRTUAL_AUDIO_CABLE_LABEL = "Virtual Audio Cable:";
    private static final String MIC_INJECTOR_LABEL = "*Use Mic Injector when your using a virtual audio cable as your input on other software.";

    private static final String VERSION_LABEL = "Version: " + SoundboardFrame.VERSION;

    private static final String MODIFIED_PLAYBACK_SPEED_COMBO_KEY = "'Modified playback speed' combo key:";

    private static final String MODIFIED_PLAYBACK_SPEED_MULTIPLIER_LABEL = "Modified playback speed multiplier:";

    private static final String MODIFIER_SPEED_INCREMENT_HOTKEY_LABEL = "Modifier speed Increment hotkey:";
    private static final String MODIFIER_SPEED_DECREMENT_HOTKEY_LABEL = "Modifier speed Decrement hotkey:";

    private static final String PUSH_TO_TALK_LABEL = "VoIP 'Push To Talk' Key(s): ";

    private static final String OVERLAP_SAME_FILES_LABEL = "Overlap same sound file:";

    public static SettingsFrame instance = null;

    private JTextField stopAllTextField;
    private StopKeyNativeKeyInputGetter stopKeyInputGetter;
    private ModSpeedKeyNativeKeyInputGetter slowKeyInputGetter;
    private IncKeyNativeKeyInputGetter increaseKeyInputGetter;
    private DecKeyNativeKeyInputGetter decreaseKeyInputGetter;
    private PttKeysNativeKeyInputGetter pttKeysInputGetter;
    private OverlapSwitchNativeKeyInputGetter overlapKeyInputGetter;
    private JComboBox<String> micComboBox;
    private JComboBox<String> vacComboBox;
    private JTextField slowKeyTextField;
    private JSpinner modSpeedSpinner;
    private JTextField increaseModSpeedHotKeyTextField;
    private JTextField decreaseModSpeedHotKeyTextField;
    private JTextField pttKeysTextField;
    private JCheckBox overlapClipsCheckbox;
    private JTextField overlapHotkeyTextField;

    private SettingsFrame() {
        getContentPane().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                focusLostOnItems();
            }
        });
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent event) {
            }

            public void windowLostFocus(WindowEvent event) {
                focusLostOnItems();
            }
        });
        setResizable(false);

        stopKeyInputGetter = new StopKeyNativeKeyInputGetter();
        slowKeyInputGetter = new ModSpeedKeyNativeKeyInputGetter();
        increaseKeyInputGetter = new IncKeyNativeKeyInputGetter();
        decreaseKeyInputGetter = new DecKeyNativeKeyInputGetter();
        pttKeysInputGetter = new PttKeysNativeKeyInputGetter();
        overlapKeyInputGetter = new OverlapSwitchNativeKeyInputGetter();

        setDefaultCloseOperation(DEFAULT_CLOSE_OPERATION);
        setTitle(TITLE);

        JLabel stopAllSoundsLabel = new JLabel(STOP_ALL_SOUNDS_LABEL);
        stopAllSoundsLabel.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));

        stopAllTextField = new JTextField();
        stopAllTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                GlobalScreen.removeNativeKeyListener(stopKeyInputGetter);
                stopAllTextField.setBackground(Color.WHITE);
            }
        });
        stopAllTextField.setEditable(false);
        stopAllTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                stopAllTextField.setBackground(Color.CYAN);
                GlobalScreen.addNativeKeyListener(stopKeyInputGetter);
            }
        });
        stopAllTextField.setColumns(10);

        final JCheckBox checkForUpdateCheckbox = new JCheckBox(CHECK_FOR_UPDATE_ON_LAUNCH_CHECKBOX);
        checkForUpdateCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SoundboardFrame.updateCheck = !SoundboardFrame.updateCheck;
                checkForUpdateCheckbox.setSelected(SoundboardFrame.updateCheck);
            }
        });
        checkForUpdateCheckbox.setSelected(SoundboardFrame.updateCheck);
        final JButton checkForUpdateButton = new JButton(CHECK_FOR_UPDATE_BUTTON);
        checkForUpdateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (UpdateChecker.isUpdateAvailable()) {
                    SwingUtilities.invokeLater(new UpdateChecker());
                } else {
                    checkForUpdateButton.setText(NO_UPDATES_TEXT);
                }

            }
        });
        JLabel lblExpenosa = new JLabel(COPYRIGHT);

        JButton btnProjectWebsite = new JButton(PROJECT_WEBSITE_BUTTON);
        btnProjectWebsite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(PROJECT_WEBSITE_URL));
                } catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }
        });

        JSeparator separatorA = new JSeparator();
        separatorA.setForeground(Color.BLACK);

        JSeparator separatorB = new JSeparator();
        separatorB.setForeground(Color.BLACK);

        JLabel micInjectorSettingsLabel = new JLabel(MIC_INJECTOR_SETTINGS_LABEL);
        micInjectorSettingsLabel.setForeground(Color.RED);

        JLabel microphoneLabel = new JLabel(MICROPHONE_LABEL);

        micComboBox = new JComboBox<>();

        JLabel virtualAudioCableLabel = new JLabel(VIRTUAL_AUDIO_CABLE_LABEL);

        vacComboBox = new JComboBox<>();

        JLabel useMicInjectorLabel = new JLabel(MIC_INJECTOR_LABEL);
        useMicInjectorLabel.setFont(new Font(FONT_NAME, 2, 10));

        JLabel versionLabel = new JLabel(VERSION_LABEL);

        JLabel modifiedPlaybackSpeedLabel = new JLabel(MODIFIED_PLAYBACK_SPEED_COMBO_KEY);

        slowKeyTextField = new JTextField();
        slowKeyTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                GlobalScreen.removeNativeKeyListener(slowKeyInputGetter);
                slowKeyTextField.setBackground(Color.WHITE);
            }
        });
        slowKeyTextField.setEditable(false);
        slowKeyTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                slowKeyTextField.setBackground(Color.CYAN);
                GlobalScreen.addNativeKeyListener(slowKeyInputGetter);
            }
        });
        slowKeyTextField.setColumns(10);

        JLabel modifiedPlaybackLabel = new JLabel(MODIFIED_PLAYBACK_SPEED_MULTIPLIER_LABEL);

        modSpeedSpinner = new JSpinner();
        modSpeedSpinner.setModel(new SpinnerNumberModel((Float) Utils.getModifiedPlaybackSpeed(), (Float) Utils.MINIMUM_MODIFIED_SPEED, (Float) Utils.MAXIMUM_MODIFIED_SPEED, (Float) Utils.MINIMUM_MODIFIED_SPEED));
        JComponent jComponent = modSpeedSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) jComponent.getComponent(0);
        field.setEditable(false);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        modSpeedSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                float speed = (float) modSpeedSpinner.getValue();
                if (speed >= Utils.MINIMUM_MODIFIED_SPEED && speed <= Utils.MAXIMUM_MODIFIED_SPEED) {
                    Utils.setModifiedPlaybackSpeed(speed);
                }

            }
        });

        increaseModSpeedHotKeyTextField = new JTextField();
        increaseModSpeedHotKeyTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                GlobalScreen.removeNativeKeyListener(increaseKeyInputGetter);
                increaseModSpeedHotKeyTextField.setBackground(Color.WHITE);
            }
        });
        increaseModSpeedHotKeyTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                GlobalScreen.addNativeKeyListener(increaseKeyInputGetter);
                increaseModSpeedHotKeyTextField.setBackground(Color.CYAN);
            }
        });
        increaseModSpeedHotKeyTextField.setEditable(false);
        increaseModSpeedHotKeyTextField.setColumns(10);

        decreaseModSpeedHotKeyTextField = new JTextField();
        decreaseModSpeedHotKeyTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                decreaseModSpeedHotKeyTextField.setBackground(Color.WHITE);
                GlobalScreen.removeNativeKeyListener(decreaseKeyInputGetter);
            }
        });
        decreaseModSpeedHotKeyTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                GlobalScreen.addNativeKeyListener(decreaseKeyInputGetter);
                decreaseModSpeedHotKeyTextField.setBackground(Color.CYAN);
            }
        });
        decreaseModSpeedHotKeyTextField.setEditable(false);
        decreaseModSpeedHotKeyTextField.setColumns(10);

        JLabel modifierSpeedIncrementHotkeyLabel = new JLabel(MODIFIER_SPEED_INCREMENT_HOTKEY_LABEL);
        JLabel modifierSpeedDecrementHotkeyLabel = new JLabel(MODIFIER_SPEED_DECREMENT_HOTKEY_LABEL);

        JLabel pushToTalkLabel = new JLabel(PUSH_TO_TALK_LABEL);
        pushToTalkLabel.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));

        pttKeysTextField = new JTextField();
        pttKeysTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                pttKeysTextField.setBackground(Color.WHITE);
                pttKeysTextField.removeKeyListener(pttKeysInputGetter);
            }
        });
        pttKeysTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                pttKeysTextField.removeKeyListener(pttKeysInputGetter);
                pttKeysTextField.addKeyListener(pttKeysInputGetter);
                pttKeysTextField.setBackground(Color.CYAN);
            }
        });
        pttKeysTextField.setEditable(false);
        pttKeysTextField.setColumns(10);
        pttKeysTextField.setFocusTraversalKeysEnabled(false);

        JLabel overlapSameSoundLabel = new JLabel(OVERLAP_SAME_FILES_LABEL);
        overlapSameSoundLabel.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));

        overlapClipsCheckbox = new JCheckBox("");
        overlapClipsCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                boolean selected = overlapClipsCheckbox.isSelected();
                Utils.setOverlapSameClipWhilePlaying(selected);
            }
        });
        setIconImage(SoundboardFrame.icon);

        String[] inputMixers = MicInjector.getMixerNames(MicInjector.targetDataLineInfo);
        String[] outputMixers = MicInjector.getMixerNames(MicInjector.sourceDataLineInfo);

        for (String inputMixer : inputMixers) {
            micComboBox.addItem(inputMixer);
        }

        for (String outputMixer : outputMixers) {
            vacComboBox.addItem(outputMixer);
        }

        micComboBox.setSelectedItem(SoundboardFrame.micInjectorInputMixerName);
        vacComboBox.setSelectedItem(SoundboardFrame.micInjectorOutputMixerName);
        micComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == 1) {
                    updateMicInjectorSettings();
                }
            }
        });
        vacComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == 1) {
                    updateMicInjectorSettings();
                }

            }
        });
        stopAllTextField.setText(NativeKeyEvent.getKeyText(Utils.getStopKey()));
        slowKeyTextField.setText(NativeKeyEvent.getKeyText(Utils.getModifiedSpeedKey()));
        increaseModSpeedHotKeyTextField.setText(NativeKeyEvent.getKeyText(Utils.getModspeedupKey()));
        decreaseModSpeedHotKeyTextField.setText(NativeKeyEvent.getKeyText(Utils.getModspeeddownKey()));
        pttKeysInputGetter.updateTextField();
        overlapClipsCheckbox.setSelected(Utils.isOverlapSameClipWhilePlaying());
        getContentPane().setLayout(new MigLayout("", "[101px][20px][45px][13px][71px][4px][34px,grow][10px][135px]",
                "[20px][20px][20px][20px][20px][20px][21px][][2px][14px][20px][20px][13px][2px][14px][23px]"));
        getContentPane().add(stopAllSoundsLabel, "cell 0 0 3 1,alignx left,aligny center");
        getContentPane().add(modifiedPlaybackSpeedLabel, "cell 0 1 5 1,growx,aligny center");
        getContentPane().add(modifiedPlaybackLabel, "cell 0 2 3 1,alignx left,aligny center");
        getContentPane().add(stopAllTextField, "cell 6 0 3 1,growx,aligny top");
        getContentPane().add(slowKeyTextField, "cell 6 1 3 1,growx,aligny top");
        getContentPane().add(modSpeedSpinner, "cell 6 2 3 1,growx,aligny top");

        JLabel overlapSwitchHotkeyLabel = new JLabel("Overlap switch hotkey:");
        getContentPane().add(overlapSwitchHotkeyLabel, "cell 0 7 3 1");

        overlapHotkeyTextField = new JTextField();
        overlapHotkeyTextField.setEditable(false);
        getContentPane().add(overlapHotkeyTextField, "cell 6 7 3 1,growx");
        overlapHotkeyTextField.setColumns(10);
        overlapHotkeyTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                GlobalScreen.addNativeKeyListener(overlapKeyInputGetter);
                overlapHotkeyTextField.setBackground(Color.CYAN);
            }
        });

        overlapHotkeyTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event) {
                overlapHotkeyTextField.setBackground(Color.WHITE);
                GlobalScreen.removeNativeKeyListener(overlapKeyInputGetter);
            }
        });
        overlapHotkeyTextField.setText(NativeKeyEvent.getKeyText(Utils.getOverlapSwitchKey()));

        getContentPane().add(separatorA, "cell 0 13 9 1,growx,aligny top");
        getContentPane().add(checkForUpdateCheckbox, "cell 0 15 3 1,alignx left,aligny top");
        getContentPane().add(btnProjectWebsite, "cell 4 15 3 1,alignx right,aligny top");
        getContentPane().add(checkForUpdateButton, "cell 8 15,alignx right,aligny top");
        getContentPane().add(versionLabel, "cell 0 14,alignx left,aligny top");
        getContentPane().add(lblExpenosa, "cell 8 14,alignx right,aligny top");
        getContentPane().add(micInjectorSettingsLabel, "cell 0 9,alignx left,aligny top");
        getContentPane().add(microphoneLabel, "cell 0 10,alignx left,aligny center");
        getContentPane().add(virtualAudioCableLabel, "cell 0 11,alignx left,aligny center");
        getContentPane().add(vacComboBox, "cell 2 11 7 1,growx,aligny top");
        getContentPane().add(micComboBox, "cell 2 10 7 1,growx,aligny top");
        getContentPane().add(useMicInjectorLabel, "cell 0 12 9 1,alignx left,aligny top");
        getContentPane().add(separatorB, "cell 0 8 9 1,growx,aligny top");
        getContentPane().add(modifierSpeedDecrementHotkeyLabel, "cell 0 4 5 1,growx,aligny center");
        getContentPane().add(modifierSpeedIncrementHotkeyLabel, "cell 0 3 5 1,growx,aligny center");
        getContentPane().add(pushToTalkLabel, "cell 0 5 3 1,alignx left,aligny center");
        getContentPane().add(overlapSameSoundLabel, "cell 0 6 3 1,alignx left,growy");
        getContentPane().add(overlapClipsCheckbox, "cell 6 6,alignx left,aligny top");
        getContentPane().add(pttKeysTextField, "cell 6 5 3 1,growx,aligny top");
        getContentPane().add(decreaseModSpeedHotKeyTextField, "cell 6 4 3 1,growx,aligny top");
        getContentPane().add(increaseModSpeedHotKeyTextField, "cell 6 3 3 1,growx,aligny top");
        pack();
        setVisible(true);
    }

    public static SettingsFrame getInstanceOf() {
        if (instance == null) {
            instance = new SettingsFrame();
        } else {
            instance.setVisible(true);
            instance.requestFocus();
        }

        return instance;
    }

    private void updateMicInjectorSettings() {
        SoundboardFrame.micInjectorInputMixerName = (String) micComboBox.getSelectedItem();
        SoundboardFrame.micInjectorOutputMixerName = (String) vacComboBox.getSelectedItem();
        if (SoundboardFrame.useMicInjector) {
            Utils.startMicInjector(SoundboardFrame.micInjectorInputMixerName,
                    SoundboardFrame.micInjectorOutputMixerName);
        }
    }

    public void updateDisplayedModSpeed() {
        modSpeedSpinner.setValue(Float.valueOf(Utils.getModifiedPlaybackSpeed()));
    }

    public void updateOverlapSwitchCheckBox() {
        overlapClipsCheckbox.setSelected(Utils.isOverlapSameClipWhilePlaying());
    }

    public void dispose() {
        super.dispose();
        GlobalScreen.removeNativeKeyListener(slowKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(stopKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(increaseKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(decreaseKeyInputGetter);
        pttKeysTextField.removeKeyListener(pttKeysInputGetter);
        instance = null;
    }

    private void focusLostOnItems() {
        stopAllTextField.setBackground(Color.WHITE);
        slowKeyTextField.setBackground(Color.WHITE);
        decreaseModSpeedHotKeyTextField.setBackground(Color.WHITE);
        increaseModSpeedHotKeyTextField.setBackground(Color.WHITE);
        overlapHotkeyTextField.setBackground(Color.WHITE);
        pttKeysTextField.setBackground(Color.WHITE);
        pttKeysInputGetter.clearPressedKeys();
        GlobalScreen.removeNativeKeyListener(stopKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(slowKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(increaseKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(decreaseKeyInputGetter);
        GlobalScreen.removeNativeKeyListener(overlapKeyInputGetter);
        pttKeysTextField.removeKeyListener(pttKeysInputGetter);
    }

    private class OverlapSwitchNativeKeyInputGetter implements NativeKeyListener {
        int key = Utils.stopKey;

        private OverlapSwitchNativeKeyInputGetter() {
        }

        private void updateTextField() {
            String keyName = NativeKeyEvent.getKeyText(key);
            overlapHotkeyTextField.setText(keyName);
        }

        public void nativeKeyPressed(NativeKeyEvent event) {
            key = event.getKeyCode();
            Utils.setOverlapSwitchKey(key);
            updateTextField();
        }

        public void nativeKeyReleased(NativeKeyEvent event) {
        }

        public void nativeKeyTyped(NativeKeyEvent event) {
        }
    }

    private class PttKeysNativeKeyInputGetter implements KeyListener {
        HashSet<Integer> pressedKeys = new HashSet<>();

        private PttKeysNativeKeyInputGetter() {
        }

        public void keyPressed(KeyEvent event) {
            int key = event.getKeyCode();
            pressedKeys.add(key);
            Utils.setPTTkeys(pressedKeys);
            updateTextField();
            System.out.println("PPT listener key pressed: " + KeyEvent.getKeyText(key));
        }

        public void keyReleased(KeyEvent event) {
            int key = event.getKeyCode();
            pressedKeys.remove((Integer) key);
            System.out.println("PPT listener key released: " + KeyEvent.getKeyText(key));
        }

        public void keyTyped(KeyEvent event) {
        }

        private synchronized void updateTextField() {
            StringBuilder keyString = new StringBuilder();
            ArrayList<Integer> keys = Utils.getPTTkeys();
            for (int i = 0; i < keys.size(); i++) {
                if (i == 0) {
                    keyString.append(KeyEvent.getKeyText(keys.get(i)));
                } else {
                    keyString.append(" + " + KeyEvent.getKeyText(keys.get(i)));
                }
            }

            pttKeysTextField.setText(keyString.toString());
            System.out.println("PTT listener text field updated");
        }

        private synchronized void clearPressedKeys() {
            pressedKeys.clear();
            System.out.println("PTT listener keys cleared");
        }
    }

    private class ModSpeedKeyNativeKeyInputGetter implements NativeKeyListener {
        int key = Utils.slowKey;

        private ModSpeedKeyNativeKeyInputGetter() {
        }

        public void nativeKeyPressed(NativeKeyEvent event) {
            key = event.getKeyCode();
            Utils.setModifiedSpeedKey(key);
            updateTextField();
        }

        public void nativeKeyReleased(NativeKeyEvent event) {
        }

        public void nativeKeyTyped(NativeKeyEvent event) {
        }

        private synchronized void updateTextField() {
            String keyName = NativeKeyEvent.getKeyText(key);
            slowKeyTextField.setText(keyName);
        }
    }

    private class StopKeyNativeKeyInputGetter implements NativeKeyListener {
        int key = Utils.stopKey;

        private StopKeyNativeKeyInputGetter() {
        }

        public void nativeKeyPressed(NativeKeyEvent event) {
            key = event.getKeyCode();
            Utils.setStopKey(key);
            updateTextField();
        }

        public void nativeKeyReleased(NativeKeyEvent event) {
        }

        public void nativeKeyTyped(NativeKeyEvent event) {
        }

        private synchronized void updateTextField() {
            String keyName = NativeKeyEvent.getKeyText(key);
            stopAllTextField.setText(keyName);
        }
    }

    private class IncKeyNativeKeyInputGetter implements NativeKeyListener {
        int key = Utils.getModspeedupKey();

        private IncKeyNativeKeyInputGetter() {
        }

        public void nativeKeyPressed(NativeKeyEvent event) {
            key = event.getKeyCode();
            Utils.setModspeedupKey(key);
            updateTextField();
        }

        public void nativeKeyReleased(NativeKeyEvent event) {
        }

        public void nativeKeyTyped(NativeKeyEvent event) {
        }

        private synchronized void updateTextField() {
            String keyName = NativeKeyEvent.getKeyText(key);
            increaseModSpeedHotKeyTextField.setText(keyName);
        }
    }

    private class DecKeyNativeKeyInputGetter implements NativeKeyListener {
        int key = Utils.getModspeeddownKey();

        private DecKeyNativeKeyInputGetter() {
        }

        public void nativeKeyPressed(NativeKeyEvent event) {
            key = event.getKeyCode();
            Utils.setModspeeddownKey(key);
            updateTextField();
        }

        public void nativeKeyReleased(NativeKeyEvent event) {
        }

        public void nativeKeyTyped(NativeKeyEvent event) {
        }

        private synchronized void updateTextField() {
            String keyName = NativeKeyEvent.getKeyText(key);
            decreaseModSpeedHotKeyTextField.setText(keyName);
        }
    }
}
