package com.cartwheels;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.savagelook.android.UrlJsonAsyncTask;

public class LoginActivity extends Activity {

	private final static String LOGIN_API_ENDPOINT_URL = "http://cartwheels.us/mobile/sessions";
	
	private SharedPreferences preferences;
	private String userEmail;
	private String userPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new LoginFragment()).commit();
		}
		
		preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		if (intent.getBooleanExtra("logout", false)) {
			logout();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	
	/*
	 * Have back pressed do nothing if the user is not logged in
	 */
	@Override
	public void onBackPressed() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
	}
	
	public void login(View view) {
	    EditText userEmailField = (EditText) findViewById(R.id.userEmail);
	    userEmail = userEmailField.getText().toString();
	    EditText userPasswordField = (EditText) findViewById(R.id.userPassword);
	    userPassword = userPasswordField.getText().toString();

	    if (userEmail.length() == 0 || userPassword.length() == 0) {
	        // input fields are empty
	        Toast.makeText(this, "Please complete all the fields",
	            Toast.LENGTH_SHORT).show();
	        return;
	    } else {
	        LoginTask loginTask = new LoginTask(LoginActivity.this);
	        loginTask.setMessageLoading("Logging in...");
	        loginTask.execute(LOGIN_API_ENDPOINT_URL);
	    }
	}
	
	public void logout() {
		LogoutTask logoutTask = new LogoutTask(LoginActivity.this);
		logoutTask.setMessageLoading("Logging out...");
		logoutTask.execute(LOGIN_API_ENDPOINT_URL);
	}

	public void switchRegister(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		intent.putExtra("user", true);
		startActivity(intent);
	}
	
	public void switchRegisterOwner(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		intent.putExtra("user", false);
		startActivity(intent);
	}
	
	private class LogoutTask extends UrlJsonAsyncTask {
		public LogoutTask(Context context) {
			super(context);
		}
		
		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			
			String response = null;
			JSONObject json = new JSONObject();
			
			try {
				Builder uri = new Builder();
				uri.scheme("http").authority("cartwheels.us").appendPath("mobile")
					.appendPath("sessions");
				
				uri.appendQueryParameter("auth_token", preferences.getString("AuthToken", ""));
				uri.appendQueryParameter("email", preferences.getString("email", ""));
				
				HttpDelete delete = new HttpDelete(uri.build().toString());
				
				// default return values
				json.put("success", false);
				json.put("info", "Logout Failed");
				Log.d("email", preferences.getString("email", ""));
				Log.d("AuthToken", preferences.getString("AuthToken",""));
				
				// request headers
				delete.setHeader("Accept", "application/json");
				delete.setHeader("Content-Type", "application/json");
				
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = client.execute(delete, responseHandler);
				json = new JSONObject(response);
			} catch (HttpResponseException e) {
				
			} catch (JSONException e) {
				
			} catch (IOException e) {
				
			}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			
			// delete the auth token on android
			// set need to log out to false
			try {
				if (json.getBoolean("success")) {
					Log.d("onPostExecute logout", "successfully logged out on server");
				} else{
					// keep the auth token, but set need to log out to true
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			super.onPostExecute(json);
		}
	}
	private class LoginTask extends UrlJsonAsyncTask {
		 public LoginTask(Context context) {
		        super(context);
		    }

	    @Override
	    protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        JSONObject userObj = new JSONObject();
	        String response = null;
	        JSONObject json = new JSONObject();

	        try {
	            try {
	                // setup the returned values in case
	                // something goes wrong
	                json.put("success", false);
	                json.put("info", "Something went wrong. Retry!");
	                // add the user email and password to
	                // the params
	                userObj.put("email", userEmail);
	                userObj.put("password", userPassword);
	                holder.put("user", userObj);
	                StringEntity se = new StringEntity(holder.toString());
	                post.setEntity(se);
	                
	                SharedPreferences.Editor editor = preferences.edit();
	                editor.putString("email", userEmail);
	                editor.commit();

	                // setup the request headers
	                post.setHeader("Accept", "application/json");
	                post.setHeader("Content-Type", "application/json");

	                ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                response = client.execute(post, responseHandler);
	                json = new JSONObject(response);

	            } catch (HttpResponseException e) {
	                e.printStackTrace();
	                Log.e("ClientProtocol", "" + e);
	                json.put("info", "Email and/or password are invalid. Retry!");
	            } catch (IOException e) {
	                e.printStackTrace();
	                Log.e("IO", "" + e);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	            Log.e("JSON", "" + e);
	        }

	        return json;
	    }

	    @Override
	    protected void onPostExecute(JSONObject json) {
	        try {
	            if (json.getBoolean("success")) {
	                // everything is ok
	                SharedPreferences.Editor editor = preferences.edit();
	                // save the returned auth_token into
	                // the SharedPreferences
	                editor.putString("AuthToken", json.getJSONObject("data").getString("auth_token"));
	                editor.commit();
	                
	                Log.d("onPostExecute authToken", json.getJSONObject("data").getString("auth_token"));

	                // launch the HomeActivity and close this one
	                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	                startActivity(intent);
	                finish();
	            }
	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_SHORT).show();
	        } catch (Exception e) {
	            // something went wrong: show a Toast
	            // with the exception message
	            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}

	
	/*
	 * Login fragment
	 */
	public static class LoginFragment extends Fragment {

		public LoginFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
		}
		
	}

}
