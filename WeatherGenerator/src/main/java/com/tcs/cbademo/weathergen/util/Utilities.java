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
import com.tcs.cbademo.weathergen.consts.Constants;
import com.tcs.cbademo.weathergen.consts.WeatherCondition;
import com.tcs.cbademo.weathergen.exception.WeatherGeneratorException;

/**
 * Provides a set of general utility methods.
 * @author tinoj
 *
 */
public class Utilities {
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final SimpleDateFormat dateFormatTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private static final String CONFIG_BASE = "config/";
	
	private final static Gson gson = new Gson();
	
	private final static Logger logger = Logger.getLogger(Utilities.class);
	
	static {
		// To enable strict date format parsing
		dateFormat.setLenient(false);
	}

	/**
	 * Gets Weather Stations from config file.
	 * @param String - Stations config file to be loaded.
	 * @return List of all station in the config file.
	 */
	public static List <Station> getStationsFromConfigFile(String stationsConfigFile) {
		JsonReader reader = null;
		Stations stations = null;
		try {

			reader = new JsonReader(new FileReader(CONFIG_BASE + stationsConfigFile));

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
	 * Date is expected in in yyyy-MM-dd format
	 * @param args Command line arguments
	 * @return Start Date
	 */
	public static Calendar getStartDateFromString(String startDate) throws WeatherGeneratorException {
		try {
			Calendar calendarStart = Calendar.getInstance();
			calendarStart.setTime(dateFormat.parse(startDate));
			logger.info("Start date read from command line:"+ dateFormat.format(calendarStart.getTime()));
			return calendarStart;
		} catch (ParseException e) {
			logger.warn("Error in parsing user supplied start date. Setting to default start date:"+ Constants.DEFAULT_START_DATE);
			return getDefaultStartDate();
		}

	}
	
	/**
	 * Gets the default start date.
	 * @return Calendar - Default start date.
	 */
	public static Calendar getDefaultStartDate() throws WeatherGeneratorException {
		// Initializing with default start date
		Calendar calendarStart = Calendar.getInstance();
		try {
			calendarStart = Calendar.getInstance();
			calendarStart.setTime(dateFormat.parse(Constants.DEFAULT_START_DATE));
		} catch (ParseException e) {
			logger.error("Error in parsing default start date: Default start date:"+ Constants.DEFAULT_START_DATE);
			throw new WeatherGeneratorException("Error in parsing default start date",e);
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
	
	public static String getFormattedOutputString(Station station,String timeStamp,WeatherCondition condition,float temperature, int pressure) {
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

