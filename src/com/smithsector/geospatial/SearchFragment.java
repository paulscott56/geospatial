package com.smithsector.geospatial;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.smithsector.geospatial.chisimba.ChisimbaRestAPI;
import com.smithsector.geospatial.http.IRestAPIDelegate;
import com.smithsector.geospatial.http.RestFailure;
import com.smithsector.geospatial.http.RestResponse;

public class SearchFragment extends SherlockFragment implements
		IRestAPIDelegate {
	public static final String TAG = "SearchFragment";
	public IPOISpectator mPOISpectator;
	
	private ProgressDialog _dialog;
	private EditText _placesSearchEditText;

	public SearchFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group,
			Bundle saved) {

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
				mPOISpectator.clearPOIs();
			}
		});

		ImageButton startSearchButton = (ImageButton) getActivity()
				.findViewById(R.id.search_image);
		startSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String searchCriteria = _placesSearchEditText.getText().toString();
				if (searchCriteria.length() > 0) {
					submitPlaceQuery(searchCriteria);
				}				
			}
		});

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
		// response.debug();
		
		_dialog.cancel();
		mPOISpectator.receivePOIs(response.getData());
	}

	@Override
	public void receiveFailure(RestFailure failure) {

		Log.d("CONSOLE", "call" + failure.getMessage());

		_dialog.cancel();
	}
	
}
