package com.tcs.cbademo.weathergen.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Appender to write output to a file
 * @author tinoj
 *
 */
public class FileAppender {

	PrintWriter writer;
	
	public FileAppender() {
		
		try {
			writer = new PrintWriter("weather_data.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * Add the line to the file
	 * @param line
	 */
	public void addLine(String line) {
		if (writer != null)
			writer.println(line);
	}
	
	public void closeFileAppender() {
		if (writer != null)
			writer.close();
	}
	
}
