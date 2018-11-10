package model;

import java.util.ArrayList;
import java.util.List;

public class Soundboard {
	
	private List<Entry> entries;

	public Soundboard() {
		entries = new ArrayList<Entry>();
	}
	
	public void addEntry(Entry entry) {
		entries.add(entry);
	}
	
	public void removeEntry(Entry entry) {
		entries.remove(entry);
	}

}
