package gui;

import java.util.Observable;

import javafx.scene.control.TableView;

public class EntryTable extends Observable {
	
	private TableView table;

	public EntryTable(TableView table) {
		this.table = table;
	}

}
