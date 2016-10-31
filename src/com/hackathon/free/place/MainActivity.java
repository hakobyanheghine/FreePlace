package com.hackathon.free.place;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.hackathon.free.place.backend.API;
import com.hackathon.free.place.backend.API.RequestObserver;
import com.hackathon.free.place.data.GroupOfRestaurants;
import com.hackathon.free.place.data.UserData;
import com.hackathon.free.place.manager.FreePlaceManager;
import com.hackathon.free.place.manager.PreferenceManager;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		setContentView(R.layout.activity_main);
		PreferenceManager preferenceManager = PreferenceManager.getInstance();
		preferenceManager.init(MainActivity.this);
		

		((Button) findViewById(R.id.fb_login_btn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Session.openActiveSession(MainActivity.this, true, new Session.StatusCallback() {

					// callback when session changes state
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if (session.isOpened()) {

							// make request to the /me API
							Request.newMeRequest(session, new Request.GraphUserCallback() {
			
								// callback after Graph API response with user object
								@Override
								public void onCompleted(GraphUser user, Response response) {
									if (user != null) {
										if(PreferenceManager.getInstance().getUserFbId().equals("")){
											API.addUser(user, new RequestObserver() {
												@Override
												public void onSuccess(JSONObject response) throws JSONException {
													JSONObject userObject = response.getJSONObject("user");
													UserData userData = new UserData(userObject);
													PreferenceManager.getInstance().setUserData(userData);
													startRestaurantListActivity();
													
												}
												@Override
												public void onError(String response, Exception e) {
													
												}
											});
										}else{
											startRestaurantListActivity();
										}
									}
								}						
								}).executeAsync();
						}
					}
				});
			}
		});
	}
	
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startRestaurantListActivity(){
		ArrayList<GroupOfRestaurants> tabGroups = FreePlaceManager.getInstance().groups;
		if(tabGroups.size() > 0){
			startRestaurantActivity();		
		}else{
			API.getAllRestaurants(new RequestObserver() {
				@Override
				public void onSuccess(JSONObject response) throws JSONException {				 
					FreePlaceManager.getInstance().createRestaurantData(response);
					startRestaurantActivity();
			
				}
				
				@Override
				public void onError(String response, Exception e) {
					
				}
			});
		}
	}
	
	private void startRestaurantActivity(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
						Intent restaurantsIntent = new Intent(MainActivity.this, RestaurantsListActivity.class);
						startActivity(restaurantsIntent);																	
			}
		});
	}


}
