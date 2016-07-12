package com.tcs.cbademo.weathergen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tcs.cbademo.weathergen.bean.CloudProbablityRange;
import com.tcs.cbademo.weathergen.bean.DailyTemperatureSlopeCoefficients;
import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.bean.TemperatureRange;
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
 
	private static final int MONTHS_COUNT = 12;
	
	/**
	 * Mapping from STATION -> Montly Temperature Range
	 */
	private static HashMap <String,HashMap<Months,TemperatureRange>> temperatureHistStationMap = new HashMap <String,HashMap<Months,TemperatureRange>>();
	
	/**
	 * Mapping from STATION -> Slope co-efficients for each month,
	 */
	private static HashMap <String,HashMap<Months,DailyTemperatureSlopeCoefficients>> temperatureSlopeCoeffientsStationMap = 
																			new HashMap <String,HashMap<Months,DailyTemperatureSlopeCoefficients>>();
	
	/**
	 * Mapping from STATION -> Monthly monsoon cloud probablity range
	 */
	private static HashMap <String,HashMap<Months,CloudProbablityRange>> cloudProbablityStationMap = new HashMap <String,HashMap<Months,CloudProbablityRange>>();
	
	/**
	 * List of valid station where histoty weather data and cloud probablities are loaded.
	 */
	private static List <Station> stationsWithValidWeatherHist = new ArrayList <Station>();
	
	/**
	 * Loads historic weather data.
	 * @param stations
	 */
	public static void loadAllWeatherHistory(final List<Station> stations) { 
		
		for(Station station : stations) {
			boolean temperatureLoaded = loadTemperatureHistoryForStation(station.getCode());
			boolean cloudProbablityLoaded = loadCloudProbablityForStation(station.getCode());
			
			if (temperatureLoaded && cloudProbablityLoaded) {
				stationsWithValidWeatherHist.add(station);
			}
		}
	}
	
	/**
	 *  Returns the list of stations with properly formatted weather data.
	 * @return list of valid stations
	 */
	static List <Station> getStationsWithValidWeatherHistory() {
		return stationsWithValidWeatherHist;
	}
	
	/**
	 * Loads Temperature history for the station from config file
	 * @param stationCode
	 * @return true if successfully read, else false.
	 */
	private static boolean loadTemperatureHistoryForStation(String stationCode) {
		String configFilePath = WeatherHistoryLoaderUtil.getWeatherConfigFilePath(WeatherCharacter.TEMPERATURE, stationCode);
		HashMap <Months,TemperatureRange> temperatureHistoryEachMonth = WeatherHistoryLoaderUtil.loadTemperatureHistForStation(configFilePath);
		if (temperatureHistoryEachMonth != null && temperatureHistoryEachMonth.size() == MONTHS_COUNT) {
			temperatureHistStationMap.put(stationCode, temperatureHistoryEachMonth);
			
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
		if (cloudProbablityEachMonth != null && cloudProbablityEachMonth.size() == MONTHS_COUNT) {
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
	public static DailyTemperatureSlopeCoefficients temperatureSlopeCoeffientsByStationAndMonth(String station, Months month) {
		return temperatureSlopeCoeffientsStationMap.get(station).get(month);
	}
}
