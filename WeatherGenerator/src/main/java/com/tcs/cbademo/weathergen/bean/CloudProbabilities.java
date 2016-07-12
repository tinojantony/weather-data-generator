package com.tcs.cbademo.weathergen.bean;

import java.util.ArrayList;
import java.util.List;

public class CloudProbabilities {
	List <CloudProbablityRange> cloudProbabilities = new ArrayList <CloudProbablityRange>();

	public List<CloudProbablityRange> getMonthlyCloudProbabilitiesList() {
		return cloudProbabilities;
	}

	public void setMonthlyCloudProbabilitiesList(List<CloudProbablityRange> cloudProbabilities) {
		this.cloudProbabilities = cloudProbabilities;
	}
}
