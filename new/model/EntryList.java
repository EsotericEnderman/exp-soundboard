package model;

import java.util.ArrayList;
import java.util.List;

public class EntryList {

	private List<Entry> entries;

	public EntryList() {
		entries = new ArrayList<Entry>();
	}

	public void addEntry(Entry entry) {
		entries.add(entry);
	}

	public void removeEntry(Entry entry) {
		entries.remove(entry);
	}

	public void removeEntry(int index) {
		entries.remove(index);
	}

	public Entry getEntry(int index) {
		return entries.get(index);
	}

}
