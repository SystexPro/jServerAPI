package org.christianhorton.japi;


import java.util.*;
import java.io.*;
import java.text.*;

// Utils class
public class Utils {
	// Method to assemble array slice given array, start and length
	public static String assembleSlice(String[] input, int start, int length) {
		int s = start;
		StringBuffer buf = new StringBuffer();

		// Loop length items of array from start
		for (int i = 0; i < length; i++) {
			buf.append(input[s + i] + " "); // Append to buffer
		}

		// Return String form of buffer
		return buf.toString();
	}

	// Overloaded method to assemble array slice given array and start
	public static String assembleSlice(String[] input, int start) {
		// Call self with full length calculation
		return assembleSlice(input, start, input.length - start);
	}

	// Method to translate a config file into a hash map
	public static HashMap<String, String> parseConfig(String file) {
		// Create a hash map
		HashMap<String, String> options = new HashMap<String, String>();

		// Process File
		try {
			// Readers
			File inFile = new File(file);
			FileInputStream fis = new FileInputStream(inFile);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader in = new BufferedReader(new InputStreamReader(dis));

			// String
			String line;

			// Loop Lines
			while ((line = in.readLine()) != null) {
				// Trim the line
				line = line.trim();

				// Check it's not a blank line or a comment
				if (!line.matches("^$") && !line.matches("^//.*")) {
					// Check it's in the valid format e.g. name:value
					if (!line.matches("^.+:.*$")) {
						// Throw exception if not
						throw new ParseException(line, 0);
					}
					else {
						// Get position of divider
						int div = line.indexOf(":");

						// Split the 2 into a new hashmap entry
						options.put(line.substring(0, div), line.substring(div + 1));
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		// Return the hash map
		return options;
	}
}