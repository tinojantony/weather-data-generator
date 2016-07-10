package com.tcs.cbademo.weathergen.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tcs.cbademo.weathergen.consts.Months;

public class TemperatureHistory {

	List <TemperatureRange> temperatures = new ArrayList <TemperatureRange>();

	public List<TemperatureRange> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(List<TemperatureRange> temperatures) {
		this.temperatures = temperatures;
	}

}
