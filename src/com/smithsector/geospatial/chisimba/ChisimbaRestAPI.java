package com.smithsector.geospatial.chisimba;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smithsector.geospatial.R;
import com.smithsector.geospatial.http.IRestAPIDelegate;
import com.smithsector.geospatial.http.RestAPIClient;
import com.smithsector.geospatial.http.RestFailure;
import com.smithsector.geospatial.http.RestResponse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ChisimbaRestAPI {

	Context _context;
	IRestAPIDelegate _responseHandler;
	
	private static final String NAME_SEARCH_URL = "http://geo.chisimba.com:8080/documents/";
	private static final String LOC_SEARCH_URL = "http://geo.chisimba.com:8080/loc";

	private ChisimbaRestAPI(Context context) {
		_context = context;
	}
	
	private static ChisimbaRestAPI _instance;
	public static ChisimbaRestAPI initializedInstance(Context context) {
		if (null == _instance) {
			_instance = new ChisimbaRestAPI(context);			 
		}
		return _instance;
	}

	public void searchByName(String name, IRestAPIDelegate responseHandler) {
		
		_responseHandler = responseHandler;
		
		RequestParams params = new RequestParams();
		String ulrEncodedName = "";
		try {
			ulrEncodedName = URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
			ulrEncodedName = "";
			e.printStackTrace();
		}
		
		Log.d("CONSOLE", "url= " + NAME_SEARCH_URL+ulrEncodedName);
		doGetRequest(NAME_SEARCH_URL+ulrEncodedName, params);
	}

	public void searchByLocation(Double latitude, Double longitude, IRestAPIDelegate responseHandler) {
		
		_responseHandler = responseHandler;
		
		RequestParams params = new RequestParams();
		try {
			params.put("lat", URLEncoder.encode(latitude.toString(), "UTF-8"));
			params.put("lon", URLEncoder.encode(longitude.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
		doGetRequest(LOC_SEARCH_URL, params);
	}
	
	private void doGetRequest(String url, RequestParams params) {

		if (this.haveNetworkConnection()) {
			RestAPIClient.get(url, params, new AsyncHttpResponseHandler(){
				
				@Override
				public void onSuccess(String httpResponse) {
					super.onSuccess(httpResponse);
					
					// this line is a hack because of the API having lost its root tag
					httpResponse = "{\"data\": " + httpResponse + "}";
					
Log.d("CONSOLE", "httpResponse:" + httpResponse);					
					Gson gson = new Gson();
					ChisimbaResponse chisimbaResponse = gson.fromJson(httpResponse, ChisimbaResponse.class);
					
					_responseHandler.receiveSuccess(new RestResponse(chisimbaResponse.data, true));
				}
				
	            @Override
	            public void onFailure(Throwable throwable) {
	            	_responseHandler.receiveFailure(new RestFailure(_context.getString(R.string.api_error)));
	            }
			});
		} else {
			_responseHandler.receiveFailure(new RestFailure(_context.getString(R.string.no_network)));
		}
	}
	
	
	private boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) this._context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}
}
