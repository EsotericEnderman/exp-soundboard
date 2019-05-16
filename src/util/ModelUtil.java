package util;

import gui.EntryModel;
import model.Entry;

import java.util.Collection;
import java.util.Collections;

public class ModelUtil {

    public static EntryModel[] toModel(Entry[] entries) {
        EntryModel[] arr = new EntryModel[entries.length];

        for (int i = 0; i < entries.length; i++) {
            arr[i] = new EntryModel(entries[i]);
        }

        return arr;
    }

}
