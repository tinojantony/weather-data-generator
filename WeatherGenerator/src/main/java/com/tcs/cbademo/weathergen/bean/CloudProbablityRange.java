package com.tcs.cbademo.weathergen.bean;

import com.tcs.cbademo.weathergen.consts.Months;

public class CloudProbablityRange {

	float lowerLimit;
	
	float upperLimit;
	
	Months month;

	public float getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(float lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public float getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(float upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public Months getMonth() {
		return month;
	}

	public void setMonth(Months month) {
		this.month = month;
	}
}

