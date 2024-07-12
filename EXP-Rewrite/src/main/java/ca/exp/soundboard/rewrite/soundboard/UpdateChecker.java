package ca.exp.soundboard.rewrite.soundboard;

import ca.exp.soundboard.rewrite.gui.UpdateConfirmFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateChecker implements Runnable {

    private static final String UPDATE_URL_STRING = "http://sourceforge.net/projects/expsoundboard/files/";

    public static String getUpdateNotes() {
        boolean hasInternetConnection = false;
        BufferedReader reader = null;

        try {
            URI uri = new URI(UPDATE_URL_STRING);
            URL url = uri.toURL();
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            hasInternetConnection = true;
        } catch (IOException | URISyntaxException exception) {
            Logger.getLogger(UpdateChecker.class.getName()).log(Level.SEVERE, null, exception);
        }

        if (hasInternetConnection) {
            boolean newVersionFound = false;

            String patchlist = "";
            boolean changeLogFound = false;

            try {
                String line = reader.readLine();

                while (line != null) {
                    if (!changeLogFound) {
                        if (line.startsWith("CHANGELOG")) {
                            changeLogFound = true;
                        }
                    } else if (changeLogFound) {
                        if (line.startsWith("vers.") && !newVersionFound) {
                            newVersionFound = true;
                            patchlist = patchlist + '\n' + line;
                        } else {
                            if (newVersionFound && line.startsWith("vers.")) {
                                reader.close();
                                return patchlist;
                            }

                            patchlist = patchlist + '\n' + line;
                        }
                    }
                }

                reader.close();
            } catch (IOException exception) {
                Logger.getLogger(UpdateChecker.class.getName()).log(Level.SEVERE, null, exception);
            }
        }

        return "Update notes could not be found";
    }

    public static boolean isUpdateAvailable() {
        boolean internetconnection = false;
        BufferedReader reader = null;

        try {
            URI uri = new URI(UPDATE_URL_STRING);
            URL url = uri.toURL();
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            internetconnection = true;
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(UpdateChecker.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (internetconnection) {
            boolean changelogFound = false;
            try {
                String line = reader.readLine();
                while (line != null) {
                    if (!changelogFound) {
                        if (line.startsWith("CHANGELOG")) {
                            changelogFound = true;
                        }
                    } else if (changelogFound && line.startsWith("vers.")) {
                        String version = line.substring(line.indexOf('.') + 1, line.lastIndexOf(':')).trim();
                        float versionNo = Float.parseFloat(version);
                        if (versionNo > 0.5F) {
                            reader.close();
                            return true;
                        }
                        reader.close();
                        return false;
                    }
                }

                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(UpdateChecker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    public void run() {
        if (isUpdateAvailable()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new UpdateConfirmFrame(UpdateChecker.getUpdateNotes());
                }
            });
        }
    }
}
