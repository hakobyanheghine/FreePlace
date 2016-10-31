package com.hackathon.free.place.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hackathon.free.place.data.UserData;

public class PreferenceManager {

	private static PreferenceManager instance;
	
	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	public static final String NAME = "FreePlace";

	public static final String PREFERENCE_USER_ID = "user.id";
	public static final String PREFERENCE_USER_FBID = "user.fbId";
	public static final String PREFERENCE_USER_USERNAME = "user.userName";
	public static final String PREFERENCE_USER_FIRSTNAME = "user.firstName";
	public static final String PREFERENCE_USER_LASTNAME = "user.lastName";
	
	private PreferenceManager() {
		
	}
	
	public static PreferenceManager getInstance() {
		if (instance == null) {
			instance = new PreferenceManager();
		}
		return instance;
	}
	
	public void init(Context context) {
		sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}
	
	public String getUserId() {
		return sharedPreferences.getString(PREFERENCE_USER_ID, "");
	}

	public void setUserId(String value) {
		editor.putString(PREFERENCE_USER_ID, value).commit();
	}
	
	public String getUserFbId() {
		return sharedPreferences.getString(PREFERENCE_USER_FBID, "");
	}	
	public void setUserFbId(String value) {
		editor.putString(PREFERENCE_USER_FBID, value);
		editor.commit();
	}
	
	public String getUserName() {
		return sharedPreferences.getString(PREFERENCE_USER_USERNAME, "");
	}
	public void setUserName(String value) {
		editor.putString(PREFERENCE_USER_USERNAME, value);
		editor.commit();
	}
	
	public String getFirstName() {
		return sharedPreferences.getString(PREFERENCE_USER_FIRSTNAME, "");
	}
	public void setFirstName(String value) {
		editor.putString(PREFERENCE_USER_FIRSTNAME, value);
		editor.commit();
	}
	
	public String getLastName() {
		return sharedPreferences.getString(PREFERENCE_USER_LASTNAME, "");
	}
	public void setLastName(String value) {
		editor.putString(PREFERENCE_USER_LASTNAME, value);
		editor.commit();
	}

	public void setUserData(UserData userData) {
		editor.putString(PREFERENCE_USER_ID, userData.userId);
		editor.putString(PREFERENCE_USER_FBID, userData.userFbId);
		editor.putString(PREFERENCE_USER_USERNAME, userData.userName);
		editor.putString(PREFERENCE_USER_FIRSTNAME, userData.firstName);
		editor.putString(PREFERENCE_USER_LASTNAME, userData.lastName);
		editor.commit();
	}
	
	
}
