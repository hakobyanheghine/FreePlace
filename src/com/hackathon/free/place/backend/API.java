package com.hackathon.free.place.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.facebook.model.GraphUser;
import com.hackathon.free.place.utils.Constants;


public class API {
	
	public static String userId;
	
	public static final String ADD_USER = "add_user";
	public static final String GET_USER_DATA = "get_user_data";
	public static final String GET_RESTAURANT_DATA = "get_restaurant_data";
	public static final String GET_ALL_RESTAURANTS = "get_all_restaurants";
	
	public static final String TAG = "API";
	
	private static RequestThread requestThread = new RequestThread();
	private static LinkedList<RequestData> requestStack = new LinkedList<RequestData>();
	
	static {
		requestThread.start();
	}
	
	public static void addUser(GraphUser user, RequestObserver observer) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user_fb_id", user.getId()));
		String userName = "";
		if (user.getUsername() != null) {
			userName = user.getUsername();
		}
		params.add(new BasicNameValuePair("user_name", userName));
		params.add(new BasicNameValuePair("first_name", user.getFirstName()));
		params.add(new BasicNameValuePair("last_name", user.getLastName()));
		String gender = "";
		if (user.asMap().get("gender") != null) {
			gender = user.asMap().get("gender").toString()=="female"?"1":"0";
		}
		params.add(new BasicNameValuePair("gender", gender));
		String location = "";
		if (user.getLocation() != null) { 
			location = (String) user.getLocation().getProperty("name");
		}
		params.add(new BasicNameValuePair("location", location));
		
		sendAsyncRequestPost(ADD_USER, params, observer);
 	}
	
	public static void getUserData(String userId, RequestObserver observer) {
		sendAsyncRequestGet(GET_USER_DATA, "&user_id=" + userId, observer);
	}
	
	public static void getAllRestaurants(RequestObserver observer) {
		sendAsyncRequestGet(GET_ALL_RESTAURANTS, "", observer);
	}
	
	public static void getRestaurantData(int restaurantId, RequestObserver observer) {
		sendAsyncRequestGet(GET_RESTAURANT_DATA, "&restaurant_id=" + restaurantId, observer);
	}
	
	
	// helper functions to send get and post requests
	private static void sendAsyncRequestGet(String command, String requestStr, RequestObserver observer) {
		requestStr = Constants.SERVER_URL + command + requestStr;

		sendAsyncRequest(command, requestStr, RequestData.GET_METHOD, observer);
	}
	
	private static void sendAsyncRequestPost(String command, ArrayList<BasicNameValuePair> params, RequestObserver observer) {
		params.add(new BasicNameValuePair("user_id", userId));
		
		String requestStr = Constants.SERVER_URL + command;

		try {
			sendAsyncRequest(requestStr, command, RequestData.POST_METHOD, new UrlEncodedFormEntity(params), observer);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static RequestData sendAsyncRequest(String command, String requestStr, int resuestMethod, RequestObserver observer) {
		RequestData requestData = new RequestData();
		requestData.requestObserver = observer;
		requestData.requestStr = requestStr;
		requestData.command = command;
		requestData.requestMethod = resuestMethod;
		synchronized (requestStack) {
			requestStack.add(requestData);
			requestStack.notifyAll();
		}
		return requestData;
	}
	
	private static RequestData sendAsyncRequest(String requestStr, String command, int resuestMethod, HttpEntity params, RequestObserver observer) {
		RequestData requestData = new RequestData();
		requestData.requestObserver = observer;
		requestData.requestStr = requestStr;
		requestData.command = command;
		requestData.params = params;
		requestData.requestMethod = RequestData.POST_METHOD;
		synchronized (requestStack) {
			requestStack.add(requestData);
			requestStack.notifyAll();
		}
		return requestData;
	}
	
	public static String convertResponseToString(HttpResponse response) {
		StringBuilder sb = new StringBuilder();
		HttpEntity entity = response.getEntity();
		try {
			if (entity != null) {
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					instream.close();
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String resultString = sb.toString();
		return resultString;
	}
	
	private static class RequestThread extends Thread {

		private DefaultHttpClient httpClient;

		public RequestThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (requestStack.size() == 0) {
						synchronized (requestStack) {
							requestStack.wait();
						}
					}
					if (requestStack.size() != 0) {
						RequestData requestData;
						synchronized (requestStack) {
							requestData = requestStack.get(0);
						}
						if (httpClient == null) {
							httpClient = new DefaultHttpClient();
						}
						HttpRequestBase request = null;
						if (requestData.requestMethod == RequestData.GET_METHOD) {
							request = new HttpGet(requestData.requestStr);
						} else if (requestData.requestMethod == RequestData.POST_METHOD) {
							request = new HttpPost(requestData.requestStr);
							((HttpPost) request).setEntity(requestData.params);
						}
						Log.i(TAG, "request = " + requestData.requestStr);
						
						HttpResponse response = null;
						int statusCode = -1;
						String responseStr = null;
						try {
							Log.i(TAG, "request = " + requestData.requestStr);

							response = httpClient.execute(request);
							statusCode = response.getStatusLine().getStatusCode();
							
							Log.i(TAG, "statusCode = " + statusCode);
							
							if (statusCode >= 500) {
								throw new IOException("statusCode: " + statusCode);
							}
							
							responseStr = convertResponseToString(response);
							Log.i(TAG, "response = " + responseStr);
							
							JSONObject jsonObject = null;
							try {
								jsonObject = new JSONObject(responseStr);
							} catch (JSONException e) {
								JSONArray jsonArray = new JSONArray(responseStr);
								jsonObject = new JSONObject();
								jsonObject.put("values", jsonArray);
							}
							if (requestData.requestObserver != null) {
								requestData.requestObserver.onSuccess(jsonObject);
							}

							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						} catch (IOException e) {
							e.printStackTrace();
							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				Log.d(TAG, "api is interapted " + e);
			}
		}
	}

	public static class RequestData {
		public final static int POST_METHOD = 1;
		public final static int GET_METHOD = 2;
		
		public RequestObserver requestObserver;
		public int requestMethod = GET_METHOD;
		public String requestStr;
		public String command;
		public HttpEntity params;
		
		public void setRequestObserver(RequestObserver requestObserver){
			 this.requestObserver = requestObserver;
		}
	}

	public static interface RequestObserver {
		public void onSuccess(JSONObject response) throws JSONException;

		public void onError(String response, Exception e);
	}

}
