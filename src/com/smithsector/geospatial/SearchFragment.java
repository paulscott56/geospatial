package com.smithsector.geospatial;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.smithsector.geospatial.chisimba.ChisimbaRestAPI;
import com.smithsector.geospatial.entities.Place;
import com.smithsector.geospatial.http.IRestAPIDelegate;
import com.smithsector.geospatial.http.RestFailure;
import com.smithsector.geospatial.http.RestResponse;

public class SearchFragment extends SherlockFragment implements
		IRestAPIDelegate {
	public static final String TAG = "contributeFragment";

	private Context mContext;

	private ProgressDialog _dialog;
	private EditText _placesSearchEditText;
	private ListView _foundPlacesListView;
	private FoundPlacesArrayAdapter _foundPlacesAdapter;

	public SearchFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group,
			Bundle saved) {
		mContext = getActivity();
		return inflater.inflate(R.layout.search_fragment, group, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		_placesSearchEditText = (EditText) getActivity().findViewById(
				R.id.placesSearchEditText);
		_placesSearchEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId,
							KeyEvent event) {

						// only when the Search button on the keyboard was
						// pressed
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							String placeName = view.getText().toString();

							if (placeName.length() > 0) {
								submitPlaceQuery(placeName);
							}
						}

						return false;
					}
				});

		ImageButton clearSearchButton = (ImageButton) getActivity()
				.findViewById(R.id.clear_search_image_button);
		clearSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_placesSearchEditText.setText("");
				_foundPlacesAdapter.setPlaces(new ArrayList<Place>());
				_foundPlacesAdapter.notifyDataSetChanged();
			}
		});

//		_foundPlacesListView = (ListView) getActivity().findViewById(R.id.foundPlacesListView);
//
//		_foundPlacesAdapter = new FoundPlacesArrayAdapter(mContext,
//				R.layout.place_listitem, new ArrayList<Place>());
//		
//		_foundPlacesListView.setAdapter(_foundPlacesAdapter);
//		_foundPlacesListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
////				Place place = _foundPlacesAdapter.getItem(position);
//
//			}
//		});

	}

	public boolean submitPlaceQuery(String placeName) {

		_dialog = ProgressDialog.show(getActivity(), getString(R.string.searching_for_hint) + " " + placeName, getString(R.string.patience_hint) + "...", true);
		ChisimbaRestAPI api = ChisimbaRestAPI.initializedInstance(getActivity());
		api.searchByName(placeName, this);

		return false;
	}	

	/*
	 * IRestAPIDelegate methods SECTION
	 */
	@Override
	public void receiveSuccess(RestResponse response) {

		Log.d("CONSOLE", "success: " + response.getSuccess());
		// Log.d("CONSOLE", "data: " + response.getData());
		response.debug();
		
		_dialog.cancel();
	}

	@Override
	public void receiveFailure(RestFailure failure) {

		Log.d("CONSOLE", "call" + failure.getMessage());

		_dialog.cancel();
	}
	
	private class FoundPlacesArrayAdapter extends ArrayAdapter<Place> {

		private static final String tag = "PlaceArrayAdapter";

		private TextView _placeName;
		private TextView _placeProvince;
		private TextView _placeCountry;
		private List<Place> _places = new ArrayList<Place>();

		public FoundPlacesArrayAdapter(Context context, int textViewResourceId,
				List<Place> objects) {
			super(context, textViewResourceId, objects);
			this._places = objects;
		}
		
		public void setPlaces(List<Place>places) {
			_places = places;
		}

		public int getCount() {
			return this._places.size();
		}

		public Place getItem(int index) {
			return this._places.get(index);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// ROW INFLATION
				Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.place_listitem, parent, false);
				Log.d(tag, "Successfully completed XML Row Inflation!");
			}

			// Get item
			Place place = getItem(position);
			_placeName = (TextView) row.findViewById(R.id.place_name);
			_placeProvince = (TextView) row.findViewById(R.id.place_province);
			_placeCountry = (TextView)row.findViewById(R.id.place_country);

			_placeName.setText(place.name[0]);
			_placeProvince.setText(place.timezone[0]);
			_placeCountry.setText(place.countrycode[0]);

			return row;
		}
		
		public void remove(Place place) {
			
		}

	}

}
