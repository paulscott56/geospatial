package com.smithsector.geospatial;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.smithsector.geospatial.chisimba.ChisimbaRestAPI;
import com.smithsector.geospatial.entities.Place;
import com.smithsector.geospatial.helpers.ApplicationContext;
import com.smithsector.geospatial.http.IRestAPIDelegate;
import com.smithsector.geospatial.http.RestFailure;
import com.smithsector.geospatial.http.RestResponse;
import com.smithsector.geospatial.MainActivity.Exchanger;

/**
 * This is the Fragment class that will hold the MapView as its content view
 */
public class MapFragment extends SherlockFragment implements IRestAPIDelegate {

	public static final String TAG = "MapFragment";
	private Drawable mRedPinDrawable;
	private Drawable mBluePinDrawable;

	private List<Overlay> mMapOverlays;
	private ItemizedPOIsOverlay mItemizedPOIsOverlay;

	private ProgressDialog _dialog;

	public MapFragment() {
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
		// The MainActivity created the MapView, additional initialization can
		// take place here
		Exchanger.mMapView.setClickable(true);
		Exchanger.mMapView.setBuiltInZoomControls(true); // If you want.

		MapController mapController = Exchanger.mMapView.getController();
		mapController.setZoom(2);

		mRedPinDrawable = this.getResources().getDrawable(R.drawable.red_dot);
		mBluePinDrawable = this.getResources().getDrawable(R.drawable.blue_dot);

		return Exchanger.mMapView;
	}

	public boolean submitLocationQuery() {

		ApplicationContext ac = ApplicationContext
				.initializedInstance(getActivity());

		double lat = ac.getLatitude();
		double lon = ac.getLongitude();

		if ((-777 == lat) || (-777 == lon)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("No Location currently available")
					.setCancelable(true);
			AlertDialog alert = builder.create();

			alert.show();
		} else {

			_dialog = ProgressDialog.show(getActivity(),
					getString(R.string.searching_for_hint) + " "
							+ getString(R.string.nearby_poi_text),
					getString(R.string.patience_hint) + "...", true);
			ChisimbaRestAPI api = ChisimbaRestAPI
					.initializedInstance(getActivity());
			api.searchByLocation(ac.getLatitude(), ac.getLongitude(), this);
		}

		return false;
	}

	public void addPOIs(List<?> poiList) {

		populateOverlays(poiList, mBluePinDrawable);
	}

	public void clearPOIs() {

		mMapOverlays = Exchanger.mMapView.getOverlays();
		mMapOverlays.clear();
		Exchanger.mMapView.invalidate();
	}

	private void populateOverlays(List<?> poiList, Drawable pinDrawable) {

		if (poiList.size() > 0) {

			mMapOverlays = Exchanger.mMapView.getOverlays();

			if (null != mMapOverlays) {

				double maxLat = Integer.MIN_VALUE;// (int) (90 * 1e6);
				double minLat = Integer.MAX_VALUE;// 0;
				double maxLon = Integer.MIN_VALUE;// (int) (180 * 1e6);
				double minLon = Integer.MAX_VALUE;// (int) (-180 * 1e6);

				mItemizedPOIsOverlay = new ItemizedPOIsOverlay(getActivity(),
						pinDrawable);

				for (Object placeObj : poiList) {
					Place place = ((Place) placeObj);

					// in order for mapping use coordinates need to be in Micro
					// degrees
					int lat = (int) (place.latitude[0] * 1e6);
					int lon = (int) (place.longitude[0] * 1e6);
					Log.d("CONSOLE", ">>>>> lat: " + lat + ", lon: " + lon);

					// keep track of minimum and maximums
					maxLat = Math.max(lat, maxLat);
					minLat = Math.min(lat, minLat);
					maxLon = Math.max(lon, maxLon);
					minLon = Math.min(lon, minLon);

					String type = place.type[0];
					String payload = place.name[0] + "\n" + place.admin1code[0]
							+ "\n" + place.countrycode[0] + "\n"
							+ "elevation: " + place.elevation[0] + "\n"
							+ "featurecode: " + place.featurecode[0] + "\n"
							+ "population: " + place.population[0];

					GeoPoint point = new GeoPoint(lat, lon);
					OverlayItem overlayitem = new OverlayItem(point, type,
							payload);
					mItemizedPOIsOverlay.addOverlay(overlayitem);

					fitOverlays();
					// fitAndCenter(maxLat, minLat, maxLon, minLon);
					// Log.d("CONSOLE", "added " + payload);
				}

				mMapOverlays.add(mItemizedPOIsOverlay);

				Exchanger.mMapView.invalidate();
			}
		}
	}

	public void fitOverlays() {
		
		// Zoom
		Exchanger.mMapView.getController().zoomToSpan(mItemizedPOIsOverlay.getLatSpanE6(),
				mItemizedPOIsOverlay.getLonSpanE6());
		
		// Center
		Exchanger.mMapView.getController().setCenter(mItemizedPOIsOverlay.getCenter());
	}

	private void fitAndCenter(double maxLat, double minLat, double maxLon,
			double minLon) {

		Log.d("CONSOLE", "^^^^^ maxLat: " + maxLat);
		Log.d("CONSOLE", "^^^^^ minLat: " + minLat);
		Log.d("CONSOLE", "^^^^^ maxLon: " + maxLon);
		Log.d("CONSOLE", "^^^^^ minLon: " + minLon);

		MapController mapController = Exchanger.mMapView.getController();

		// Center
		int centerLat = (int) (Math.abs(maxLat - minLat) / 2);
		int centerLon = (int) (Math.abs(maxLon - minLon) / 2);
		GeoPoint centerPoint = new GeoPoint(centerLat, centerLon);
		mapController.setCenter(centerPoint);

		// Zoom
		double fitFactor = 1.5;
		mapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor),
				(int) (Math.abs(maxLon - minLon) * fitFactor));
	}

	/*
	 * IRestAPIDelegate methods SECTION
	 */
	@Override
	public void receiveSuccess(RestResponse response) {

		populateOverlays(response.getData(), mRedPinDrawable);
		_dialog.cancel();
	}

	@Override
	public void receiveFailure(RestFailure failure) {

		Log.e("CONSOLE", "call" + failure.getMessage());
		_dialog.cancel();
	}

}