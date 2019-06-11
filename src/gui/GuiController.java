package gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A parent class to every class that handles interaction between code and gui.
 */
public abstract class GuiController {

    protected SoundboardStage parent;
    protected Logger logger;

    protected Stage stage;
    protected Scene scene;

    /**
     * Allows the controller to access GUI objects before initialize() is called by the JavaFX thread.
     * Typically used for setting up GUI functions that require arguments fed from outside the class.
     * @param parent The 'main' class that the application is running on.
     * @param stage The window this gui will run in.
     * @param scene The scene all the GUI's elements are loaded into.
     */
    void preload(SoundboardStage parent, Stage stage, Scene scene) {
        this.parent = parent;
        this.stage = stage;
        this.scene = scene;

        // Setup logger
        logger = Logger.getLogger(this.getClass().getName());

        // Setup logger handler to pipe important logs to error dialogs
        Handler guiOutput = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel() == Level.WARNING || record.getLevel() == Level.SEVERE)
                    parent.throwError(record.getMessage());
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };
        guiOutput.setLevel(Level.WARNING);
        logger.addHandler(guiOutput);
    }

    public abstract void start();

    public abstract void stop();

    public void forceStop() {
        logger.log(Level.INFO, "Force stopping " + this.getClass().getName());
        stage.close();
    }
}
