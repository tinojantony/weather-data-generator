package com.tcs.cbademo.weathergen.consts;

public class Constants {

	/**
	 * Default start date of the Clock, if no command line based start date is available.
	 */
	public static final String DEFAULT_START_DATE = "2015-01-01";

	/**
	 * Config file to read the list of weather stations.
	 */
	public static final String STATIONS_CONFIG_FILE = "stations.json";
	
	/**
	 * Unit test config file to read the list of weather stations.
	 */
	public static final String UNIT_TEST_STATIONS_CONFIG_FILE = "test_stations.json";
	
	/**
	 * Twelve months in a calendar year.
	 */
	public static final int MONTHS_COUNT = 12;
	
	/**
	 * Temperature adjustment factor based on the clouds presence.
	 */
	public static final int TEMPERATURE_ADJUSTMENT_CONSTANT = 5;

}
