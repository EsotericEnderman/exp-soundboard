package ca.exp.soundboard.remake.util;

import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

	public static final FileChooser.ExtensionFilter standard_audio = new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac");
	public static final FileChooser.ExtensionFilter all_files = new FileChooser.ExtensionFilter("All Files", "*.*");
	
	/**
	 * Reads the entirety of a given file into a list.
	 * 
	 * @param fileName The name of the file to be read from.
	 * 
	 * @return The desired file in a list, each element being a line in the file.
	 * 
	 * @throws IOException           If an I/O error occurs.
	 * @throws FileNotFoundException if the named file does not exist,is a directory
	 *                               rather than a regular file,or for some other
	 *                               reason cannot be opened for reading.
	 */
	public static List<String> readFile(String fileName) throws IOException, FileNotFoundException {
		List<String> fileLoad = new ArrayList<String>();
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			fileLoad.add(line);
		}
		bufferedReader.close();

		return fileLoad;
	}

	/**
	 * Writes the given data into a file of choice.
	 * 
	 * @param load     The data, given in a list of lines to be written.
	 * @param fileName The name of the file to be written into.
	 * 
	 * @throws FileNotFoundException If the given string does not denote an
	 *                               existing, writable regular file and a new
	 *                               regular file of that name cannot be created, or
	 *                               if some other error occurs while opening or
	 *                               creating the file.
	 */
	public static void writeFile(List<String> load, String fileName) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(fileName);

		for (int i = 0; i < load.size(); i++) {
			writer.println(load.get(i));
		}
		writer.close();
	}

}
