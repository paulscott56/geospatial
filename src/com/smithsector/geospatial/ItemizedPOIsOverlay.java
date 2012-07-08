package com.smithsector.geospatial;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedPOIsOverlay extends ItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	
	public ItemizedPOIsOverlay(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlayItems.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {

		return mOverlayItems.get(i);
	}

	@Override
	public int size() {

		return mOverlayItems.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		
	  OverlayItem overlayItem = mOverlayItems.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(overlayItem.getTitle());
	  dialog.setMessage(overlayItem.getSnippet());
	  dialog.show();
	  return true;
	}
	
}
