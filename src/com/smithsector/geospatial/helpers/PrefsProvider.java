package com.smithsector.geospatial.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsProvider {

	private Context _sharedContext;
	private String _sharedPrefsFile;
	
	public PrefsProvider(Context context, String file) {
		
		_sharedContext = context;
		_sharedPrefsFile = file;
	}
	
	public String getString(String key, String defaultValue) {
		
    	SharedPreferences sharedPrefs = _sharedContext.getSharedPreferences(_sharedPrefsFile, 0);
    	return sharedPrefs.getString(key, defaultValue);
	}
	
	public void setString(String key, String value) {
		
    	SharedPreferences.Editor sharedPrefsEditor = _sharedContext.getSharedPreferences(_sharedPrefsFile, Context.MODE_WORLD_WRITEABLE).edit();
    	sharedPrefsEditor.putString(key, value).commit();
	}
	
	public JSONObject getJson(String key, String defaultValue) {
		
		String jsonString = this.getString(key, defaultValue);
		try {
			return new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
