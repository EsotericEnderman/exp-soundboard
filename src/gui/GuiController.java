package gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A parent class to every class that handles interaction between code and gui.
 */
public abstract class GuiController {

    protected SoundboardStage parent;
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
    }

    public abstract void start();

    public abstract void stop();
}
