package ca.exp.soundboard.rewrite.gui;

import ca.exp.soundboard.rewrite.soundboard.AudioManager;
import ca.exp.soundboard.rewrite.soundboard.Utils;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AudioLevelsFrame extends JFrame {

    private static final String TITLE = "Audio Gain Controls";

    private static final String PRIMARY_OUTPUT_GAIN_LABEL = "Primary Output Gain:";
    private static final String SECONDARY_OUTPUT_GAIN_LABEL = "Secondary Output Gain:";
    private static final String MIC_INJECTOR_GAIN_LABEL = "Mic Injector Gain:";

    private static final long serialVersionUID = 464347549019590824L;

    private static AudioLevelsFrame instance = null;
    private JSlider primarySlider;
    private JSlider secondarySlider;
    private JSlider micInjectorSlider;

    private AudioLevelsFrame() {
        setTitle(TITLE);
        setResizable(false);
        setDefaultCloseOperation(2);
        setIconImage(SoundboardFrame.icon);

        JLabel primaryOutputGainLabel = new JLabel(PRIMARY_OUTPUT_GAIN_LABEL);

        int primaryGain = (int) AudioManager.getFirstOutputGain();
        int secondaryGain = (int) AudioManager.getSecondOutputGain();
        int micInjectorGain = (int) Utils.getMicInjectorGain();

        primarySlider = new JSlider();
        primarySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                if (!primarySlider.getValueIsAdjusting()) {
                    float gain = primarySlider.getValue();
                    AudioManager.setFirstOutputGain(gain);
                }
            }
        });

        primarySlider.setMajorTickSpacing(6);
        primarySlider.setPaintLabels(true);
        primarySlider.setPaintTicks(true);
        primarySlider.setSnapToTicks(true);
        primarySlider.setMinorTickSpacing(1);
        primarySlider.setValue(0);
        primarySlider.setMinimum(-66);
        primarySlider.setMaximum(6);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);

        JLabel lblSecondaryOutputGain = new JLabel(SECONDARY_OUTPUT_GAIN_LABEL);

        secondarySlider = new JSlider();
        secondarySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                if (!secondarySlider.getValueIsAdjusting()) {
                    float gain = secondarySlider.getValue();
                    AudioManager.setSecondOutputGain(gain);
                }
            }
        });

        secondarySlider.setValue(0);
        secondarySlider.setSnapToTicks(true);
        secondarySlider.setPaintTicks(true);
        secondarySlider.setPaintLabels(true);
        secondarySlider.setMinorTickSpacing(1);
        secondarySlider.setMinimum(-66);
        secondarySlider.setMaximum(6);
        secondarySlider.setMajorTickSpacing(6);

        JSeparator jSeparator = new JSeparator();
        jSeparator.setForeground(Color.BLACK);

        JLabel micInjectorGainLabel = new JLabel(MIC_INJECTOR_GAIN_LABEL);

        micInjectorSlider = new JSlider();
        micInjectorSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                if (!micInjectorSlider.getValueIsAdjusting()) {
                    float gain = micInjectorSlider.getValue();
                    Utils.setMicInjectorGain(gain);
                }
            }
        });

        micInjectorSlider.setValue(0);
        micInjectorSlider.setSnapToTicks(true);
        micInjectorSlider.setPaintTicks(true);
        micInjectorSlider.setPaintLabels(true);
        micInjectorSlider.setMinorTickSpacing(1);
        micInjectorSlider.setMinimum(-66);
        micInjectorSlider.setMaximum(6);
        micInjectorSlider.setMajorTickSpacing(6);

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(separator, -1, 424, 32767)
                                                        .addComponent(primarySlider, -1, 424, 32767)
                                                        .addComponent(primaryOutputGainLabel)
                                                        .addComponent(lblSecondaryOutputGain)
                                                        .addComponent(secondarySlider, -2, 424, -2)))
                                        .addGroup(groupLayout.createSequentialGroup().addGap(11)
                                                .addComponent(jSeparator, -1, 423, 32767))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addComponent(micInjectorGainLabel))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addComponent(micInjectorSlider, -2, 424, -2)))
                                .addContainerGap()));

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                groupLayout
                        .createSequentialGroup().addContainerGap().addComponent(primaryOutputGainLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(primarySlider, -2, -1, -2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(separator, -2, 2, -2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(lblSecondaryOutputGain)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secondarySlider, -2, 45, -2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jSeparator, -2, 2, -2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(micInjectorGainLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(micInjectorSlider, -2, 45, -2).addContainerGap(38, 32767)));

        getContentPane().setLayout(groupLayout);

        primarySlider.setValue(primaryGain);
        secondarySlider.setValue(secondaryGain);
        micInjectorSlider.setValue(micInjectorGain);

        pack();
        setVisible(true);
    }

    public static AudioLevelsFrame getInstance() {
        if (instance == null) {
            instance = new AudioLevelsFrame();
        } else {
            instance.setVisible(true);
            instance.requestFocus();
        }

        return instance;
    }

    public void dispose() {
        super.dispose();
        instance = null;
    }
}
