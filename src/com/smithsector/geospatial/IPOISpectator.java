package com.smithsector.geospatial;

import java.util.List;

public interface IPOISpectator {
	
	public void receivePOIs(List<?> poiList);
	public void clearPOIs();

}
