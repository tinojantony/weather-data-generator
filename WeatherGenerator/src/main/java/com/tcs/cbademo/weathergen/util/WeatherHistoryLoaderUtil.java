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
	
	private final static Logger logger = Logger.getLogger(WeatherHistoryLoaderUtil.class);

	
	public static String getWeatherConfigFilePath(WeatherCharacter weatherCharacter,String stationCode) {
		String filePath = null;
		switch (weatherCharacter) {
		case TEMPERATURE: 
			filePath = WEATHER_CONFIG_BASE + stationCode + "/temperature.json"; break;
		case HUMIDITY: 
			filePath = WEATHER_CONFIG_BASE + stationCode + "/humidity.json"; break;
		}    	
		return filePath;
	}
	
	public static String getCloudProbablityConfigFilePath(String stationCode) {
		return CLOUD_PROBABLITY_CONFIG_BASE + stationCode + ".json";
		
	}

	public static HashMap <Months,TemperatureRange> loadTemperatureHistForStation(String configFilePath) {

		JsonReader reader = null;
		HashMap <Months,TemperatureRange> tempratureHistoryByMonthHash = new HashMap <Months,TemperatureRange>();
		
		try {
			reader = new JsonReader(new FileReader(configFilePath));
			TemperatureHistory temperatureHistory = gson.fromJson(reader, TemperatureHistory.class);
			for (TemperatureRange monthRange: temperatureHistory.getTemperatures()) {
				tempratureHistoryByMonthHash.put(monthRange.getMonth(), monthRange);
			}
			
		} catch (FileNotFoundException e) {
			logger.error("Failed loading config file:"+configFilePath);
		} catch (Exception all){
			logger.error("Failed loading config file:"+configFilePath+" Reason:"+all.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				logger.error("IOException:"+e.getMessage());
			}
		}
		return tempratureHistoryByMonthHash;
	}

	public static HashMap <Months,CloudProbablityRange> loadCloudProbabilityForStation(String configFilePath) {

		JsonReader reader = null;
		HashMap <Months,CloudProbablityRange> cloudProbablityByMonthHash = new HashMap <Months,CloudProbablityRange>();
		
		try {
			reader = new JsonReader(new FileReader(configFilePath));
			CloudProbabilities cloudProbablities = gson.fromJson(reader, CloudProbabilities.class);
			for (CloudProbablityRange monthRange: cloudProbablities.getCloudProbabilities()) {
				cloudProbablityByMonthHash.put(monthRange.getMonth(), monthRange);
			}
		} catch (FileNotFoundException e) {
			logger.error("Failed loading config file:"+configFilePath);
		} catch (Exception all){
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
