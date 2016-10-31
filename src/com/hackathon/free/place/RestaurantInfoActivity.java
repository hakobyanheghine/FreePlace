package com.hackathon.free.place;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.free.place.data.RestaurantTableData;
import com.hackathon.free.place.manager.FreePlaceManager;

public class RestaurantInfoActivity extends ActionBarActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_restaurant_info);
		
		Log.d("heghine", "id = " + FreePlaceManager.getInstance().currentRestaurantData.restaurantId);
		
		((TextView) findViewById(R.id.restaurant_name_txt)).setText(FreePlaceManager.getInstance().currentRestaurantData.name);
		((TextView) findViewById(R.id.restaurant_address_txt)).setText(FreePlaceManager.getInstance().currentRestaurantData.address);
		((TextView) findViewById(R.id.restaurant_phone_txt)).setText(FreePlaceManager.getInstance().currentRestaurantData.phone);
		((TextView) findViewById(R.id.restaurant_working_hours_txt)).setText(FreePlaceManager.getInstance().currentRestaurantData.workingHours);
		((TextView) findViewById(R.id.restaurant_description_txt)).setText(FreePlaceManager.getInstance().currentRestaurantData.description);
		((RatingBar) findViewById(R.id.restaurant_rating_bar)).setRating(FreePlaceManager.getInstance().currentRestaurantData.rating);
		((Button) findViewById(R.id.add_review_btn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startFeedbackDialog();
			}
		});
		
		GridView gridview = (GridView) findViewById(R.id.restaurant_plan_container);
		gridview.setNumColumns(FreePlaceManager.getInstance().currentRestaurantData.width);
	    gridview.setAdapter(new GridViewAdapter(this, FreePlaceManager.getInstance().currentRestaurantData.tableDataList));
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	Log.d("heghine", "item = " + FreePlaceManager.getInstance().currentRestaurantData.tableDataList.get(position).tableId);
	        	startReservationDialog(FreePlaceManager.getInstance().currentRestaurantData.tableDataList.get(position).tableId);
	        }
	    });
	}
	
	public void startFeedbackDialog() {
		final Dialog feedbackDialog = new Dialog(RestaurantInfoActivity.this, R.style.Theme_Base_AppCompat_DialogWhenLarge_Base);
		feedbackDialog.setContentView(R.layout.dialog_restaurant_rating);
		feedbackDialog.setTitle(R.string.rating_dialog_title);
				
		((Button) feedbackDialog.findViewById(R.id.done)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int rating = (int)((RatingBar) feedbackDialog.findViewById(R.id.rating)).getRating();
				String review = ((EditText) feedbackDialog.findViewById(R.id.review)).getText().toString();
				Log.d("heghine", "rating = " + rating + "; review = " + review);
				// sned review to backend
				((Button) findViewById(R.id.add_review_btn)).setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
				feedbackDialog.dismiss();
			}
		});		
		feedbackDialog.show();
	}
	
	public void startReservationDialog(int tableId) {
		final Dialog reservationDialog = new Dialog(RestaurantInfoActivity.this, R.style.Theme_Base_AppCompat_DialogWhenLarge_Base);
		reservationDialog.setContentView(R.layout.dialog_table_reservation);
		reservationDialog.setTitle(R.string.reserve);
				
		((TextView) reservationDialog.findViewById(R.id.table_name_txt)).setText("Table #" + tableId);
		((Button) reservationDialog.findViewById(R.id.done)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "Reserved!", Toast.LENGTH_SHORT).show();
				reservationDialog.dismiss();
			}
		});		
		reservationDialog.show();
	}
	
	public class GridViewAdapter extends BaseAdapter {

	    private Context context;
	    private ArrayList<RestaurantTableData> tables;

	    public GridViewAdapter(Context context, ArrayList<RestaurantTableData> tables) {
	        this.context = context;
	        this.tables = tables;
	        
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {

	        if (convertView == null) {
	        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.item_restaurant_table, null);
	        }
	        
	        RestaurantTableData currentTable = (RestaurantTableData) getItem(position);
	        if (currentTable.isTable) {
	        	((LinearLayout) convertView.findViewById(R.id.table_data_container)).setBackgroundResource(R.drawable.textview_shape);
	        	((TextView) convertView.findViewById(R.id.table_name_txt)).setText("#" + currentTable.tableId);
	        }
	        return convertView;
	    }

	    @Override
	    public int getCount() {
	        return tables.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return tables.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	}

}
