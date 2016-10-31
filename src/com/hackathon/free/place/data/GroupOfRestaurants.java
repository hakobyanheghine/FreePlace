package com.hackathon.free.place.data;

import java.util.ArrayList;
import java.util.List;


public class GroupOfRestaurants {
	  public String restaurantType;
	  public final List<RestaurantData> children = new ArrayList<RestaurantData>();

	  public GroupOfRestaurants(String restaurantType) {
	    this.restaurantType = restaurantType;
	  }

}
