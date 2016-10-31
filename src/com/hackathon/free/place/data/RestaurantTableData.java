package com.hackathon.free.place.data;

import org.json.JSONObject;

public class RestaurantTableData {
	
	public boolean isTable;
	public int tableId;
	public int placeCount;
	
	public RestaurantTableData(JSONObject data) {
		isTable = data.optInt("is_table", 0) == 0 ? false : true;
		tableId = data.optInt("table_id", 0);
		placeCount = data.optInt("place_count", 0);
	}

}
