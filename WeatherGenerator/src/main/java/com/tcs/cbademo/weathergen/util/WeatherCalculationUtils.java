package com.tcs.cbademo.weathergen.util;

import java.util.HashMap;

import com.tcs.cbademo.weathergen.WeatherHistory;
import com.tcs.cbademo.weathergen.bean.CloudProbablityRange;
import com.tcs.cbademo.weathergen.bean.DailyTemperatureSlopeCoefficients;
import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.bean.TemperatureRange;
import com.tcs.cbademo.weathergen.consts.AtmosphericPressure;
import com.tcs.cbademo.weathergen.consts.Constants;
import com.tcs.cbademo.weathergen.consts.Months;
import com.tcs.cbademo.weathergen.consts.WeatherCondition;

/**
 * Provides set of utility methods for Weather Calculation
 * @author tinoj
 *
 */
public class WeatherCalculationUtils {
	
	
	/**
	 * Get probability of monsoon clouds this hour
	 * Probability is generated as a random number, within the cloud probability range configured for this month for the station.
	 * @param station
	 * @param month
	 * @return Random probability of monsoon clouds in this station,this month
	 */
	public static float getProbablityOfCloudsInStationThisHour(String station, Months month) {
		CloudProbablityRange probablityRange = WeatherHistory.getCloudProbablityByStationAndMonth(station, month);
		float probablityOfCloudsInTheStation = (float)Utilities.generateRandomNumbersWithInBoundary(probablityRange.getLowerLimit(),
													probablityRange.getUpperLimit());
		return Utilities.decimalRounding(probablityOfCloudsInTheStation);
	}
	
	/**
	 * Get weather condition based on cloud presence and temperature.
	 * @param probablityOfCloudsInTheStation
	 * @param currentTemperature
	 * @return enum WeatherCondition with values SNOW, SUNNY, CLOUDY, RAIN
	 */
	public static WeatherCondition getWeatherCondition(float probablityOfCloudsInTheStation, float currentTemperature) {
		
		boolean noClouds = probablityOfCloudsInTheStation <= Constants.NOT_ENOUGH_CLOUDS_UPPER_THRESHOLD;
		boolean notEnoughCloudsToStartRain = (probablityOfCloudsInTheStation > Constants.NOT_ENOUGH_CLOUDS_UPPER_THRESHOLD && 
				probablityOfCloudsInTheStation <= Constants.RAIN_START_LOWER_THRESHOLD);
		boolean rainStarted = probablityOfCloudsInTheStation > Constants.RAIN_START_LOWER_THRESHOLD;
		
		WeatherCondition weatherCondition = WeatherCondition.UNKNOWN;
		if (currentTemperature < 0) {
			// Temperature below 0 degrees.
			weatherCondition = WeatherCondition.SNOW;
		} else if (noClouds) {
			// Not enough clouds. So will be a sunny day
			weatherCondition = WeatherCondition.SUNNY;
		} else if (notEnoughCloudsToStartRain) {
			// Clouds are there. But not enough to start rain.
			weatherCondition = WeatherCondition.CLOUDY;
		} else if (rainStarted) {
			// Lots of cloud and it will be raining.
			weatherCondition = WeatherCondition.RAIN;
		}
		return weatherCondition;
	}
	
	/**
	 * Refer README.md for the hourly temperature calculation.
	 * y = mx +c; where m = slope, c = intercept ;
	 * 'm' and 'c' are same for full month. Which is pre-calculated after loading temperature history.
	 * 'x' is the 'hourOfDay'
	 * 'y' is the 'temperature of the hour'
	 * @param station
	 * @param month
	 * @param hourOfDay
	 * @return
	 */
	public static float getTemperatureBasedOnHourOfDay(String station, Months month, int hourOfDay) {
		
		int scaledHourOfDay = Utilities.getScaledHourOfDay(hourOfDay);
		
		// Get slope co-efficiens m1,m2,c1,c2 (pre-calculated for the month and station) for using in formula y=mx+c
		DailyTemperatureSlopeCoefficients slopeCoefficients = WeatherHistory.getTemperatureSlopeCoeffientsByStationAndMonth(station,month);
		
		// 1pm (noon) is '13' in 24 hour clock.
		if (scaledHourOfDay < 13) {
			// m1 and c1 for 4am to 1pm
			float temperatureOfTheHour = (slopeCoefficients.getSlope1()* scaledHourOfDay) + slopeCoefficients.getIntercept1();
			return Utilities.decimalRounding(temperatureOfTheHour);
		} else {
			// m2 and c2 for 1pm to 4am
			float temperatureOfTheHour = (slopeCoefficients.getSlope2()* scaledHourOfDay) + slopeCoefficients.getIntercept2();
			return Utilities.decimalRounding(temperatureOfTheHour);
		}
	}
	
	/**
	 * Get the temperature adjustment factor due to the presence of the clouds.
	 * Refer README.md for the effects of the clouds on temperature.
	 * @param probablityOfCloudsInTheStation
	 * @param hourOfDay
	 * @return Int - temperature adjustment
	 */
	public static int getTemperatureVariationDueToCloud(float probablityOfCloudsInTheStation,int hourOfDay) {
		int adjustedTemperature = 0;
		
		boolean noClouds = probablityOfCloudsInTheStation <= Constants.NOT_ENOUGH_CLOUDS_UPPER_THRESHOLD;
		boolean notEnoughCloudsToStartRain = (probablityOfCloudsInTheStation > Constants.NOT_ENOUGH_CLOUDS_UPPER_THRESHOLD && 
				probablityOfCloudsInTheStation <= Constants.RAIN_START_LOWER_THRESHOLD);
		boolean rainStarted = probablityOfCloudsInTheStation > Constants.RAIN_START_LOWER_THRESHOLD;
		
		if (noClouds) {
			// Not enough clouds. So no temperature adjustment.
			// Later will add minor adjustment based on amount of clouds.
		} else if (notEnoughCloudsToStartRain) {
			if (Utilities.isNightTime(hourOfDay)) {
				// In night, clouds blocks the escape of heat from the earth surface. Will make surface warm.
				adjustedTemperature = Constants.TEMPERATURE_ADJUSTMENT_CONSTANT;
			} else {
				//In daytime, clouds acts as shade from infrared radiation. So will reduce temperature.
				adjustedTemperature = -1 * Constants.TEMPERATURE_ADJUSTMENT_CONSTANT ;
			}
		} else if (rainStarted) {
			// Started raining. So temperature will be reduced.
			adjustedTemperature = -1 * Constants.TEMPERATURE_ADJUSTMENT_CONSTANT ;
		}	
		return adjustedTemperature;
	}
	
	/**
	 * Temperature is increasing from 4 am to 13 pm (noon). 
     * Temperature Difference /  X-axis(time): 4am to 13pm  = 9 hours.
	 * Refer README.md
	 * @param minTemp
	 * @param maxTemp
	 * @return float - Slope m1
	 */
	static float getTemperatureSlopeFrom4amTo13pm(float minTemp, float maxTemp) {
		
		return (maxTemp - minTemp)/9;
	}
	
	/**
	 * Temperature is decreasing from 13pm (noon) to 4 am (early morning). 
	 * Temperature Difference /  X-axis(time): 13pm to 4am  = 15 hours.
	 * Refer README.md
	 * @param minTemp
	 * @param maxTemp
	 * @return float - Slope m2
	 */
	static float getTemperatureSlopeFrom13pmTo4am(float minTemp, float maxTemp) {
		
		return (minTemp - maxTemp)/15;
	}
	
	/**
	 * Calculaes intercept for the slopes, given slope and a point(maxTemp)
	 *  y = mx + c;    c = y - mx;
	 *  At max temperature (y-axis), time of the day is 13 pm (noon time)
	 *  So y => maxTemp, x => 13pm
	 *  Refer README.md
	 * @param slope
	 * @param maxTemp
	 * @return float - Intercept
	 */
	static float calculateIntercept(float slope, float maxTemp) {
		return maxTemp - slope * 13;
	}
	
	/**
	 * Calculates slopes and intercepts for each month of the station
	 * slopes and intercepts are calculates based on the Avg Temperature Range configured(each month) for the station  
	 * @param temperatureHistoryEachMonth
	 * @return HashMap - mapping between Months -> Slope & Intercept co-efficients 
	 */
	public static HashMap<Months,DailyTemperatureSlopeCoefficients> getTemperatureSlopeCoefficientsForStation(
														HashMap <Months,TemperatureRange> temperatureHistoryEachMonth) {
		
		HashMap<Months,DailyTemperatureSlopeCoefficients> slopeCoefficientsForStation = new HashMap<Months,DailyTemperatureSlopeCoefficients>();
		
		for (Months month: Months.values()) {
			DailyTemperatureSlopeCoefficients slopeCoefficientsForMonth = new DailyTemperatureSlopeCoefficients(); 
			TemperatureRange temperatureRangeForThisMonth = temperatureHistoryEachMonth.get(month);
			slopeCoefficientsForMonth.setSlope1(getTemperatureSlopeFrom4amTo13pm(temperatureRangeForThisMonth.getMinTemp(),
																							temperatureRangeForThisMonth.getMaxTemp()));
			slopeCoefficientsForMonth.setSlope2(getTemperatureSlopeFrom13pmTo4am(temperatureRangeForThisMonth.getMinTemp(),
																							temperatureRangeForThisMonth.getMaxTemp()));
			slopeCoefficientsForMonth.setIntercept1(calculateIntercept(slopeCoefficientsForMonth.getSlope1(), 
																							temperatureRangeForThisMonth.getMaxTemp()));
			slopeCoefficientsForMonth.setIntercept2(calculateIntercept(slopeCoefficientsForMonth.getSlope2(), 
																							temperatureRangeForThisMonth.getMaxTemp()));
			slopeCoefficientsForStation.put(month, slopeCoefficientsForMonth);
			
		}
		return slopeCoefficientsForStation;
	}
	
	/**
	 * Gets atmospheric pressure based on the altitude, probablity of monsoon clouds and temperature
	 * @param station
	 * @param probablityOfClouds
	 * @param temperature
	 * @return Atmospheric pressure in hPA
	 */
	public static int getAtmosPressureForStation(Station station,float probablityOfClouds, float temperature) {
		int altitudeOfStation = station.getAltitude();
		
		int pressure = AtmosphericPressure.getAtmosPressureFromAltitude(altitudeOfStation);
		
		/**
		TO DO: Incorporate correction factor for the pressure based on:
		1) Temperature   2) Probability of Clouds 
		*/
		
		return pressure;
	}
	
}
