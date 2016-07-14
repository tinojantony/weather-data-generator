package com.tcs.cbademo.weathergen.exception;

/**
 * Custom exception class thrown when Weather data generation failed.
 * @author tinoj
 *
 */
public class WeatherGeneratorException extends Exception  {
	public WeatherGeneratorException(String message, Exception e) {
		super(message,e);
	}
}
