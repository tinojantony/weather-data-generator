package com.tcs.cbademo.weathergen.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.tcs.cbademo.weathergen.WeatherGenerator;

/**
 * Appender to write output to a file
 * @author tinoj
 *
 */
public class FileAppender {

	private PrintWriter writer;
	
	private final static Logger logger = Logger.getLogger(FileAppender.class);
	
	public FileAppender() {
		
		try {
			writer = new PrintWriter("weather_data.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException creating output file weather_data.txt:", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException creating output file weather_data.txt:", e);
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
