package com.tcs.cbademo.weathergen.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import com.google.gson.stream.JsonReader;
import com.tcs.cbademo.weathergen.bean.CloudProbabilities;
import com.tcs.cbademo.weathergen.bean.CloudProbablityRange;
import com.tcs.cbademo.weathergen.bean.TemperatureHistory;
import com.tcs.cbademo.weathergen.bean.TemperatureRange;
import com.tcs.cbademo.weathergen.consts.Months;
import com.tcs.cbademo.weathergen.consts.WeatherCharacter;

/**
 * Provides set of utility methods for loading weather history
 * @author tinoj
 *
 */
public class WeatherHistoryLoaderUtil {

	final static Gson gson = new Gson();

	private static final String WEATHER_CONFIG_BASE = "config/weather/";
	
	private static final String CLOUD_PROBABLITY_CONFIG_BASE = "config/cloudProbability/";
	
	private static final String TEMPERATURE_CONFIG_FILE = "/temperature.json";
	
	private static final String HUMIDITY_CONFIG_FILE = "/humidity.json";
	
	private final static Logger logger = Logger.getLogger(WeatherHistoryLoaderUtil.class);

	
	public static String getWeatherConfigFilePath(WeatherCharacter weatherCharacter,String stationCode) {
		String filePath = null;
		switch (weatherCharacter) {
		case TEMPERATURE: 
			filePath = WEATHER_CONFIG_BASE + stationCode + TEMPERATURE_CONFIG_FILE ; break;
		case HUMIDITY: 
			filePath = WEATHER_CONFIG_BASE + stationCode + HUMIDITY_CONFIG_FILE; break;
		}    	
		return filePath;
	}
	
	public static String getCloudProbablityConfigFilePath(String stationCode) {
		return CLOUD_PROBABLITY_CONFIG_BASE + stationCode + ".json";
		
	}

	/**
	 * Get temperature history (avg max and avg min) for every month for the station 
	 * @param configFilePath temperature config file path
	 * @return HashMap of Months -> TemperatureRange
	 */
	public static HashMap <Months,TemperatureRange> loadTemperatureHistForStation(String configFilePath) {

		JsonReader reader = null;
		HashMap <Months,TemperatureRange> tempratureHistoryByMonthHash = null; 
		
		try {
			reader = new JsonReader(new FileReader(configFilePath));
			
			// Read from temperature.json file.
			TemperatureHistory temperatureHistoryForYear = gson.fromJson(reader, TemperatureHistory.class);
			
			tempratureHistoryByMonthHash = new HashMap <Months,TemperatureRange>();
			
			// Iterate through temperature history every month 
			for (TemperatureRange tempratureRangeForMonth : temperatureHistoryForYear.getMonthlyTemperatureRangeList()) {
				
				// Skip any month if month name is wrongly configured.
				if (tempratureRangeForMonth.getMonthName() != null ) {
					tempratureHistoryByMonthHash.put(tempratureRangeForMonth.getMonthName(), tempratureRangeForMonth);
				}
			}
			
		} catch (FileNotFoundException e) {
			logger.error("Failed loading config file:"+configFilePath);
		} catch (Exception all){
			// Catching all exception to skip configuration read for the station
			logger.error("Failed loading config file:"+configFilePath+" Reason:"+all.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				logger.error("IOException in loadTemperatureHistForStation():"+e.getMessage());
			}
		}
		return tempratureHistoryByMonthHash;
	}

	/**
	 * Load monsoon cloud probability (lowerLimit and upperLimit) for every month for the station 
	 * @param configFilePath - File path to read the config file
	 * @return HashMap which maps from Months -> CloudProbablityRange
	 */
	public static HashMap <Months,CloudProbablityRange> loadCloudProbabilityForStation(String configFilePath) {

		JsonReader reader = null;
		HashMap <Months,CloudProbablityRange> cloudProbablityByMonthHash = null;  
		
		try {
			reader = new JsonReader(new FileReader(configFilePath));
			
			// Read from cloud probabilities json file.
			CloudProbabilities cloudProbablitiesForYear = gson.fromJson(reader, CloudProbabilities.class);
			
			cloudProbablityByMonthHash = new HashMap <Months,CloudProbablityRange>();
			
			for (CloudProbablityRange cloudProbltyForMonth: cloudProbablitiesForYear.getMonthlyCloudProbabilitiesList()) {
				
				// Skip any month if month name is wrongly configured.
				if (cloudProbltyForMonth.getMonthName() != null ) {
					cloudProbablityByMonthHash.put(cloudProbltyForMonth.getMonthName(), cloudProbltyForMonth);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Failed loading config file:"+configFilePath);
		} catch (Exception all){
			// Catching all exception to skip configuration read for the station
			logger.error("Failed loading config file:"+configFilePath+" Reason:"+all.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				logger.error("IOException:"+e.getMessage());
			}
		}
		return cloudProbablityByMonthHash;
	}
}
