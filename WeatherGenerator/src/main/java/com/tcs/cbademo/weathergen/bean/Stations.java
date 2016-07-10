package com.tcs.cbademo.weathergen.bean;

import java.util.ArrayList;
import java.util.List;

public class Stations {

	List <Station> stations = new ArrayList<Station>();

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
}
