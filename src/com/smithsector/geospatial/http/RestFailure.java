package com.smithsector.geospatial.http;

public class RestFailure {
	
	public RestFailure(String message) {
		
		_message = message;
	}
	
	private String _message;
	public String getMessage() {
		return _message;
	}
	
}
