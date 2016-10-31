package com.hackathon.free.place.data;

import org.json.JSONObject;

import com.facebook.model.GraphUser;

public class UserData {
	public String userId;
	public String userFbId;
	public String userName;
	public String firstName;
	public String lastName;
	
	public UserData() {
		
	}
	
	public UserData(JSONObject data) {
		userId = data.optString("user_id", "");
		userFbId = data.optString("user_fb_id", "");
		userName = data.optString("user_name", "");
		firstName = data.optString("first_name", "");
		lastName = data.optString("last_name", "");
	}
	
	public UserData(GraphUser user) {
		userFbId = user.getId();
		userName = user.getUsername();
		firstName = user.getFirstName();
		lastName = user.getLastName();
	}
	
}
