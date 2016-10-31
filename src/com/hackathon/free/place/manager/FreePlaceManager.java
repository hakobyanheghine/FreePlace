package com.hackathon.free.place.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.hackathon.free.place.data.GroupOfRestaurants;
import com.hackathon.free.place.data.RestaurantData;
import com.hackathon.free.place.utils.Constants;

public class FreePlaceManager {
	
	private static FreePlaceManager instance;
	
	public RestaurantData currentRestaurantData;
	
	public ArrayList<GroupOfRestaurants> groups = new ArrayList<GroupOfRestaurants>();
	
	private FreePlaceManager() {
		
	}
	
	public static FreePlaceManager getInstance() {
		if (instance == null) {
			instance = new FreePlaceManager();
		}
		
		return instance;
	}
	
	
	public void createRestaurantData(JSONObject response) throws JSONException {
		int restaurantCount = response.getJSONArray("values").length();

		Map<String, GroupOfRestaurants> mapRestaurants = new HashMap<String,GroupOfRestaurants>();
		for (int resIndex = 0; resIndex < restaurantCount; resIndex++) {
			RestaurantData restaurantData = new RestaurantData(response.getJSONArray("values").getJSONObject(resIndex));
			if(resIndex == 0){
				GroupOfRestaurants group = new GroupOfRestaurants(Constants.RESTAURANT_ALL);				
				mapRestaurants.put(Constants.RESTAURANT_ALL,group);				
			}
			if(!mapRestaurants.keySet().contains(restaurantData.type)){
				GroupOfRestaurants group = new GroupOfRestaurants(restaurantData.type);				
				mapRestaurants.put(restaurantData.type,group);
				
			}
			mapRestaurants.get(restaurantData.type).children.add(restaurantData);
			mapRestaurants.get(Constants.RESTAURANT_ALL).children.add(restaurantData);
		}
		
		int restaurantTypesCount = mapRestaurants.size();
		
		for (int resTypeIndex = 0; resTypeIndex < restaurantTypesCount; resTypeIndex++) {
			groups.add((GroupOfRestaurants)mapRestaurants.values().toArray()[resTypeIndex]);
		}

	}

}
