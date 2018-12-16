package gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class GuiController {

    protected Soundboard parent;
    protected Stage stage;
    protected Scene scene;

    void preload(Soundboard parent, Stage stage, Scene scene) {
        this.parent = parent;
        this.stage = stage;
        this.scene = scene;
    }
}
