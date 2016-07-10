package com.tcs.cbademo.weathergen.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;

import com.tcs.cbademo.weathergen.WeatherGenerator;

public class UtilitiesTest {

	
	@Test
	public void testGetStartDateFromCommandLineWithArguments()  {
		Calendar calendar1 = Utilities.getStartDateFromCommandLine(new String[] {"2014-02-02"});
		Calendar calendar2 = Calendar.getInstance();
		
		try {
			calendar2.setTime(Utilities.dateFormat.parse("2014-02-02"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(calendar1.getTimeInMillis() == calendar2.getTimeInMillis() );
		
	}
	
	@Test
	public void testGetStartDateFromCommandLineWithOutArguments()  {
		Calendar calendar1 = Utilities.getStartDateFromCommandLine(new String[] {});
		Calendar calendar2 = Calendar.getInstance();
		
		try {
			calendar2.setTime(Utilities.dateFormat.parse(WeatherGenerator.DEFAULT_START_DATE));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(calendar1.getTimeInMillis() == calendar2.getTimeInMillis() );
		
	}
	
	@Test
	public void testGenerateRandomNumbersWithInBoundary() {
		double randomNumber = Utilities.generateRandomNumbersWithInBoundary(0.5f, 0.6f);
		assertTrue(randomNumber > 0.5 && randomNumber <0.6);
		
	}

	@Test
	public void testIsNightTime1() {
		boolean isNightTime = Utilities.isNightTime(5);
		assertTrue(isNightTime);
		
	}
	
	@Test
	public void testIsNightTime2() {
		boolean isNightTime = Utilities.isNightTime(7);
		assertFalse(isNightTime);
		
	}
	
	@Test
	public void testIsNightTime3() {
		boolean isNightTime = Utilities.isNightTime(19);
		assertFalse(isNightTime);
		
	}
	
	@Test
	public void testIsNightTime4() {
		boolean isNightTime = Utilities.isNightTime(21);
		assertTrue(isNightTime);
		
	}

	@Test
	public void testGetScaledHourOfDay() {
		int scaledHour = Utilities.getScaledHourOfDay(3);
		assertSame(27, scaledHour);
		
		scaledHour = Utilities.getScaledHourOfDay(23);
		assertSame(23, scaledHour);
		
		scaledHour = Utilities.getScaledHourOfDay(5);
		assertSame(5, scaledHour);
	}

	@Test
	public void testDecimalRounding() {
		//fail("Not yet implemented");
	}

}