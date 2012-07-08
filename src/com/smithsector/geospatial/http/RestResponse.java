package com.smithsector.geospatial.http;

import java.util.List;

import com.smithsector.geospatial.entities.Place;

import android.util.Log;

public class RestResponse {
	
	public RestResponse(List<?> data, boolean success) {
		
		_data = data;
		_success = success;
	}

	List<?> _data;
	public List<?> getData() {
		return _data;
	}
	
	public void debug() {
		
		for (Object place : this._data) {
			
			Log.d("CONSOLE", "name:" + ((Place)place).name[0]);
		}
	}
	
	boolean _success;
	public boolean getSuccess() {
		return _success;
	}
}
