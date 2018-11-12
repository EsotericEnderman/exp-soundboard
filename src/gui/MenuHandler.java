package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class MenuHandler implements EventHandler<ActionEvent> {
	
	MenuItem[] items;

	public MenuHandler(MenuItem... items) {
		for (MenuItem item : items) {
			item.setOnAction(this);
		}
		this.items = items;
	}

	@Override
	public void handle(ActionEvent event) {
		MenuItem source = (MenuItem) event.getSource();
		
		switch(source.getText()) {
		case "New":
			break;
		case "Open":
			break;
		case "Save":
			break;
		}
	}

}
