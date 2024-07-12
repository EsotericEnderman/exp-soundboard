package ca.exp.soundboard.rewrite.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UpdateConfirmFrame extends JFrame {

    private static final long serialVersionUID = -6700862565543741036L;

    private static final String TITLE = "Update Available!";
    private static final int DEFAULT_CLOSE_OPERATION = 2;

    private static final String UPDATE_AVAILABLE_LABEL = "EXP SoundboardStage Update Available";
    private static final String UPDATE_URI = "https://sourceforge.net/projects/expsoundboard/";
    private static final String CLOSE_BUTTON = "Close";
    private static final String GET_UPDATE_BUTTON = "Get Update";
    private static final String CHECK_FOR_UPDATES_ON_LAUNCH_CHECKBOX = "Check for Updates on launch";

    private JTextPane textPane;

    public UpdateConfirmFrame(String updateNotes) {
        setResizable(false);
        setDefaultCloseOperation(DEFAULT_CLOSE_OPERATION);
        setTitle(TITLE);

        JLabel lblSoundboardUpdateAvailable = new JLabel(UPDATE_AVAILABLE_LABEL);

        JScrollPane scrollPane = new JScrollPane();

        JButton closeButton = new JButton(CLOSE_BUTTON);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        JButton getUpdateButton = new JButton(GET_UPDATE_BUTTON);
        getUpdateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(UPDATE_URI));
                } catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }

                dispose();
            }
        });

        final JCheckBox checkForUpdatesCheckbox = new JCheckBox(CHECK_FOR_UPDATES_ON_LAUNCH_CHECKBOX);
        checkForUpdatesCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SoundboardFrame.updateCheck = !SoundboardFrame.updateCheck;
                checkForUpdatesCheckbox.setSelected(SoundboardFrame.updateCheck);
            }
        });

        checkForUpdatesCheckbox.setSelected(SoundboardFrame.updateCheck);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout
                                .createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(scrollPane, -1, 480, 32767)
                                .addComponent(lblSoundboardUpdateAvailable)
                                .addGroup(groupLayout.createSequentialGroup().addComponent(checkForUpdatesCheckbox)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 161, 32767)
                                        .addComponent(getUpdateButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(closeButton)))
                                .addContainerGap()));

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        groupLayout.createSequentialGroup().addContainerGap()
                                .addComponent(lblSoundboardUpdateAvailable)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, -2, 124, -2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(closeButton)
                                        .addComponent(checkForUpdatesCheckbox).addComponent(getUpdateButton))
                                .addContainerGap(78, 32767)));

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setText(updateNotes);
        scrollPane.setViewportView(textPane);

        getContentPane().setLayout(groupLayout);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
