package com.smithsector.geospatial.helpers;

import java.util.UUID;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ApplicationContext {

	//
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
				LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
	}

	private static ApplicationContext _instance;

	public static ApplicationContext initializedInstance(Context context) {
		if (null == _instance) {
			_instance = new ApplicationContext(context);
		}
		// Log.d("CONSOLE", "uid: " + _instance.getUid());

		return _instance;
	}

	public double getLatitude() {
		return mLocation.getLatitude();
	}

	public double getLongitude() {
		return mLocation.getLongitude();
	}

	public String currentUnixTimestamp() {

		return String.valueOf(System.currentTimeMillis() / 1000L);
	}
	
	public boolean isGPSActive() {
		
		// is GPS enabled?
		boolean gpsEnabled = mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		return gpsEnabled;
	}

}
