package com.hackathon.free.place.utils;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackathon.free.place.R;
import com.hackathon.free.place.data.RestaurantData;

public class RestaurantListAdapter extends ArrayAdapter<RestaurantData>{
	public LayoutInflater inflater;
	public Context context;
	
	public RestaurantListAdapter(Context context, int resource, List<RestaurantData> children) {
		super(context, resource, children);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_details, parent, false);
		}
		RestaurantData currentRestaurant = (RestaurantData) getItem(position);
		text = (TextView) convertView.findViewById(R.id.textView1);
		String restaurantData = currentRestaurant.name.toUpperCase() + "\n" + currentRestaurant.address;
		text.setText(restaurantData);
		return convertView;
	}
}
