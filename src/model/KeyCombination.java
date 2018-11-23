package model;

import org.jnativehook.keyboard.NativeKeyEvent;

public class KeyCombination {
	
	private int[] keys;
	private int[] modifiers;

	public KeyCombination(int[] keys, int[] modifiers) {
		this.keys = keys;
		this.modifiers = modifiers;
	}
	
	public String asReadable() {
		String out = "";
		for (int i : modifiers) {
			out += NativeKeyEvent.getModifiersText(i) + " + ";
		}
		return null;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	
	public boolean equals(KeyCombination compare) {
		// TODO compare
		return false;
	}

}
