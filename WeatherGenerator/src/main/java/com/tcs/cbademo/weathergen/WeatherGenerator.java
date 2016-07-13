package com.tcs.cbademo.weathergen;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.consts.Constants;
import com.tcs.cbademo.weathergen.consts.Months;
import com.tcs.cbademo.weathergen.consts.WeatherCondition;
import com.tcs.cbademo.weathergen.util.FileAppender;
import com.tcs.cbademo.weathergen.util.Utilities;
import com.tcs.cbademo.weathergen.util.WeatherCalculationUtils;


/**
 * Entry point with main() for the Weather Generator - generates a mock of weather data.
 *
 */
public class WeatherGenerator 
{
	private final static Logger logger = Logger.getLogger(WeatherGenerator.class);
	
	// File Appender to write results to file.
	private FileAppender fileAppender ;
	
	// For record count - Used for Verification & Performance checks.
	private int recordCounter = 0;
	
	public static void main( String[] args ) {
		
		Calendar calendarStart = null;
		
		// Get start date of clock from command line arguments.
		// First command line argument is the start date in yyyy-MM-dd format
		if (args != null && args.length > 0) {
			calendarStart = Utilities.getStartDateFromString(args[0]);
		} else {
			// If no command line argument, initialize with default start date.
			calendarStart = Utilities.getDefaultStartDate();
		}
		
		if (calendarStart != null) {

			// End date of the clock. Current implementation calculates end date as 1 YEAR from Start date
			Calendar calendarEnd  = Utilities.getCalendarEnd(calendarStart);

			// Instantiate WeatherGenerator. 
			// Loads all historic data and configurations.
			WeatherGenerator weatherGenerator = new WeatherGenerator();

			// Start the clock.
			weatherGenerator.startClock(calendarStart,calendarEnd);
		}
	}

	/**
	 * Loads the station list, historic temperature data, humidity data, cloud probability from config files.
	 */
	private WeatherGenerator() {
		logger.info("Started loading historic data...");
		
		// Get Weather Stations from config file. 
		List <Station> stations = Utilities.getStationsFromConfigFile(Constants.STATIONS_CONFIG_FILE);

		// Loads historic weather data.
		WeatherHistory.loadAllWeatherHistory(stations);
		
		logger.info("Finished loading historic data.");
	}

	/**
	 * Starts the clock. Clock increments every 1 HOUR.
	 * @param calendar - Clock start calendar instance
	 * @param calendarEnd - Clock end calendar instance.
	 */
	private void startClock(Calendar calendar, Calendar calendarEnd) {
		
		// Initialize the file appender for writing weather data generated.
		try {
			fileAppender = new FileAppender();
		} catch (IOException e) {
			logger.error("Exiting program due to output file creation failure...", e);
			System.exit(1);
		}
		
		logger.info("Starting clock to generate weather data for "+ WeatherHistory.getStationsWithValidWeatherHistory().size()+ " stations..");
		do {
			// Get the weather data for all the station in this hour
			getWeatherDataThisHourAllStations(calendar);
			
			// Increment the clock by 1 HOUR
			calendar.add(Calendar.HOUR, 1);
			
		} while (calendar.before(calendarEnd));
		
		logger.info("Clock stopped. Generated "+recordCounter+" records.");		
		logger.info("Output written to [weather_data.txt] in the project base folder.");
		
		// Close the output file as the clock is stopped.
		fileAppender.closeFileAppender();
	}
	
	
	/**
	 * Get the weather data for all the valid stations for this hour.
	 * @param calendar - Time this hour
	 */
	private void getWeatherDataThisHourAllStations(Calendar calendar) {

		// Get hour of the day in 0-23 format
		int hour =  calendar.get(Calendar.HOUR_OF_DAY);

		// Get calendar month in 0-11 format.
		int monthInInt = calendar.get(Calendar.MONTH);
		// Convert month in Integer format to Months enum.		
		Months month = Months.getMonthforValue(monthInInt);

		// Get timestamp (this hour) in display format
		String timeStamp = Utilities.getTimeStamp(calendar);

		// Iterates through all valid stations and get weather data this hour for all stations.
		for (Station station: WeatherHistory.getStationsWithValidWeatherHistory()) {
			getWeatherDataThisHourByStation(station, month,hour,timeStamp);
			recordCounter++;
		}
	}
	
	/**
	 * Get the weather data this hour for the station.
	 * @param station 
	 * @param month
	 * @param hourOfDay
	 * @param timeStamp
	 */
	private void getWeatherDataThisHourByStation(final Station station, final Months month, final int hourOfDay,final String timeStamp) {
		
		// Get probability of monsoon clouds this hour
		// Probability is generated as a random number, within the cloud probability range configured for this month for the station.
		float probablityOfClouds = WeatherCalculationUtils.getProbablityOfCloudsInStationThisHour(station.getCode(), month);
		
		// Get the temperature based on the hour of the day - 
		// Based on time of the day, temperature is calculated within diurnal temperature range.
		float tempBasedOnHourOfDay = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(station.getCode(), month, hourOfDay);
		
		// Get temperature adjustment based on the presence of clouds
		float tempChangeDuetoClouds = WeatherCalculationUtils.getTemperatureVariationDueToCloud(probablityOfClouds, hourOfDay);
		
		// Get the actual temperature after adjustment
		float actualTemperature = tempBasedOnHourOfDay + tempChangeDuetoClouds;
		
		// Calculates weather conditions like SNOW,SUNNY,RAIN,CLOUDY 
		WeatherCondition weatherCondition = WeatherCalculationUtils.getWeatherCondition(probablityOfClouds, actualTemperature);
		
		// Get atmospheric pressure based on the altitude based lookup and cloud presence.
		int atmPressure = WeatherCalculationUtils.getAtmosPressureForStation(station, probablityOfClouds,actualTemperature);
		
		// Get the formatted string with all weather data for file logging.
		String weatherThisHourStr = Utilities.getFormattedOutputString(station, timeStamp, weatherCondition, actualTemperature, atmPressure);
		
		// Append the formatted string to output file.
		fileAppender.addLine(weatherThisHourStr);
		
		logger.debug("CloudProbablity:"+probablityOfClouds+" tempBasedOnHourOfDay:"+ tempBasedOnHourOfDay+ 
				" tempChangeDueToCloud"+ tempChangeDuetoClouds + " "+ weatherThisHourStr);
	}

}
