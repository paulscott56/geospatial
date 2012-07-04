package com.smithsector.geospatial.http;

public interface IRestAPIDelegate {

	void receiveSuccess(RestResponse response);

	void receiveFailure(RestFailure message);

}
