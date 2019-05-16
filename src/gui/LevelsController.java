package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;

public class LevelsController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="primarySlider"
    private Slider primarySlider; // Value injected by FXMLLoader

    @FXML // fx:id="secondarySlider"
    private Slider secondarySlider; // Value injected by FXMLLoader

    @FXML // fx:id="injectorSlider"
    private Slider injectorSlider; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert primarySlider != null : "fx:id=\"primarySlider\" was not injected: check your FXML file 'levels_jfx.fxml'.";
        assert secondarySlider != null : "fx:id=\"secondarySlider\" was not injected: check your FXML file 'levels_jfx.fxml'.";
        assert injectorSlider != null : "fx:id=\"injectorSlider\" was not injected: check your FXML file 'levels_jfx.fxml'.";
    }
}
