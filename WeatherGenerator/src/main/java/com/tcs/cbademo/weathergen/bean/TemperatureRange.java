package com.tcs.cbademo.weathergen.bean;

import com.tcs.cbademo.weathergen.consts.Months;

public class TemperatureRange {
		
	float minTemp;
	
	float maxTemp;
	
	Months month;

	public Months getMonth() {
		return month;
	}

	public void setMonth(Months month) {
		this.month = month;
	}

	public float getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(float minTemp) {
		this.minTemp = minTemp;
	}

	public float getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(float maxTemp) {
		this.maxTemp = maxTemp;
	}
	
}
