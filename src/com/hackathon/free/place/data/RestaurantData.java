package com.hackathon.free.place.data;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class RestaurantData {
	
	public int restaurantId;
	public String name;
	public String type;
	public String address;
	public String phone;
	public String workingHours;
	public String description;
	public int width;
	public int height;
	public int rating;
	
	public ArrayList<RestaurantTableData> tableDataList = new ArrayList<RestaurantTableData>();
	
	public RestaurantData(JSONObject data) {
		restaurantId = data.optInt("restaurant_id", 0);
		name = data.optString("name", "");
		type = data.optString("type", "");
		address = data.optString("address", "");
		phone = data.optString("phone", "");
		workingHours = data.optString("working_hours", "");
		description = data.optString("description", "");
		width = data.optInt("width", 1);
		height = data.optInt("height", 1);
		rating = data.optInt("rating", 5);
		
		JSONArray planJson = data.optJSONArray("plan");
		for (int i = 0; i < planJson.length(); i++) {
			RestaurantTableData tableData = new RestaurantTableData(planJson.optJSONObject(i));
			tableDataList.add(tableData);
		}
	}
}
