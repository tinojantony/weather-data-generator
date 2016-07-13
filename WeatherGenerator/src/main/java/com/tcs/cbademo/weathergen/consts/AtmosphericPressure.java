package com.tcs.cbademo.weathergen.consts;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.tcs.cbademo.weathergen.WeatherGenerator;

/**
 * Provides a mapping between Altitude (feet/100) to Atmospheric pressure
 * @author tinoj
 *
 */
public class AtmosphericPressure {
	
	private static HashMap<Integer,Integer> pressureFromAltitude = new HashMap<Integer,Integer>();
	
	private final static Logger logger = Logger.getLogger(WeatherGenerator.class);
	
	static {
		pressureFromAltitude.put(0, 1013);
		pressureFromAltitude.put(5,995);
		pressureFromAltitude.put(10, 977);
		pressureFromAltitude.put(15,960);
		pressureFromAltitude.put(20, 942);
		pressureFromAltitude.put(25,925);
		pressureFromAltitude.put(30, 908);
		pressureFromAltitude.put(35,891);
		pressureFromAltitude.put(40, 875);
		pressureFromAltitude.put(45,859);
		pressureFromAltitude.put(50, 843);
		pressureFromAltitude.put(60,812);
		pressureFromAltitude.put(70, 782);
		pressureFromAltitude.put(80,753);
		pressureFromAltitude.put(90, 724);
		pressureFromAltitude.put(100,697);
		pressureFromAltitude.put(150, 572);
		pressureFromAltitude.put(200,466);
		pressureFromAltitude.put(250, 376);
		pressureFromAltitude.put(300,301);
		pressureFromAltitude.put(350, 238);
		pressureFromAltitude.put(400,188);
		pressureFromAltitude.put(450,147);
		pressureFromAltitude.put(500, 116);
		pressureFromAltitude.put(550,91);
		pressureFromAltitude.put(600, 72);
		pressureFromAltitude.put(650,56);
	}
	
	
	/**
	 * Gets atmospheric pressure based on the altitude
	 * @param altitudeOfStation - altitude of station in hectofeet (1 hectofeet=100 feets)
	 * @return Atmospheric pressure in hPA
	 */
	public static int getAtmosPressureFromAltitude(int altitudeOfStation) {
		
		// Altitude scale adjustment for the ALTITUDE-PRESSURE Lookup.
		// From 0-50 hecto feet -> altitude to be converted to multiples of 5
		// From 50-100 hecto feet -> altitude to be converted to multiples of 10
		// From 100-650 hecto feet -> altitude to be converted to multiples of 50
		
		int scaledAltitude = 0;
		if (altitudeOfStation <= 50) {
			// Converting to multiples of 5. 
			scaledAltitude = (int) 5 *(Math.round(altitudeOfStation / 5));
		} else if (altitudeOfStation > 50 && altitudeOfStation <= 100) {
			// Converting to multiples of 10
			scaledAltitude = (int) 10 * Math.round(altitudeOfStation / 10);
		} else {
			// Converting to multiples of 50
			scaledAltitude = (int) 50 *(Math.round(altitudeOfStation / 50));
		}
		
		if (pressureFromAltitude.containsKey(scaledAltitude)) {
			return pressureFromAltitude.get(scaledAltitude);
		} else {
			logger.error("Failed to fetch pressure for altitude:" + altitudeOfStation);
			return -1;
		}		
	}

}
