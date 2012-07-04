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
		// The Activity created the MapView for us, so we can do some init
		// stuff.
		Exchanger.mMapView.setClickable(true);
		Exchanger.mMapView.setBuiltInZoomControls(true); // If you want.

		/*
		 * If you're getting Exceptions saying that the MapView already has a
		 * parent, uncomment the next lines of code, but I think that it won't
		 * be necessary. In other cases it was, but in this case I don't this
		 * should happen.
		 */
		/*
		 * final ViewGroup parent = (ViewGroup) Exchanger.mMapView.getParent();
		 * if (parent != null) parent.removeView(Exchanger.mMapView);
		 */

		return Exchanger.mMapView;
	}

	/*
	 * IRestAPIDelegate methods SECTION
	 */
	@Override
	public void receiveSuccess(RestResponse response) {
		
		Log.d("CONSOLE", "call: " + response.getCall());
		Log.d("CONSOLE", "success: " + response.getSuccess());
	}

	@Override
	public void receiveFailure(RestFailure failure) {

		Log.d("CONSOLE", "call" + failure.getMessage());
	}
	
}