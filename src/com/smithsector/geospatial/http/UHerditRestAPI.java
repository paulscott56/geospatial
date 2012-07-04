package com.smithsector.geospatial.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.smithsector.geospatial.R;

public class UHerditRestAPI {

	Context _context;
	IRestAPIDelegate _responseHandler;
	
	private static final String DESTINATION_URL = "http://10.0.0.4/index.php";
	private static final String ACTION_REPORT = "report"; 
	private static final String ACTION_SEARCH = "search"; 
//	private static final String ACTION_AUDIO = "audio"; 

	private UHerditRestAPI(Context context) {
		_context = context;
	}
	
	private static UHerditRestAPI _instance;
	public static UHerditRestAPI initializedInstance(Context context) {
		if (null == _instance) {
			_instance = new UHerditRestAPI(context);			 
		}
		return _instance;
	}

	public void report(String uid, Double lat, Double lon, String occurence, File soundFile, IRestAPIDelegate responseHandler) {
		
		_responseHandler = responseHandler;
		
		RequestParams params = new RequestParams();
		try {
			params.put("action", URLEncoder.encode(ACTION_REPORT, "UTF-8"));
			params.put("uid", URLEncoder.encode(uid, "UTF-8"));
			params.put("lat", URLEncoder.encode(lat.toString(), "UTF-8"));
			params.put("lon", URLEncoder.encode(lon.toString(), "UTF-8"));
			params.put("occ", URLEncoder.encode(occurence, "UTF-8"));
			params.put("uploadedfile", soundFile);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
		if (this.haveNetworkConnection()) {
			
			RestAPIClient.post(DESTINATION_URL, params, new AsyncHttpResponseHandler(){
				
				@Override
				public void onSuccess(String jsonResponse) {
					super.onSuccess(jsonResponse);
					
					Gson gson = new Gson();
					RestResponse response = gson.fromJson(jsonResponse, RestResponse.class);
					
					_responseHandler.receiveSuccess(response);
				}
				
	            @Override
	            public void onFailure(Throwable throwable) {
					Log.d("CONSOLE", _context.getString(R.string.api_error));
					
	            	_responseHandler.receiveFailure(new RestFailure(_context.getString(R.string.api_error)));
	            }
			});
		}
	}		

	public void search(Double lat, Double lon, int radius, int age, IRestAPIDelegate responseHandler) {
		
		_responseHandler = responseHandler;
		
		RequestParams params = new RequestParams();
		try {
			params.put("action", URLEncoder.encode(ACTION_SEARCH, "UTF-8"));
			params.put("lat", URLEncoder.encode(lat.toString(), "UTF-8"));
			params.put("lon", URLEncoder.encode(lon.toString(), "UTF-8"));
			params.put("r", URLEncoder.encode(String.valueOf(radius), "UTF-8"));
			params.put("age", URLEncoder.encode(String.valueOf(age), "UTF-8"));
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
		if (this.haveNetworkConnection()) {
			
			RestAPIClient.post(DESTINATION_URL, params, new AsyncHttpResponseHandler(){
				
				@Override
				public void onSuccess(String jsonResponse) {
					super.onSuccess(jsonResponse);
					
					Gson gson = new Gson();
					RestResponse response = gson.fromJson(jsonResponse, RestResponse.class);
					
//					GeoNamesResponse geonamesResponse = gson.fromJson(jsonResponse, GeoNamesResponse.class);
//					
//					List<Place> placesFound = new ArrayList<Place>();
//					for (GeoName geoname : geonamesResponse.getGeoNames()) {
//						
//						Place placeFound = new Place(geoname.name, geoname.adminName1, geoname.countryName, geoname.lat, geoname.lng);
//						placesFound.add(placeFound);
//					}
					Log.d("CONSOLE", jsonResponse);
					_responseHandler.receiveSuccess(response);
				}
				
	            @Override
	            public void onFailure(Throwable throwable) {
					Log.d("CONSOLE", _context.getString(R.string.api_error));
					
	            	_responseHandler.receiveFailure(new RestFailure(_context.getString(R.string.api_error)));
	            }
			});
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
