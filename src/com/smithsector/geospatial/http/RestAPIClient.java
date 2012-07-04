package com.smithsector.geospatial.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public abstract class RestAPIClient {

	  private static AsyncHttpClient _client = new AsyncHttpClient();
	  
	  public static void get(String urlToCall, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      _client.get(urlToCall, params, responseHandler);
	  }

	  public static void post(String urlToCall, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      _client.post(urlToCall, params, responseHandler);
	  }
	  
}
