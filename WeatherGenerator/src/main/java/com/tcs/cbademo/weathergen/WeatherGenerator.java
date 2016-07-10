package com.tcs.cbademo.weathergen;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.consts.Months;
import com.tcs.cbademo.weathergen.consts.WeatherCondition;
import com.tcs.cbademo.weathergen.util.FileAppender;
import com.tcs.cbademo.weathergen.util.Utilities;
import com.tcs.cbademo.weathergen.util.WeatherCalculationUtils;


/**
 * Generates a mock of weather data.
 *
 */
public class WeatherGenerator 
{
	// Holds the previous weather history (monthly averages of all stations)
	WeatherHistory weatherHistory = new WeatherHistory();

	// Stations with valid weather data after history loading.
	private List <Station> validWeatherStations;

	// Default start date of the Clock, if no command line based start date is available.
	public static final String DEFAULT_START_DATE = "2015-01-01";
	
	private final static Logger logger = Logger.getLogger(WeatherGenerator.class);
	
	// File Appender to write results to file.
	FileAppender fileAppender = new FileAppender();
	
	int recordCounterForPerfomanceCheck = 0;

	public static void main( String[] args ) {
		
		// Get start date of clock from command line arguments.
		// First command line argument is the start date in yyyy-MM-dd format
		Calendar calendarStart = Utilities.getStartDateFromCommandLine(args);
		
		// End date of the clock. Current implementation calculates end date as 1 YEAR from Start date
		Calendar calendarEnd  = Utilities.getCalendarEnd(calendarStart);
		
		// Instantiate WeatherGenerator. 
		// Loads all historic data and configurations.
		WeatherGenerator weatherGenerator = new WeatherGenerator();
		
		// Start the clock.
		weatherGenerator.startClock(calendarStart,calendarEnd);
	}

	/**
	 * Loads the station list, historic temperature data, humidity data, cloud probability from config files.
	 */
	private WeatherGenerator() {
		logger.info("Started loading historic data...");
		
		// Get Weather Stations from config file. 
		List <Station> stations = Utilities.getStationsFromConfigFile(false);

		// Loads historic weather data.
		weatherHistory.loadAllWeatherHistory(stations);
		
		// Gets the list of stations with properly formatted weather data.
		validWeatherStations = weatherHistory.getStationsWithValidWeatherHistory();
		
		logger.info("Finished loading historic data.");
	}

	/**
	 * Starts the clock. Clock increments every 1 HOUR.
	 * @param calendar - Clock start calendar instance
	 * @param calendarEnd - Clock end calendar instance.
	 */
	void startClock(Calendar calendar, final Calendar calendarEnd) {
		logger.info("Starting clock to generate weather data for "+validWeatherStations.size()+ " stations..");
		do {
			// Get the weather data for all the station in this hour
			getWeatherDataThisHourAllStations(calendar);
			
			// Increment the clock by 1 HOUR
			calendar.add(Calendar.HOUR, 1);
			
		} while (calendar.before(calendarEnd));
		
		logger.info("Clock stopped");		
		logger.info("Generated "+recordCounterForPerfomanceCheck+" records.");
		logger.info("Output written to [weather_data.txt] in the project base folder.");
		
		// Close the output file as the clock is stopped.
		fileAppender.closeFileAppender();
	}
	
	/**
	 * Get the weather data for all the valid stations for this hour.
	 * @param calendar - Time this hour
	 */
	void getWeatherDataThisHourAllStations(final Calendar calendar) {
		
		int monthInInt = calendar.get(Calendar.MONTH);
		int hour =  calendar.get(Calendar.HOUR_OF_DAY);
		Months month = Months.getMonthforValue(monthInInt);
		String timeStamp = Utilities.getTimeStamp(calendar);
		
		// Iterates through all valid stations and get weather data this hour for all stations.
		for (Station station: validWeatherStations) {
			getWeatherDataThisHourByStation(station, month,hour,timeStamp);
			recordCounterForPerfomanceCheck++;
		}
	}
	
	/**
	 * Get the weather data this hour for the station.
	 * @param station 
	 * @param month
	 * @param hourOfDay
	 * @param timeStamp
	 */
	void getWeatherDataThisHourByStation(final Station station, final Months month, final int hourOfDay,String timeStamp) {
		
		// Get probability of monsoon clouds this hour
		// Probability is generated as a random number, within the cloud probability range configured for this month for the station.
		float probablityOfClouds = WeatherCalculationUtils.getProbablityOfCloudsInStationThisHour(station.getCode(), month);
		
		// Get the temperature based on the hour of the day - 
		// Based on time of the day, temperature is calculated within diurnal temperature range.
		float tempBasedOnHourOfDay = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(station.getCode(), month, hourOfDay);
		
		// Get temperature adjustment based on the presence of clouds
		float tempChangeDuetoClouds = WeatherCalculationUtils.getTemperatureVariationDueToCloud(probablityOfClouds, hourOfDay);
		
		// Get the actual temperature
		float actualTemperature = tempBasedOnHourOfDay + tempChangeDuetoClouds;
		
		// Calculates weather conditions like SNOW,SUNNY,RAIN,CLOUDY 
		WeatherCondition weatherCondition = WeatherCalculationUtils.getWeatherCondition(probablityOfClouds, actualTemperature);
		
		// Get atmospheric pressure based on the altitude based lookup and cloud presence.
		int atmPressure = WeatherCalculationUtils.getAtmPressureForStation(station, probablityOfClouds,actualTemperature);
		
		// Get the formatted string with all weather data for file logging.
		String weatherThisHourStr = Utilities.getFormattedResult(station, timeStamp, weatherCondition, actualTemperature, atmPressure);
		
		// Append the formatted string to output file.
		fileAppender.addLine(weatherThisHourStr);
		
		logger.debug("CloudProbablity:"+probablityOfClouds+" tempBasedOnHourOfDay:"+ tempBasedOnHourOfDay+ 
				" tempChangeDueToCloud"+ tempChangeDuetoClouds + " "+ weatherThisHourStr);
	}

}
