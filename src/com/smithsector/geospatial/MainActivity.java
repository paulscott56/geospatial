package com.smithsector.geospatial;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.smithsector.geospatial.helpers.ApplicationContext;

public class MainActivity extends SherlockFragmentActivity implements IPOISpectator {

	private ApplicationContext mApplicationContext;
	private MyLocationOverlay mCurrentLocationOverlay;
	private MapFragment mMapFragment;
	private SearchFragment mSearchFragment;

	// We use this fragment as a pointer to the visible one, so we can hide it
	// easily.
	private Fragment mVisible = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mApplicationContext = ApplicationContext.initializedInstance(this);

		// We instantiate the MapView here, it's really important!
		setupMapping();

		setupFragments();
		// We manually show the list Fragment.
		showFragment(mMapFragment);
	}

	private void setupMapping() {

//		// DEV >>
//		Exchanger.mMapView = new MapView(this,
//				"0KidLa8F-i8OUWtEdF2Hy2aSZGmZfWG6JYb4mKw");
		// RELEASE >>
		Exchanger.mMapView = new MapView(this,
				"07ADQq9RlfY8cxyZ3_GWUFpt47I8dJcCD4BLndA");

		mCurrentLocationOverlay = new MyLocationOverlay(this,
				Exchanger.mMapView);

		Exchanger.mMapView.getOverlays().add(mCurrentLocationOverlay);

		mCurrentLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				Exchanger.mMapView.getController().animateTo(
						mCurrentLocationOverlay.getMyLocation());
			}
		});
	}

	/**
	 * This method does the setting up of the Fragments. It basically checks if
	 * the fragments exist and if they do, we'll hide them. If the fragments
	 * don't exist, we create them, add them to the FragmentManager and hide
	 * them.
	 */
	private void setupFragments() {

		final FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();

		/*
		 * If the activity is killed while in BG, it's possible that the
		 * fragment still remains in the FragmentManager, so, we don't need to
		 * add it again.
		 */
		mMapFragment = (MapFragment) getSupportFragmentManager()
				.findFragmentByTag(MapFragment.TAG);
		if (mMapFragment == null) {
			mMapFragment = new MapFragment();
			ft.add(R.id.fragment_container, mMapFragment, MapFragment.TAG);
		}
		ft.hide(mMapFragment);

		mSearchFragment = (SearchFragment) getSupportFragmentManager()
				.findFragmentById(R.id.place_search_fragment);
		mSearchFragment.mPOISpectator = this;

		ft.commit();
	}

	/**
	 * This method shows the given Fragment and if there was another visible
	 * fragment, it gets hidden. We can just do this because we know that both
	 * the mMyListFragment and the mMapFragment were added in the Activity's
	 * onCreate, so we just create the fragments once at first and not every
	 * time. This will avoid facing some problems with the MapView.
	 * 
	 * @param fragmentIn
	 *            The fragment to show.
	 */
	private void showFragment(Fragment fragmentIn) {
		if (fragmentIn == null)
			return;

		final FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

		if (mVisible != null)
			ft.hide(mVisible);

		ft.show(fragmentIn).commit();
		mVisible = fragmentIn;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ic_locate:
			if (mApplicationContext.isGPSActive()) {

				Exchanger.mMapView.getOverlays().add(mCurrentLocationOverlay);

				mCurrentLocationOverlay.enableMyLocation();
				showFragment(mMapFragment);
				
				mMapFragment.submitLocationQuery();
			} else {
				// go to Location Service settings
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
			return true;

		case R.id.ic_map:

			mCurrentLocationOverlay.disableMyLocation();
			showFragment(mMapFragment);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This class acts as an exchanger between the Activity and the MapFragment
	 */
	public static class Exchanger {
		// We will always use this MapView.
		public static MapView mMapView;
	}
	
	/*
	 * IPOISpectator SECTION
	 */
	public void receivePOIs(List<?> poiList) {
		mMapFragment.addPOIs(poiList);
	}
	
	public void clearPOIs() {
		mMapFragment.clearPOIs();
	}
}