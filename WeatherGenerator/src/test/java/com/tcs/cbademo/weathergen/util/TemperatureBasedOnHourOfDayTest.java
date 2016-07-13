package com.tcs.cbademo.weathergen.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.tcs.cbademo.weathergen.WeatherHistory;
import com.tcs.cbademo.weathergen.bean.Station;
import com.tcs.cbademo.weathergen.consts.Constants;
import com.tcs.cbademo.weathergen.consts.Months;

@RunWith(Parameterized.class)
public class TemperatureBasedOnHourOfDayTest {

	private int hourOfTheDay;
	
	static List <Station> stations;
	
	public TemperatureBasedOnHourOfDayTest(int hourOfTheDay)
	{
		this.hourOfTheDay = hourOfTheDay;

	}

	@Parameters
	public static Collection<Object[]> primeNumbers() {
		return Arrays.asList(new Object[][] {
			{ 1}, { 2},{ 3},{ 4},
			{ 5}, { 6},{ 7},{ 8},
			{ 12}, { 13},{ 14},{ 15},
			{ 21}, { 22},{ 23},{ 24}         
		});
	}

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
	public void testTemperatureWithInRangeBothPositiveAndNegative()
	{	float temperature = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(stations.get(0).getCode(), Months.JANUARY, this.hourOfTheDay);
		assertTrue(temperature >= -10 && temperature <= 30);
	}

	@Test
	public void testTemperatureWithInRangeBothNegative()
	{	float temperature = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(stations.get(0).getCode(), Months.FEBRUARY, this.hourOfTheDay);
		assertTrue(temperature >= -18 && temperature <= -2);
	}
	
	@Test
	public void testTemperatureWithInRangeBothPositive()
	{	float temperature = WeatherCalculationUtils.getTemperatureBasedOnHourOfDay(stations.get(0).getCode(), Months.MARCH, this.hourOfTheDay);
		assertTrue(temperature >= 2 && temperature <= 26);
	}
	
}

