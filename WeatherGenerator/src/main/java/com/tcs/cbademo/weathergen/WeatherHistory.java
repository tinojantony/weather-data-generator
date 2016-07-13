package com.tcs.cbademo.weathergen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tcs.cbademo.weathergen.bean.CloudProbablityRange;
import com.tcs.cbademo.weathergen.bean.DailyTemperatureSlopeCoefficients;
import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.bean.TemperatureRange;
import com.tcs.cbademo.weathergen.consts.Constants;
import com.tcs.cbademo.weathergen.consts.Months;
import com.tcs.cbademo.weathergen.consts.WeatherCharacter;
import com.tcs.cbademo.weathergen.util.WeatherCalculationUtils;
import com.tcs.cbademo.weathergen.util.WeatherHistoryLoaderUtil;


/**
 * Loads and stores weather history (temperature, probability of monsoon clouds) as a map for each station.
 * These history details are loaded before clock is started.
 * @author tinoj
 *
 */
public class WeatherHistory {
 
	/**
	 * Mapping from STATION -> Monthly Temperature Range
	 */
	private static HashMap <String,HashMap<Months,TemperatureRange>> temperatureHistStationMap = new HashMap <String,HashMap<Months,TemperatureRange>>();
	
	/**
	 * Mapping from STATION -> Slope co-efficients for each month,
	 */
	private static HashMap <String,HashMap<Months,DailyTemperatureSlopeCoefficients>> temperatureSlopeCoeffientsStationMap = 
																	new HashMap <String,HashMap<Months,DailyTemperatureSlopeCoefficients>>();
	
	/**
	 * Mapping from STATION -> Monthly monsoon cloud probability range
	 */
	private static HashMap <String,HashMap<Months,CloudProbablityRange>> cloudProbablityStationMap = new HashMap <String,HashMap<Months,CloudProbablityRange>>();
	
	/**
	 * List of valid station where history weather data and cloud probabilities are loaded.
	 */
	private static List <Station> stationsWithValidWeatherHistory = new ArrayList <Station>();
	
	/**
	 * Loads historic weather data (to be called before clock is started)
	 * @param stations - list of stations configured in station.json file
	 */
	public static void loadAllWeatherHistory(final List<Station> stations) { 
		
		// Iterates through each station and loads its monthly avg temperature and cloud probability data from config file.
		for(Station station : stations) {
			
			// Loads monthly avg min temperature and avg max temperature from config.
			boolean temperatureLoaded = loadTemperatureHistoryForStation(station.getCode());
			
			// Loads monthly monsoon cloud probability from config.
			boolean cloudProbablityLoaded = loadCloudProbablityForStation(station.getCode());
			
			if (temperatureLoaded && cloudProbablityLoaded) {
				// Add to the list of valid stations if static configs are loaded successfully.
				stationsWithValidWeatherHistory.add(station);
			}
		}
	}
	
	/**
	 *  Returns the list of stations with properly formatted weather data.
	 * @return list of valid stations
	 */
	static List <Station> getStationsWithValidWeatherHistory() {
		return stationsWithValidWeatherHistory;
	}
	
	/**
	 * Loads Temperature history for the station from config file
	 * @param stationCode
	 * @return true if successfully read, else false.
	 */
	private static boolean loadTemperatureHistoryForStation(String stationCode) {
		
		// Get path of the temperature config file for the station.
		String configFilePath = WeatherHistoryLoaderUtil.getWeatherConfigFilePath(WeatherCharacter.TEMPERATURE, stationCode);
		
		// Loads the historic temperature data from the config file.
		HashMap <Months,TemperatureRange> temperatureHistoryEachMonth = WeatherHistoryLoaderUtil.loadTemperatureHistForStation(configFilePath);
		
		// Checking 12 months data availability.
		if (temperatureHistoryEachMonth != null && temperatureHistoryEachMonth.size() == Constants.MONTHS_COUNT) {
			
			// Add temperature data of the station to STATION -> Monthly Temperature Range Map.
			temperatureHistStationMap.put(stationCode, temperatureHistoryEachMonth);
			
			// From Monthly Temperature Range for the station, calculate slope co-efficients 
			// slope co-efficients is used for calculating daily temperature variation
			HashMap<Months,DailyTemperatureSlopeCoefficients> slopeCoefficientsForStation = 
					WeatherCalculationUtils.getTemperatureSlopeCoefficientsForStation(temperatureHistoryEachMonth);
			temperatureSlopeCoeffientsStationMap.put(stationCode, slopeCoefficientsForStation);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Reads probablity of monssoon clouds for this station from config file
	 * @param stationCode
	 * @return true if successfully read, else false.
	 */
	private static boolean loadCloudProbablityForStation(String stationCode) {
		String configFilePath = WeatherHistoryLoaderUtil.getCloudProbablityConfigFilePath(stationCode);
		HashMap <Months,CloudProbablityRange> cloudProbablityEachMonth = WeatherHistoryLoaderUtil.loadCloudProbabilityForStation(configFilePath);
		
		// Checking 12 months data availability.
		if (cloudProbablityEachMonth != null && cloudProbablityEachMonth.size() == Constants.MONTHS_COUNT) {
			cloudProbablityStationMap.put(stationCode, cloudProbablityEachMonth);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Gets cloud probability range configured for this month for the station
	 * @param station
	 * @param month
	 * @return Probability range configured.
	 */
	public static CloudProbablityRange getCloudProbablityByStationAndMonth(String station, Months month) {
		return cloudProbablityStationMap.get(station).get(month);
	}
	
	/**
	 * Gets slope co-efficients(m1,m2,c1,c2) for this month for the station
	 * @param station
	 * @param month
	 * @return DailyTemperatureSlopeCoefficients - slope co-efficients(m1,m2,c1,c2)
	 */
	public static DailyTemperatureSlopeCoefficients getTemperatureSlopeCoeffientsByStationAndMonth(String station, Months month) {
		return temperatureSlopeCoeffientsStationMap.get(station).get(month);
	}
}
