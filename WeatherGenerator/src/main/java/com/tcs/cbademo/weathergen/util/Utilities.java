package com.tcs.cbademo.weathergen.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.List;


import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.tcs.cbademo.weathergen.WeatherGenerator;
import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.bean.Stations;
import com.tcs.cbademo.weathergen.consts.WeatherCondition;

/**
 * Provides a set of general utility methods.
 * @author tinoj
 *
 */
public class Utilities {
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static SimpleDateFormat dateFormatTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private static final String CONFIG_BASE = "config/";
	
	final static Gson gson = new Gson();
	
	private final static Logger logger = Logger.getLogger(Utilities.class);

	/**
	 * Gets Weather Stations from config file.
	 * @param isTestMode - True if in (unit)testing, False in normal cases
	 * @return List of all station in the stations.json file.
	 */
	public static List <Station> getStationsFromConfigFile(boolean isTestMode) {
		JsonReader reader = null;
		Stations stations = null;
		try {
			if (!isTestMode)
				reader = new JsonReader(new FileReader(CONFIG_BASE + "stations.json"));
			else 
				reader = new JsonReader(new FileReader(CONFIG_BASE + "test_stations.json"));
		    stations = gson.fromJson(reader, Stations.class);
		} catch (FileNotFoundException e) {
			logger.error("Please change directory (cd) to the .jar folder before running");
			logger.error("Or configure stations.json file inside the config folder.");
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				logger.error("Error occured when closing stations.json file");
			}
		}
		return stations.getStations();
	}
	
	/**
	 * Current implementation calculates end date as 1 YEAR from Start date
	 * @param calendar
	 * @return End date of the clock.
	 */
	public static Calendar getCalendarEnd(Calendar calendar) {
		Calendar calendarEnd = (Calendar) calendar.clone();
		calendarEnd.add(Calendar.YEAR, 1);
		return calendarEnd;
	}
	
	/**
	 * Generates a random number within the boundaries
	 * @param lowerBound
	 * @param upperBound
	 * @return double - Random number
	 */
	public static double generateRandomNumbersWithInBoundary(float lowerBound, float upperBound) {
		return lowerBound + (Math.random() * (upperBound - lowerBound));
	}
	
	public static boolean isNightTime(int hourOfDay) {
		if (hourOfDay < 6 || hourOfDay > 20) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adjust hourOf the day to proper format
	 * @param hourOfDay
	 * @return
	 */
	public static int getScaledHourOfDay(int hourOfDay) {
		if (hourOfDay < 4) {
			return 24 + hourOfDay;
		} else {
			return hourOfDay;
		}
	}
	
	/**
	 * Round off a floating point to 2 decimal points.
	 * @param input
	 * @return float with max 2 decimal points.
	 */
	public static float decimalRounding(float input) {
		return (float) (Math.round(input*100.0)/100.0);
		
	}
	
	/**
	 * Get Clock start date from command line arguments
	 * @param args Command line arguments
	 * @return Start Date
	 */
	public static Calendar getStartDateFromCommandLine(String[] args) {
		Calendar calendarStart = Calendar.getInstance();
		if (args != null && args.length > 0) {
			try {
				calendarStart.setTime(dateFormat.parse(args[0]));
			} catch (ParseException e) {
				// Parse error occured with user supplied start date.
				try {
					calendarStart.setTime(dateFormat.parse(WeatherGenerator.DEFAULT_START_DATE));
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			// Initializing with default start date
			try {
				calendarStart.setTime(dateFormat.parse(WeatherGenerator.DEFAULT_START_DATE));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return calendarStart;
	}
	
	/**
	 * Gets timestamp in yyyy-MM-dd'T'HH:mm:ss'Z format
	 * @param calendar
	 * @return
	 */
	public static String getTimeStamp(Calendar calendar) {
		return dateFormatTimeStamp.format(calendar.getTime());
	}
	
	public static String getFormattedResult(Station station,String timeStamp,WeatherCondition condition,float temperature, int pressure) {
		String pipe_seperator = "|";
		String coma_seperator = ",";
		StringBuffer sb = new StringBuffer("");
	    sb.append(station.getCode()).append(pipe_seperator);
	    sb.append(station.getLattitude()).append(coma_seperator);
	    sb.append(station.getLongitude()).append(coma_seperator);
	    sb.append(station.getAltitude()).append(pipe_seperator);
	    sb.append(timeStamp).append(pipe_seperator);
	    sb.append(condition).append(pipe_seperator);
	    sb.append(temperature).append(pipe_seperator);
	    sb.append(pressure);
	    return sb.toString();
	}
}

