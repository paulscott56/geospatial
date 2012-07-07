package com.smithsector.geospatial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.smithsector.geospatial.http.IRestAPIDelegate;
import com.smithsector.geospatial.http.RestFailure;
import com.smithsector.geospatial.http.RestResponse;
import com.smithsector.geospatial.MainActivity.Exchanger;

/**
 * This is the Fragment class that will hold the MapView as its content view
 */
public class MapFragment extends SherlockFragment implements IRestAPIDelegate {

	public static final String TAG = "mapFragment";

	public MapFragment() {
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
		// The MainActivity created the MapView, additional initialization can take place here
		Exchanger.mMapView.setClickable(true);
		Exchanger.mMapView.setBuiltInZoomControls(true); // If you want.

		return Exchanger.mMapView;
	}

	/*
	 * IRestAPIDelegate methods SECTION
	 */
	@Override
	public void receiveSuccess(RestResponse response) {
		
		Log.d("CONSOLE", "success: " + response.getSuccess());
		Log.d("CONSOLE", "data: " + response.getData());
	}

	@Override
	public void receiveFailure(RestFailure failure) {

		Log.d("CONSOLE", "call" + failure.getMessage());
	}
	
}