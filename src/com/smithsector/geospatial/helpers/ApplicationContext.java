package com.smithsector.geospatial.helpers;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class ApplicationContext {

	private Context mContext;
	private LocationManager mLocationManager;
	// Listener that responds to location updates
	private LocationListener mLocationListener;
	// Last known location
	private Location mLocation;
	private String mUid;

	public String getUid() {
		return mUid;
	}

	private static final String SHARED_PREFS_FILE = "UherditPrefsFile";

	private ApplicationContext(Context context) {

		mContext = context;
		
		PrefsProvider prefs = new PrefsProvider(context, SHARED_PREFS_FILE);
		mUid = prefs.getString("UNIQUE_USER_ID", "");
		if (0 == mUid.length()) {
			mUid = UUID.randomUUID().toString();
			prefs.setString("UNIQUE_USER_ID", mUid);
		}

		// Acquire a reference to the system Location Manager
		mLocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		mLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				mLocation = location;
				
//				Toast.makeText(mContext,
//						"!!!!!! onLocationChanged",
//						Toast.LENGTH_SHORT).show();
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
	}

	private static ApplicationContext mInstance;

	public static ApplicationContext initializedInstance(Context context) {
		if (null == mInstance) {
			mInstance = new ApplicationContext(context);
		}
		mInstance.ensureGPSActive();
		// Log.d("CONSOLE", "uid: " + _instance.getUid());

		return mInstance;
	}

	public double getLatitude() {

		double lat = 0;
		try {
			lat = mLocation.getLatitude();			
		} catch (Exception e) {
			// return an invalid value
			lat = -777;
		}
		
		return lat;
	}

	public double getLongitude() {

		double lon = 0;
		try {
			lon = mLocation.getLongitude();			
		} catch (Exception e) {
			// return an invalid value
			lon = -777;
		}
		
		return lon;
	}

	public String currentUnixTimestamp() {

		return String.valueOf(System.currentTimeMillis() / 1000L);
	}
	
	public boolean ensureGPSActive() {
		
		// is GPS enabled?
		boolean gpsEnabled = mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {

			// go to Location Service settings
			Intent intent = new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(intent);
			
			gpsEnabled = mLocationManager
			.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}

		return gpsEnabled;
	}

}
