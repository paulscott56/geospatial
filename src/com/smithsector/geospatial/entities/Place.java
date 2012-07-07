package com.smithsector.geospatial.entities;

public class Place {
	public String gtopo30;
	public String admin1code;
	public String timezone;
	public String admin4code;
	public String loc;
	public String countrycode;
	public String geonameid;
	public String wikipedia;
	public String cc2; 
	public String admin2code;
	public String featureclass; 
	public String latitude;
	public String type;
	public String elevation;
	public String featurecode;
	public String modificationdate;
	public String admin3code;
	public String population;
	public String name;
	public String longitude;
	public String pics; 
	public String subtype;
	public String asciiname;
	public String _id;
	
	@Override
	public String toString() {
		return this.name;
	}
}
