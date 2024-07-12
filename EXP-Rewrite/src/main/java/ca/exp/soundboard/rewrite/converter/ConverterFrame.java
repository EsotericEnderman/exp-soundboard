package ca.exp.soundboard.rewrite.converter;

import ca.exp.soundboard.rewrite.soundboard.Utils;
import ca.exp.soundboard.rewrite.gui.SoundboardFrame;
import it.sauronsoftware.jave.EncoderProgressListener;
import it.sauronsoftware.jave.MultimediaInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ConverterFrame extends JFrame {

    private static final int DEFAULT_CLOSE_OPERATION = 2;

    private static final int CONVERSION_PROGRESS_SCALE = 10;
    private static final int MAXIMUM_CONVERSION_PROGRESS = 1000;
    private static final int ERROR_CONVERSION_PROGRESS = 1001;

    private static final String TITLE = "EXP soundboard : Audio Converter";

    private static final String INPUT_FILES_LABEL = "Input Files:";
    private static final String OUTPUT_FILE_LABEL = "Output File:";

    private static final String ENCODING_COMPLETE_MESSAGE = "Encoding Complete!";
    private static final String ENCODING_FAILED_MESSAGE = "Encoding Failed!";

    private static final String ENCODING_PROGRESS_LABEL = "Encoding Progress:";
    private static final String ENCODING_MESSAGES_LABEL = "Encoding Messages:";

    private static final String SELECT_MESSAGE = "Select";
    private static final String NONE_SELECTED_MESSAGE = "none selected";
    private static final String MULTIPLE_FILES_MESSAGE = "Multiple files";

    private static final String CHANGE_BUTTON = "Change";

    private static final String OUTPUT_FORMAT_LABEL = "Output Format:";

    private static final String MP3_RADIO_BUTTON = "MP3";
    private static final String WAV_RADIO_BUTTON = "WAV";

    private static final String CONVERT_BUTTON = "Convert";

    private static final String INITIAL_CONVERSION_PROGRESS = "0%";
    private static final String INITIAL_CONVERSION_MESSAGE = "";

    private static final String FILE_PARSING_ERROR = "File parsing error";
    private static final String SAME_INPUT_AND_OUTPUT_FILES_MESSAGE = "Input and output files cannot be the same";

    private static final String OVERWRITE_CONFIRMATION = "Overwrite confirmation";
    private static final String OUTPUT_FILE_ALREADY_EXISTS_MESSAGE = "Output file already exists. Overwrite?";

    private static final String CONVERTED = "Converted";

    private static final char PERIOD = '.';
    private static final char FILE_PATH_SEPARATOR = '/';
    private static final char PERCENTAGE_SIGN = '%';

    private static final String MP3_FILE_ENDING = PERIOD + "mp3";
    private static final String WAV_FILE_ENDING = PERIOD + "wav";

    private static final long serialVersionUID = -6720455160041920802L;

    private File[] inputFiles;
    private File outputFile;

    private JLabel inputFileLabel;
    private JLabel outputFileLabel;

    private JButton changeOutputButton;

    private JRadioButton mp3RadioButton;
    private JRadioButton wavRadioButton;

    private JLabel encodingProgressLabel;
    private JLabel encodingMessageLabel;

    private JButton convertButton;

    public ConverterFrame() {
        setResizable(false);
        setDefaultCloseOperation(DEFAULT_CLOSE_OPERATION);
        setTitle(TITLE);
        setIconImage(SoundboardFrame.icon);

        JLabel inputFilesLabel = new JLabel(INPUT_FILES_LABEL);

        inputFileLabel = new JLabel(NONE_SELECTED_MESSAGE);

        JButton selectInputButton = new JButton(SELECT_MESSAGE);

        selectInputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = Utils.getFileChooser();
                fileChooser.setFileFilter(null);
                fileChooser.setMultiSelectionEnabled(true);

                int session = fileChooser.showDialog(null, SELECT_MESSAGE);

                if (session == 0) {
                    inputFiles = fileChooser.getSelectedFiles();

                    if (inputFiles.length > 1) {
                        outputFile = inputFiles[0];
                        inputFileLabel.setText(MULTIPLE_FILES_MESSAGE);
                        renameOutputForFormat();
                        outputFileLabel.setText(outputFile.getAbsolutePath());
                    } else {
                        outputFile = inputFiles[0];
                        renameOutputForFormat();
                        inputFileLabel.setText(inputFiles[0].getAbsolutePath());
                        outputFileLabel.setText(outputFile.getAbsolutePath());
                    }

                    changeOutputButton.setEnabled(true);
                    convertButton.setEnabled(true);
                }

                fileChooser.setMultiSelectionEnabled(false);
                pack();
            }

        });

        JSeparator separator = new JSeparator();

        JLabel outputFileJLabel = new JLabel(OUTPUT_FILE_LABEL);
        outputFileLabel = new JLabel(NONE_SELECTED_MESSAGE);

        changeOutputButton = new JButton(CHANGE_BUTTON);
        changeOutputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (inputFiles != null && outputFile != null) {
                    JFileChooser fileChooser = Utils.getFileChooser();
                    fileChooser.setMultiSelectionEnabled(false);

                    if (inputFiles.length > 1) {
                        fileChooser.setFileFilter(new FileFilter() {
                            private static final String DESCRIPTION = "Folders only";

                            public boolean accept(File file) {
                                return file.isDirectory();
                            }

                            public String getDescription() {
                                return DESCRIPTION;
                            }
                        });

                        fileChooser.setSelectedFile(outputFile);
                        fileChooser.setFileSelectionMode(1);
                    } else {
                        fileChooser.setFileFilter(null);
                        fileChooser.setSelectedFile(outputFile);
                    }

                    int session = fileChooser.showSaveDialog(null);

                    if (session == 0) {
                        outputFile = fileChooser.getSelectedFile();

                        if (inputFiles.length < 2) {
                            renameOutputForFormat();
                        } else {
                            outputFileLabel.setText(outputFile.getAbsolutePath());
                        }
                    }

                    fileChooser.setFileSelectionMode(0);
                    pack();
                }
            }
        });
        changeOutputButton.setEnabled(false);

        JLabel outputFormatLabel = new JLabel(OUTPUT_FORMAT_LABEL);

        mp3RadioButton = new JRadioButton(MP3_RADIO_BUTTON);
        mp3RadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                renameOutputForFormat();
            }
        });
        mp3RadioButton.setSelected(true);

        wavRadioButton = new JRadioButton(WAV_RADIO_BUTTON);
        wavRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                renameOutputForFormat();
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(mp3RadioButton);
        buttonGroup.add(wavRadioButton);

        convertButton = new JButton(CONVERT_BUTTON);
        convertButton.setEnabled(false);
        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                convertAction();
            }
        });

        JSeparator jSeparator = new JSeparator();

        JLabel encodingProgressLabel = new JLabel(ENCODING_PROGRESS_LABEL);
        JLabel encodingMessageLabel = new JLabel(ENCODING_MESSAGES_LABEL);

        encodingProgressLabel = new JLabel(INITIAL_CONVERSION_PROGRESS);
        encodingMessageLabel = new JLabel(INITIAL_CONVERSION_MESSAGE);

        getContentPane().setLayout(new MigLayout("", "[45px][2px][14px][2px][34px][1px][33px][222px][71px]",
                "[14px][23px][2px][14px][23px][14px][23px][2px][14px][14px]"));
        getContentPane().add(jSeparator, "cell 0 7 9 1,growx,aligny top");
        getContentPane().add(separator, "cell 0 2 9 1,growx,aligny top");
        getContentPane().add(inputFilesLabel, "cell 0 0 3 1,alignx left,aligny top");
        getContentPane().add(inputFileLabel, "cell 4 0 3 1,alignx right,aligny top");
        getContentPane().add(selectInputButton, "cell 0 1 3 1,alignx left,aligny top");
        getContentPane().add(outputFileJLabel, "cell 0 3 3 1,alignx left,aligny top");
        getContentPane().add(outputFileLabel, "cell 4 3 3 1,alignx left,aligny top");
        getContentPane().add(changeOutputButton, "cell 0 4 5 1,alignx left,aligny top");
        getContentPane().add(outputFormatLabel, "cell 0 5 5 1,alignx left,aligny top");
        getContentPane().add(mp3RadioButton, "cell 0 6,alignx left,aligny top");
        getContentPane().add(wavRadioButton, "cell 2 6 3 1,alignx left,aligny top");
        getContentPane().add(convertButton, "cell 8 6,alignx left,aligny top");
        getContentPane().add(encodingProgressLabel, "cell 0 8 5 1,alignx right,aligny top");
        getContentPane().add(encodingProgressLabel, "cell 6 8,alignx left,aligny top");
        getContentPane().add(encodingMessageLabel, "cell 0 9 5 1,alignx right,aligny top");
        getContentPane().add(encodingMessageLabel, "cell 6 9,alignx left,aligny top");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void convertAction() {
        boolean shouldContinue = true;

        if (outputFile.equals(inputFiles[0])) {
            JOptionPane.showMessageDialog(null, SAME_INPUT_AND_OUTPUT_FILES_MESSAGE, FILE_PARSING_ERROR, 1);
            shouldContinue = false;
        }

        if (shouldContinue && (outputFile.exists())) {
            int session = JOptionPane.showConfirmDialog(null, OUTPUT_FILE_ALREADY_EXISTS_MESSAGE,
                    OVERWRITE_CONFIRMATION, 1);

            if (session != 0) {
                shouldContinue = false;
            }
        }

        if (shouldContinue) {
            if (mp3RadioButton.isSelected()) {
                if (inputFiles.length > 1) {
                    AudioConverter.batchConvertToMP3(inputFiles, outputFile, new ConvertProgressListener());
                } else {
                    AudioConverter.convertToMP3(inputFiles[0], outputFile, new ConvertProgressListener());
                }
            } else if (wavRadioButton.isSelected()) {
                if (inputFiles.length > 1) {
                    AudioConverter.batchConvertToWAV(inputFiles, outputFile, new ConvertProgressListener());
                } else {
                    AudioConverter.convertToWAV(inputFiles[0], outputFile, new ConvertProgressListener());
                }
            }

            convertButton.setEnabled(false);
        }
    }

    private void renameOutputForFormat() {
        if (inputFiles.length > 1) {
            if (!outputFile.isDirectory()) {
                String absoluteInputPath = inputFiles[0].getAbsolutePath();
                int slash = absoluteInputPath.lastIndexOf(File.separator);
                String output = absoluteInputPath.substring(0, slash + 1) + CONVERTED;
                outputFile = new File(output);
                outputFileLabel.setText(outputFile.getAbsolutePath());
            }
        } else {
            String absoluteOutputFilePath = outputFile.getAbsolutePath();
            int period = absoluteOutputFilePath.lastIndexOf(PERIOD);

            if (period > 0) {
                absoluteOutputFilePath = absoluteOutputFilePath.substring(0, period);

                if (mp3RadioButton.isSelected()) {
                    absoluteOutputFilePath = absoluteOutputFilePath + MP3_FILE_ENDING;
                } else if (wavRadioButton.isSelected()) {
                    absoluteOutputFilePath = absoluteOutputFilePath + WAV_FILE_ENDING;
                }
            }

            outputFile = new File(absoluteOutputFilePath);
            outputFileLabel.setText(outputFile.getAbsolutePath());
        }
    }

    private class ConvertProgressListener implements EncoderProgressListener {
        int current = 1;

        private ConvertProgressListener() {
        }

        public void message(String message) {
            if ((inputFiles.length > 1) && (current < inputFiles.length)) {
                encodingMessageLabel.setText(String.valueOf(current) + FILE_PATH_SEPARATOR + inputFiles.length);
            }
        }

        public void progress(int scaledConversionProgress) {
            float progress = scaledConversionProgress / CONVERSION_PROGRESS_SCALE;
            encodingProgressLabel.setText(String.valueOf(progress) + PERCENTAGE_SIGN);

            if (scaledConversionProgress >= MAXIMUM_CONVERSION_PROGRESS) {
                if (inputFiles.length > 1) {
                    current += 1;

                    if (current > inputFiles.length) {
                        encodingMessageLabel.setText(ENCODING_COMPLETE_MESSAGE);
                        convertButton.setEnabled(true);
                    }
                } else if (scaledConversionProgress == ERROR_CONVERSION_PROGRESS) {
                    encodingMessageLabel.setText(ENCODING_FAILED_MESSAGE);
                    convertButton.setEnabled(true);
                } else {
                    encodingMessageLabel.setText(ENCODING_COMPLETE_MESSAGE);
                    convertButton.setEnabled(true);
                }
            }
        }

        public void sourceInfo(MultimediaInfo multimediaInfo) {
        }
    }
}
