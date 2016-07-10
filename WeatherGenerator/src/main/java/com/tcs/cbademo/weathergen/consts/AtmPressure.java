package com.tcs.cbademo.weathergen.consts;

import java.util.HashMap;

/**
 * Provides a mapping between Altitude (feet/100) to Atmospheric pressure
 * @author tinoj
 *
 */
public class AtmPressure {
	
	private static HashMap<Integer,Integer> pressureFromAltitude = new HashMap<Integer,Integer>();
	
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
	 * Get atmospheric pressure from altitude.
	 * @param altitude - In (feets/100).
	 * @return int - Atmospheric presure.
	 */
	public static int getPressureFromAltitude(int altitude) {
		if (pressureFromAltitude.containsKey(altitude)) {
			return pressureFromAltitude.get(altitude);
		} else {
			return -1;
		}
	}

}
