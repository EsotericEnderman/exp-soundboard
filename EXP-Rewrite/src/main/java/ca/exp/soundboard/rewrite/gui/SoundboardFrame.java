package ca.exp.soundboard.rewrite.gui;

import ca.exp.soundboard.rewrite.converter.ConverterFrame;
import ca.exp.soundboard.rewrite.soundboard.*;
import com.apple.eawt.Application;
import com.google.gson.Gson;
import net.miginfocom.swing.MigLayout;
import org.jnativehook.GlobalScreen;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SoundboardFrame extends JFrame {

    public static final float VERSION = 0.6F;

    private static final String TITLE = "EXP SoundboardStage vers. " + VERSION + " | ";
    private static final String LOOK_AND_FEEL = "Nimbus";

    private static final int DEFAULT_CLOSE_OPERATION = 3;

    private static final String STOP_ALL_BUTTON = "Stop All";
    private static final String USE_CHECKBOX = "Use";
    private static final String ADD_BUTTON = "Add";
    private static final String USE_MIC_INJECTOR_CHECKBOX = "Use Mic Injector (see Option -> Settings)";
    private static final String FIRST_OUTPUT_LABEL = "1st Output (e.g. your speakers)";
    private static final String SECOND_OUTPUT_LABEL = "2nd Output (e.g. virtual audio cable \"input\")(optional)";
    private static final String AUTO_HOLD_PTT_KEYS = "Auto-hold PTT key(s)";
    private static final String REMOVE_BUTTON = "Remove";
    private static final String EDIT_BUTTON = "Edit";
    private static final String PLAY_BUTTON = "Play";

    private static final String FILE_MENU = "File";
    private static final String NEW_MENU_ITEM = "New";
    private static final String OPEN_MENU_ITEM = "Open";
    private static final String SAVE_MENU_ITEM = "Save";
    private static final String SAVE_AS_MENU_ITEM = "Save as...";
    private static final String SOURCEFORGE_PAGE_MENU_ITEM = "Sourceforge page";

    private static final String PROJECT_LINK = "https://sourceforge.net/projects/expsoundboard/";

    private static final String QUIT_MENU_ITEM = "Quit";
    
    private static final String OPTION_MENU = "Option";
    private static final String SETTINGS_MENU_ITEM = "Settings";
    private static final String AUDIO_LEVELS_MENU_ITEM = "Audio Levels";
    private static final String AUDIO_CONVERTER_MENU_ITEM = "Audio Converter";

    private static final String AUDIO_CONVERTER_NOT_SUPPORTED_MESSAGE = "Audio Converter currently not supported on Mac OS X";

    public static final Image icon = new ImageIcon(SoundboardFrame.class.getResource("EXP logo.png")).getImage();
    private static final long serialVersionUID = 8934802095461138592L;
    public static Soundboard soundboard;
    public static GlobalKeyMacroListener macroListener;
    public static String micInjectorInputMixerName = "";
    public static String micInjectorOutputMixerName = "";
    public static boolean useMicInjector = false;
    public static JFileChooser filechooser;
    static boolean updateCheck;
    final SoundboardFrame thisFrameInstance;
    public AudioManager audioManager;
    public File testFile;
    private JComboBox<String> secondarySpeakerComboBox;
    private JComboBox<String> primarySpeakerComboBox;
    private JTable table;
    private JCheckBox useSecondaryCheckBox;
    private File currentSoundboardFile = null;

    private JCheckBox useMicInjectorCheckBox;

    private JMenuBar menuBar;

    private JCheckBox autoPptCheckBox;

    public SoundboardFrame() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent exception) {
                exit();
            }
        });

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (LOOK_AND_FEEL.equals(info.getName())) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception) {
            Logger.getLogger(SoundboardFrame.class.getName()).log(Level.SEVERE, null, exception);
        }

        filechooser = new JFileChooser();
        audioManager = new AudioManager();
        soundboard = new Soundboard();

        setDefaultCloseOperation(DEFAULT_CLOSE_OPERATION);
        setTitle(TITLE);
        setIconImage(icon);

        macInit();

        secondarySpeakerComboBox = new JComboBox<>();
        secondarySpeakerComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == 1) {
                    String name = (String) secondarySpeakerComboBox.getSelectedItem();
                    audioManager.setSecondaryOutputMixer(name);
                }
            }
        });

        primarySpeakerComboBox = new JComboBox<>();
        primarySpeakerComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == 1) {
                    String name = (String) primarySpeakerComboBox.getSelectedItem();
                    audioManager.setPrimaryOutputMixer(name);
                }
            }
        });

        JButton stopButton = new JButton(STOP_ALL_BUTTON);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Utils.stopAllClips();
            }
        });

        useSecondaryCheckBox = new JCheckBox(USE_CHECKBOX);
        useSecondaryCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                audioManager.setUseSecondary(useSecondaryCheckBox.isSelected());
            }
        });

        JScrollPane scrollPane = new JScrollPane();

        JButton addButton = new JButton(ADD_BUTTON);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new SoundboardEntryEditor(thisFrameInstance);
            }

        });

        useMicInjectorCheckBox = new JCheckBox(USE_MIC_INJECTOR_CHECKBOX);
        useMicInjectorCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SoundboardFrame.useMicInjector = useMicInjectorCheckBox.isSelected();
                updateMicInjector();
            }
        });

        JLabel firstoutputLabel = new JLabel(FIRST_OUTPUT_LABEL);
        firstoutputLabel.setForeground(Color.DARK_GRAY);

        JLabel secondOutputLabel = new JLabel(SECOND_OUTPUT_LABEL);
        secondOutputLabel.setForeground(Color.DARK_GRAY);

        JSeparator separatorD = new JSeparator();

        autoPptCheckBox = new JCheckBox(AUTO_HOLD_PTT_KEYS);
        autoPptCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                boolean selected = autoPptCheckBox.isSelected();
                Utils.setAutoPTThold(selected);
            }
        });

        table = new JTable();
        table.setSelectionMode(0);
        table.setAutoCreateRowSorter(true);
        table.setModel(new DefaultTableModel(new Object[][] { new Object[2] }, new String[] { "Sound Clip", "HotKeys" }) {
            private static final long serialVersionUID = 1L;

            Class<?>[] columnTypes = { String.class, String.class };
            boolean[] columnEditables = new boolean[2];

            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });

        scrollPane.setViewportView(table);
        getContentPane().setLayout(new MigLayout("gapx  2:4:5, gapy 2:4:5, fillx",
                "[][][][6px][20px][6px][2px][10px][53px][6px][][24px][2px][43px]",
                "[grow,fill][23px][14px][20px][14px][23px][2px][23px]"));

        JButton removeButton = new JButton(REMOVE_BUTTON);

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int selected = table.getSelectedRow();

                if (selected > -1) {
                    int index = getSelectedEntryIndex();
                    SoundboardFrame.soundboard.removeEntry(index);
                    updateSoundboardTable();

                    if (index >= table.getRowCount()) {
                        index--;
                    }

                    if (index >= 0) {
                        table.setRowSelectionInterval(index, index);
                    }
                }
            }
        });
        getContentPane().add(removeButton, "cell 1 1,alignx left,aligny top");

        JButton editButton = new JButton(EDIT_BUTTON);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int selected = table.getSelectedRow();

                if (selected > -1) {
                    int index = getSelectedEntryIndex();
                    System.out.println("index " + index);
                    SoundboardEntry entry = SoundboardFrame.soundboard.getEntry(index);

                    new SoundboardEntryEditor(thisFrameInstance, entry);
                }
            }
        });
        getContentPane().add(editButton, "cell 2 1,alignx left,aligny top");

        JButton btnPlay = new JButton(PLAY_BUTTON);
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selected = table.getSelectedRow();
                if (selected > -1) {
                    int index = getSelectedEntryIndex();
                    SoundboardEntry entry = SoundboardFrame.soundboard.getEntry(index);
                    if (SoundboardFrame.macroListener.isSpeedModKeyHeld()) {
                        entry.play(audioManager, true);
                    } else {
                        entry.play(audioManager, false);
                    }
                }
            }
        });

        getContentPane().add(btnPlay, "cell 10 1,alignx right,aligny top");
        getContentPane().add(useMicInjectorCheckBox, "cell 0 7 7 1,alignx left,aligny top");
        getContentPane().add(autoPptCheckBox, "cell 8 7 6 1,alignx right,aligny top");
        getContentPane().add(separatorD, "cell 0 6 14 1,growx,aligny top");
        getContentPane().add(scrollPane, "cell 0 0 14 1,grow");
        getContentPane().add(primarySpeakerComboBox, "cell 0 3 14 1,growx,aligny top");
        getContentPane().add(secondarySpeakerComboBox, "cell 0 5 12 1,growx,aligny center");
        getContentPane().add(useSecondaryCheckBox, "cell 13 5,alignx left,aligny top");
        getContentPane().add(addButton, "cell 0 1,alignx left,aligny top");
        getContentPane().add(stopButton, "cell 11 1 3 1,alignx right,aligny top");
        getContentPane().add(secondOutputLabel, "cell 0 4 7 1,alignx left,aligny top");
        getContentPane().add(firstoutputLabel, "cell 0 2 7 1,alignx left,aligny top");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu(FILE_MENU);
        menuBar.add(fileMenu);

        JMenuItem newMenuItem = new JMenuItem(NEW_MENU_ITEM);
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fileNew();
            }
        });
        fileMenu.add(newMenuItem);

        JMenuItem openMenuItem = new JMenuItem(OPEN_MENU_ITEM);
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fileOpen();
            }
        });
        fileMenu.add(openMenuItem);

        JSeparator separator = new JSeparator();
        fileMenu.add(separator);

        JMenuItem saveMenuItem = new JMenuItem(SAVE_MENU_ITEM);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(83, 2));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fileSave();
            }
        });
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem(SAVE_AS_MENU_ITEM);
        saveAsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fileSaveAs();
            }
        });
        fileMenu.add(saveAsMenuItem);

        JSeparator jSeparatorB = new JSeparator();
        fileMenu.add(jSeparatorB);

        JMenuItem projectPageMenuItem = new JMenuItem(SOURCEFORGE_PAGE_MENU_ITEM);
        projectPageMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(PROJECT_LINK));
                } catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }
            }
        });
        fileMenu.add(projectPageMenuItem);

        JSeparator separatorA = new JSeparator();
        fileMenu.add(separatorA);

        JMenuItem quitMenuItem = new JMenuItem(QUIT_MENU_ITEM);
        quitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exit();
            }
        });
        fileMenu.add(quitMenuItem);

        JMenu editMenu = new JMenu(OPTION_MENU);
        menuBar.add(editMenu);

        JMenuItem settingsMenuItem = new JMenuItem(SETTINGS_MENU_ITEM);
        settingsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                getSettingsMenu();
            }
        });
        editMenu.add(settingsMenuItem);

        JMenuItem audioLevelsMenuItem = new JMenuItem(AUDIO_LEVELS_MENU_ITEM);
        audioLevelsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AudioLevelsFrame.getInstance().setLocationRelativeTo(thisFrameInstance);
            }
        });
        editMenu.add(audioLevelsMenuItem);

        JSeparator separatorC = new JSeparator();
        editMenu.add(separatorC);

        JMenuItem audioConverterMenuItem = new JMenuItem(AUDIO_CONVERTER_MENU_ITEM);
        audioConverterMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
                    new ConverterFrame();
                } else {
                    JOptionPane.showMessageDialog(null, AUDIO_CONVERTER_NOT_SUPPORTED_MESSAGE, "Feature not supported", 1);
                }
            }
        });

        editMenu.add(audioConverterMenuItem);

        setMinimumSize(new Dimension(400, 500));

        updateSpeakerComboBoxes();
        pack();
        thisFrameInstance = this;
        macroListener = new GlobalKeyMacroListener(this);
        GlobalScreen.addNativeKeyListener(macroListener);
        setLocationRelativeTo(null);
        loadPrefs();
    }

    public static void main(String[] arguments) {
        Utils.initGlobalKeyLibrary();
        Utils.startMp3Decoder();
        new SoundboardFrame().setVisible(true);
    }

    public void updateSoundboardTable() {
        Object[][] entryArray = soundboard.getEntriesAsObjectArrayForTable();

        table.setModel(
                new DefaultTableModel(entryArray, new String[] { "Sound Clip", "HotKeys", "File Locations", "Index" }) {
                    private static final long serialVersionUID = 1L;

                    Class<?>[] columnTypes = { String.class, String.class, String.class, Integer.TYPE };
                    boolean[] columnEditables = new boolean[4];

                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }

                    public boolean isCellEditable(int row, int column) {
                        return columnEditables[column];
                    }
                });

        TableColumnModel columnModel = table.getColumnModel();

        columnModel.getColumn(3).setMinWidth(0);
        columnModel.getColumn(3).setMaxWidth(0);
        columnModel.getColumn(3).setWidth(0);

        table.removeColumn(columnModel.getColumn(2));
    }

    private void fileNew() {
        Utils.stopAllClips();
        saveReminder();

        currentSoundboardFile = null;
        soundboard = new Soundboard();

        updateSoundboardTable();
        setTitle(TITLE);
    }

    private void fileOpen() {
        Utils.stopAllClips();
        saveReminder();

        filechooser.setFileFilter(new JsonFileFilter());

        int session = filechooser.showOpenDialog(null);

        if (session == 0) {
            File jsonFile = filechooser.getSelectedFile();
            open(jsonFile);
        }
    }

    private void fileSave() {
        if (currentSoundboardFile != null) {
            soundboard.saveAsJsonFile(currentSoundboardFile);
        } else {
            fileSaveAs();
        }
    }

    private void fileSaveAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new JsonFileFilter());

        if (currentSoundboardFile != null) {
            fileChooser.setSelectedFile(currentSoundboardFile);
        }

        int session = fileChooser.showSaveDialog(null);

        if (session == 0) {
            File file = fileChooser.getSelectedFile();
            currentSoundboardFile = soundboard.saveAsJsonFile(file);

            setTitle(TITLE + currentSoundboardFile.getName());
        }
    }

    private void getSettingsMenu() {
        SettingsFrame.getInstanceOf().setLocationRelativeTo(this);
    }

    private void open(File jsonfile) {
        if (jsonfile.exists()) {
            Soundboard loadedSoundboard = Soundboard.loadFromJsonFile(jsonfile);

            if (loadedSoundboard != null) {
                soundboard = loadedSoundboard;
                updateSoundboardTable();

                currentSoundboardFile = jsonfile;
                setTitle(TITLE + currentSoundboardFile.getName());
            }
        }
    }

    public void updateSpeakerComboBoxes() {
        String[] outputMixerStringArray = Utils.getMixerNames(audioManager.standardDataLineInfo);

        for (String speaker : outputMixerStringArray) {
            primarySpeakerComboBox.addItem(speaker);
            secondarySpeakerComboBox.addItem(speaker);
        }
    }

    private int getSelectedEntryIndex() {
        int selected = table.getSelectedRow();
        return ((Integer) table.getValueAt(selected, 2));
    }

    public void updateMicInjector() {
        useMicInjectorCheckBox.setSelected(useMicInjector);

        if (useMicInjector) {
            Utils.startMicInjector(micInjectorInputMixerName, micInjectorOutputMixerName);
        } else {
            Utils.stopMicInjector();
        }
    }

    private void savePrefs() {
        Preferences prefs = Utils.PREFERENCES;
        prefs.putBoolean("useSecondSpeaker", useSecondaryCheckBox.isSelected());
        prefs.put("firstSpeaker", (String) primarySpeakerComboBox.getSelectedItem());
        prefs.put("secondSpeaker", (String) secondarySpeakerComboBox.getSelectedItem());

        if (currentSoundboardFile != null) {
            prefs.put("lastSoundboardUsed", currentSoundboardFile.getAbsolutePath());
        }

        prefs.putBoolean("OverlapClipsWhilePlaying", Utils.isOverlapSameClipWhilePlaying());
        prefs.putInt("OverlapClipsKey", Utils.getOverlapSwitchKey());
        prefs.putInt("stopAllKey", Utils.getStopKey());
        prefs.putFloat("modplaybackspeed", Utils.getModifiedPlaybackSpeed());
        prefs.putInt("slowSoundKey", Utils.getModifiedSpeedKey());
        prefs.putInt("modSpeedIncKey", Utils.getModspeedupKey());
        prefs.putInt("modSpeedDecKey", Utils.getModspeeddownKey());
        prefs.putBoolean("updateCheckOnLaunch", updateCheck);
        prefs.put("micInjectorInput", micInjectorInputMixerName);
        prefs.put("micInjectorOutput", micInjectorOutputMixerName);
        prefs.putBoolean("micInjectorEnabled", useMicInjector);
        prefs.putBoolean("autoPPTenabled", Utils.autoPTThold);
        prefs.put("autoPTTkeys", Utils.getPTTkeys().toString());
        prefs.putFloat("primaryOutputGain", AudioManager.getFirstOutputGain());
        prefs.putFloat("secondaryOutputGain", AudioManager.getSecondOutputGain());
        prefs.putFloat("micInjectorOutputGain", Utils.getMicInjectorGain());
    }

    private void loadPrefs() {
        Preferences preferences = Utils.PREFERENCES;

        boolean useSecond = preferences.getBoolean("useSecondSpeaker", false);

        useSecondaryCheckBox.setSelected(useSecond);
        audioManager.setUseSecondary(useSecond);

        String firstspeaker = preferences.get("firstSpeaker", null);
        String secondspeaker = preferences.get("secondSpeaker", null);

        if (firstspeaker != null) {
            primarySpeakerComboBox.setSelectedItem(firstspeaker);
            audioManager.setPrimaryOutputMixer(firstspeaker);
        }

        if (secondspeaker != null) {
            secondarySpeakerComboBox.setSelectedItem(secondspeaker);
            audioManager.setSecondaryOutputMixer(secondspeaker);
        }

        String lastfile = preferences.get("lastSoundboardUsed", null);

        if (lastfile != null) {
            open(new File(lastfile));
        }

        float modSpeed = preferences.getFloat("modplaybackspeed", 0.5F);
        Utils.setModifiedPlaybackSpeed(modSpeed);

        int slowkey = preferences.getInt("slowSoundKey", 35);
        Utils.setModifiedSpeedKey(slowkey);

        int stopkey = preferences.getInt("stopAllKey", 19);
        Utils.setStopKey(stopkey);

        int incKey = preferences.getInt("modSpeedIncKey", 39);
        Utils.setModspeedupKey(incKey);

        int decKey = preferences.getInt("modSpeedDecKey", 37);
        Utils.setModspeeddownKey(decKey);

        updateCheck = preferences.getBoolean("updateCheckOnLaunch", true);

        if (updateCheck) {
            new Thread(new UpdateChecker()).start();
        }

        float firstOutputGain = preferences.getFloat("primaryOutputGain", 0.0F);
        float secondOutputGain = preferences.getFloat("secondaryOutputGain", 0.0F);
        float micinjectorOutputGain = preferences.getFloat("micInjectorOutputGain", 0.0F);

        AudioManager.setFirstOutputGain(firstOutputGain);
        AudioManager.setSecondOutputGain(secondOutputGain);
        Utils.setMicInjectorGain(micinjectorOutputGain);

        micInjectorInputMixerName = preferences.get("micInjectorInput", "");
        micInjectorOutputMixerName = preferences.get("micInjectorOutput", "");
        useMicInjector = preferences.getBoolean("micInjectorEnabled", false);

        updateMicInjector();

        boolean useautoptt = preferences.getBoolean("autoPPTenabled", false);
        autoPptCheckBox.setSelected(useautoptt);

        Utils.setAutoPTThold(useautoptt);
        String autoPTTKeys = preferences.get("autoPTTkeys", null);

        if (autoPTTKeys != null) {
            ArrayList<Integer> keys = Utils.stringToIntArrayList(autoPTTKeys);
            Utils.setPTTkeys(keys);
        }

        Utils.setOverlapSameClipWhilePlaying(preferences.getBoolean("OverlapClipsWhilePlaying", true));

        int overlapKey = preferences.getInt("OverlapClipsKey", 36);

        Utils.setOverlapSwitchKey(overlapKey);
    }

    private void exit() {
        Utils.stopAllClips();
        saveReminder();
        savePrefs();
        dispose();
        Utils.deregisterGlobalKeyLibrary();
        System.exit(0);
    }

    private void saveReminder() {
        if (currentSoundboardFile != null) {
            if (currentSoundboardFile.exists()) {
                Gson gson = new Gson();
                Soundboard savedFile = Soundboard.loadFromJsonFile(currentSoundboardFile);
                String savedJSON = gson.toJson(savedFile);
                String currentJSON = gson.toJson(soundboard);

                if (!savedJSON.equals(currentJSON)) {
                    int option = JOptionPane.showConfirmDialog(
                            null,
                            "SoundboardStage has changed. Do you want to save?",
                            "Save Reminder",
                            0);

                    if (option == 0) {
                        soundboard.saveAsJsonFile(currentSoundboardFile);
                    }
                }
            }
        } else if (soundboard.getSoundboardEntries().size() > 0) {
            int option = JOptionPane.showConfirmDialog(null, "SoundboardStage has not been saved. Do you want to save?", "Save Reminder", 0);

            if (option == 0) {
                fileSave();
            }
        }
    }

    private void macInit() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            Application application = Application.getApplication();

            application.setDockIconImage(icon);

            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "EXP SoundboardStage");
        }
    }

    private class JsonFileFilter extends FileFilter {
        private static final String DESCRIPTION = ".json SoundboardStage save file";

        private JsonFileFilter() {
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }

            return file.getName().toLowerCase().endsWith(".json");
        }

        public String getDescription() {
            return DESCRIPTION;
        }
    }
}
