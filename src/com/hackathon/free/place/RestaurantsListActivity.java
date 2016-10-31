package com.hackathon.free.place;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hackathon.free.place.data.GroupOfRestaurants;
import com.hackathon.free.place.data.RestaurantData;
import com.hackathon.free.place.manager.FreePlaceManager;
import com.hackathon.free.place.utils.RestaurantListAdapter;

public class RestaurantsListActivity extends ActionBarActivity {
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurants_list);

		final ArrayList<GroupOfRestaurants> tabGroups = FreePlaceManager.getInstance().groups;
		int tabsCount = tabGroups.size();

		List<RestaurantData> children = new ArrayList<RestaurantData>();
		for (int tabIndex = 0; tabIndex < tabsCount; tabIndex++) {
			GroupOfRestaurants groupOfRestaurants = tabGroups.get(tabIndex);
			if (groupOfRestaurants.restaurantType == "all") {
				children = groupOfRestaurants.children;
			}
		}
		List<RestaurantData> childrenCopy = new ArrayList<RestaurantData>();
		childrenCopy.addAll(children);

		final RestaurantListAdapter adapter = new RestaurantListAdapter(RestaurantsListActivity.this, R.layout.listrow_details, childrenCopy);
		ListView listView = (ListView) findViewById(R.id.rest_list);
		listView.setAdapter(adapter);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange)));
		final ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				String currentTabTitle = tab.getText().toString();
				ArrayList<GroupOfRestaurants> tabGroups = FreePlaceManager.getInstance().groups;
				int tabsCount = tabGroups.size();
				for (int tabIndex = 0; tabIndex < tabsCount; tabIndex++) {
					GroupOfRestaurants groupOfRestaurants = tabGroups.get(tabIndex);
					if (groupOfRestaurants.restaurantType.toUpperCase().equals(currentTabTitle)) {
						List<RestaurantData> children = groupOfRestaurants.children;
						adapter.clear();
						adapter.addAll(children);
						adapter.notifyDataSetChanged();
						break;
					}
				}

			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		for (int tabIndex = tabsCount - 1; tabIndex >= 0; tabIndex--) {
			actionBar.addTab(actionBar.newTab().setText(tabGroups.get(tabIndex).restaurantType.toUpperCase()).setTabListener(tabListener));
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FreePlaceManager.getInstance().currentRestaurantData = (RestaurantData) adapter.getItem((int) id);
				startRestaurantInfoActivity();

			}

		});
	}

	public void startRestaurantInfoActivity() {
		Intent restaurantInfoActivity = new Intent(RestaurantsListActivity.this, RestaurantInfoActivity.class);
		startActivity(restaurantInfoActivity);
	}
}
