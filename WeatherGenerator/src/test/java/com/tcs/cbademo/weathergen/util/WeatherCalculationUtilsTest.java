package com.tcs.cbademo.weathergen.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tcs.cbademo.weathergen.WeatherHistory;
import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.consts.AtmosphericPressure;
import com.tcs.cbademo.weathergen.consts.Constants;
import com.tcs.cbademo.weathergen.consts.Months;
import com.tcs.cbademo.weathergen.consts.WeatherCondition;

public class WeatherCalculationUtilsTest {

	static List <Station> stations;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/// Get Weather Stations from config file.
		stations = Utilities.getStationsFromConfigFile(Constants.UNIT_TEST_STATIONS_CONFIG_FILE);

		WeatherHistory.loadAllWeatherHistory(stations);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetProbablityOfCloudsInStationThisHourA() {
		// Generate a random probability within the range configured.
		float probablityOfClouds = WeatherCalculationUtils.getProbablityOfCloudsInStationThisHour(stations.get(0).getCode(), Months.JANUARY);
		
		//Check the bounds
		assertTrue(probablityOfClouds >= 0.3 && probablityOfClouds <= 0.5);
	}
	
	@Test
	public void testGetProbablityOfCloudsInStationThisHourB() {
		// Generate a random probability within the range configured.
		float probablityOfClouds = WeatherCalculationUtils.getProbablityOfCloudsInStationThisHour(stations.get(0).getCode(), Months.JUNE);
		
		//Check the bounds
		assertTrue(probablityOfClouds >= 0.6 && probablityOfClouds <= 1.0);
	}

	@Test
	public void testGetProbablityOfCloudsInStationThisHourC() {
		// Generate a random probability within the range configured.
		float probablityOfClouds = WeatherCalculationUtils.getProbablityOfCloudsInStationThisHour(stations.get(0).getCode(), Months.DECEMBER);
		
		//Check the bounds
		assertTrue(probablityOfClouds >= 0 && probablityOfClouds <= 0.5);
	}

	
	@Test
	public void testGetWeatherConditionSunny() {
		WeatherCondition weatherCondition = WeatherCalculationUtils.getWeatherCondition((float)0.3, 20);
		assertEquals(WeatherCondition.SUNNY, weatherCondition);
	}
	
	@Test
	public void testGetWeatherConditionCloudy() {
		WeatherCondition weatherCondition = WeatherCalculationUtils.getWeatherCondition((float)0.6, 20);
		assertEquals(WeatherCondition.CLOUDY, weatherCondition);
	}
	
	@Test
	public void testGetWeatherConditionRainy() {
		WeatherCondition weatherCondition = WeatherCalculationUtils.getWeatherCondition((float)0.8, 20);
		assertEquals(WeatherCondition.RAIN, weatherCondition);
	}
	
	@Test
	public void testGetWeatherConditionSnow() {
		WeatherCondition weatherCondition = WeatherCalculationUtils.getWeatherCondition((float)0.8, -1);
		assertEquals(WeatherCondition.SNOW, weatherCondition);
	}

	@Test
	public void testGetTemperatureBasedOnHourOfDayMaximum() {
		// Configured Value: { "month": "MARCH", "minTemp": 2.8, "maxTemp": 25 }

		float temperature = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(stations.get(0).getCode(), Months.MARCH, 13);
		assertTrue(Math.abs(temperature - 25) < 1);
	}
	
	@Test
	public void testGetTemperatureBasedOnHourOfDayMinimum() {
		// Configured Value: { "month": "MARCH", "minTemp": 2.8, "maxTemp": 25 }
		float temperature = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(stations.get(0).getCode(), Months.MARCH, 4);
		assertTrue(Math.abs(temperature - 2.8) < 1);
	}

	@Test
	public void testGetTemperatureVariationDueToCloudWithOutClouds() {
		int adjustment = WeatherCalculationUtils.getTemperatureVariationDueToCloud(0.3f, 12);
		assertEquals(0, adjustment);
	}
	
	@Test
	public void testGetTemperatureVariationDueToCloudWithCloudsDuringDay() {
		int adjustment = WeatherCalculationUtils.getTemperatureVariationDueToCloud(0.6f, 12);
		assertEquals(-5, adjustment);
	}
	
	@Test
	public void testGetTemperatureVariationDueToCloudWithCloudsDuringNight() {
		int adjustment = WeatherCalculationUtils.getTemperatureVariationDueToCloud(0.6f, 22);
		assertEquals(+5, adjustment);
	}

	@Test
	public void testGetTemperatureSlopeFrom4amTo13pm() {
		float slope = WeatherCalculationUtils.getTemperatureSlopeFrom4amTo13pm(10, 30);
		assertTrue(Math.abs(slope - 2.22) < 0.1);
	}

	@Test
	public void testGetTemperatureSlopeFrom13pmTo4am() {
		float slope = WeatherCalculationUtils.getTemperatureSlopeFrom13pmTo4am(10, 30);
		assertTrue(Math.abs(slope + 1.33) < 0.1);
	}

	@Test
	public void testCalculateIntercept1() {
		float intercept1 = WeatherCalculationUtils.calculateIntercept(2.22f, 30);
		assertTrue(Math.abs(intercept1 - 1.14) < 0.1);
	}
	
	@Test
	public void testCalculateIntercept2() {
		float intercept1 = WeatherCalculationUtils.calculateIntercept(1.33f, 30);
		assertTrue(Math.abs(intercept1 - 12.7) < 1);
	}

	
	@Test
	public void testGetAtmPressureForStationAltitudeA() {
		int pressure = AtmosphericPressure.getAtmosPressureFromAltitude(0);
		assertEquals(1013, pressure);
	}

	@Test
	public void testGetAtmPressureForStationAltitudeB() {
		int pressure = AtmosphericPressure.getAtmosPressureFromAltitude(7);
		assertEquals(995, pressure);
	}
	
	@Test
	public void testGetAtmPressureForStationAltitudeC() {
		int pressure = AtmosphericPressure.getAtmosPressureFromAltitude(57);
		assertEquals(843, pressure);
	}
	
	@Test
	public void testGetAtmPressureForStationAltitudeD() {
		int pressure = AtmosphericPressure.getAtmosPressureFromAltitude(125);
		assertEquals(697, pressure);
	}

}
